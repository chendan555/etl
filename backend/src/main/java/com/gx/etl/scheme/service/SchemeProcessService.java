package com.gx.etl.scheme.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gx.etl.config.DynamicDataSourceManager;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.mapper.DatabaseSourceMapper;
import com.gx.etl.scheme.entity.*;
import com.gx.etl.scheme.mapper.*;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SchemeProcessService {

    private static final Logger log = LoggerFactory.getLogger(SchemeProcessService.class);

    @Resource
    private SchemeDetailMapper schemeDetailMapper;
    @Resource
    private SchemeMapper schemeMapper;
    @Resource
    private CjConfigMapper cjConfigMapper;
    @Resource
    private TopicConfigMapper topicConfigMapper;
    @Resource
    private TopicFieldMapper topicFieldMapper;
    @Resource
    private DatabaseSourceMapper databaseSourceMapper;

    /** 进度缓存 */
    private final Map<Long, ProcessProgress> progressMap = new ConcurrentHashMap<>();

    public static class ProcessProgress {
        private final int total;
        private final AtomicInteger completed = new AtomicInteger(0);
        private volatile boolean finished = false;
        private volatile boolean running = false;

        public ProcessProgress(int total) { this.total = total; }
        public int getTotal() { return total; }
        public int getCompleted() { return completed.get(); }
        public boolean isFinished() { return finished; }
        public boolean isRunning() { return running; }
        public void setRunning(boolean running) { this.running = running; }
        public void setFinished(boolean finished) { this.finished = finished; }
        public int incrementAndGet() { return completed.incrementAndGet(); }
    }

    /**
     * 重新引接前：删除之前写入的数据，重置明细状态
     */
    public void deleteExistingData(Long schemeId) {
        log.info("删除旧引接数据: schemeId={}", schemeId);
        try {
            Scheme scheme = schemeMapper.selectById(schemeId);
            if (scheme == null || scheme.getCjListId() == null) return;
            CjConfig cjConfig = cjConfigMapper.selectById(scheme.getCjListId());
            if (cjConfig == null || cjConfig.getYbList() == null) return;

            List<Long> topicIds = parseIdList(cjConfig.getYbList());
            if (topicIds.isEmpty()) return;
            List<TopicConfig> topics = topicConfigMapper.selectBatchIds(topicIds);

            List<SchemeDetail> details = schemeDetailMapper.selectList(
                    new LambdaQueryWrapper<SchemeDetail>()
                            .eq(SchemeDetail::getSchemeId, schemeId));

            // 收集所有实例ID
            Set<String> allInstanceIds = new LinkedHashSet<>();
            for (SchemeDetail detail : details) {
                allInstanceIds.addAll(parseIdListRaw(detail.getYbList()));
            }

            // 删除每个目标表中的旧数据（表可能不存在，跳过）
            DataSource dmDataSource = getDmDataSource();
            try (Connection conn = dmDataSource.getConnection()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET SCHEMA ETL");
                }
                for (TopicConfig topic : topics) {
                    String tableName = topic.getTableName();
                    if (tableName == null || tableName.isEmpty() || allInstanceIds.isEmpty()) continue;
                    try {
                        String deleteSql = "DELETE FROM ETL." + tableName
                                + " WHERE SCHEME_ID IN (" + String.join(",", allInstanceIds) + ")";
                        log.info("DELETE旧数据: {}", deleteSql);
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(deleteSql);
                        }
                    } catch (Exception e) {
                        // 表不存在时跳过（首次引接时表还没建）
                        log.info("表不存在或删除失败，跳过: {}", tableName);
                    }
                }
            }

            // 重置状态
            for (SchemeDetail detail : details) {
                detail.setStatus(0);
                schemeDetailMapper.updateById(detail);
            }
            scheme.setStatus(0);
            schemeMapper.updateById(scheme);

            log.info("旧数据删除完成: schemeId={}", schemeId);
        } catch (Exception e) {
            log.error("删除旧引接数据失败: schemeId=" + schemeId, e);
            throw new RuntimeException("删除旧数据失败: " + e.getMessage(), e);
        }
    }

    private List<String> parseIdListRaw(String csv) {
        List<String> ids = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return ids;
        for (String s : csv.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) ids.add(s);
        }
        return ids;
    }

    public ProcessProgress getProgress(Long schemeId) {
        return progressMap.get(schemeId);
    }

    @Value("${batch.execution.parallel-size:5}")
    private int poolSize;

    public void initProgress(Long schemeId) {
        long count = schemeDetailMapper.selectCount(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .eq(SchemeDetail::getStatus, 0));
        log.info("initProgress: schemeId={}, count={}", schemeId, count);
        ProcessProgress progress = new ProcessProgress((int) count);
        progress.setRunning(true);
        progressMap.put(schemeId, progress);
    }

    /**
     * 异步执行引接处理
     */
    @Async
    public void processScheme(Long schemeId) {
        try {
            doProcess(schemeId);
        } catch (Exception e) {
            log.error("引接处理异常: schemeId=" + schemeId, e);
        } finally {
            ProcessProgress progress = progressMap.get(schemeId);
            if (progress != null) {
                progress.setFinished(true);
                progress.setRunning(false);
            }
        }
    }

    private void doProcess(Long schemeId) {
        // 1. 加载方案和采集配置
        Scheme scheme = schemeMapper.selectById(schemeId);
        if (scheme == null || scheme.getCjListId() == null) {
            log.error("方案或采集配置不存在: schemeId=" + schemeId);
            return;
        }
        CjConfig cjConfig = cjConfigMapper.selectById(scheme.getCjListId());
        if (cjConfig == null || cjConfig.getYbList() == null || cjConfig.getYbList().isEmpty()) {
            log.error("采集配置的YB_LIST为空: cjConfigId=" + scheme.getCjListId());
            return;
        }

        // 2. 加载主题列表（CjConfig.ybList = 逗号分隔的主题ID）
        List<Long> topicIds = parseIdList(cjConfig.getYbList());
        if (topicIds.isEmpty()) {
            log.error("无关联主题: ybList=" + cjConfig.getYbList());
            return;
        }
        List<TopicConfig> topics = topicConfigMapper.selectBatchIds(topicIds);
        log.info("加载主题: {}", topics.stream().map(t -> t.getTopicName() + "(" + t.getTopicCode() + ")").reduce((a, b) -> a + ", " + b).orElse(""));

        // 3. 为每个主题加载字段定义
        Map<Long, List<TopicField>> topicFieldsMap = new LinkedHashMap<>();
        for (TopicConfig topic : topics) {
            List<TopicField> fields = topicFieldMapper.selectList(
                    new LambdaQueryWrapper<TopicField>()
                            .eq(TopicField::getTopicId, topic.getId())
                            .orderByAsc(TopicField::getSortOrder));
            topicFieldsMap.put(topic.getId(), fields);
        }

        // 4. 获取MySQL data_collect数据源
        DatabaseSource mysqlDs = databaseSourceMapper.selectById(2L); // MySQL数据源id=2
        if (mysqlDs == null) {
            log.error("MySQL数据源不存在(id=2)");
            return;
        }

        // 5. 获取待处理的明细
        List<SchemeDetail> details = schemeDetailMapper.selectList(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .eq(SchemeDetail::getStatus, 0));
        log.info("待处理明细: schemeId={}, count={}", schemeId, details.size());

        if (details.isEmpty()) return;

        ProcessProgress progress = progressMap.get(schemeId);

        // 6. 多线程处理
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        CountDownLatch latch = new CountDownLatch(details.size());

        for (SchemeDetail detail : details) {
            executor.submit(() -> {
                try {
                    processDetail(detail, topics, topicFieldsMap, mysqlDs);
                    detail.setStatus(1);
                    schemeDetailMapper.updateById(detail);
                } catch (Exception e) {
                    log.error("处理明细失败: detailId={}, ybName={}", detail.getId(), detail.getYbName(), e);
                    detail.setStatus(2); // 失败标记
                    schemeDetailMapper.updateById(detail);
                } finally {
                    if (progress != null) progress.incrementAndGet();
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("引接处理被中断: schemeId=" + schemeId, e);
        } finally {
            executor.shutdown();
        }

        // 7. 所有明细完成后，更新方案状态
        long remaining = schemeDetailMapper.selectCount(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .eq(SchemeDetail::getStatus, 0));
        if (remaining == 0) {
            scheme.setStatus(1);
            schemeMapper.updateById(scheme);
            log.info("方案引接完成: schemeId={}", schemeId);
        }
    }

    /**
     * 处理单条明细：对每个实例ID × 每个主题，查MySQL、解包msgpack、写入达梦
     */
    private void processDetail(SchemeDetail detail, List<TopicConfig> topics,
                               Map<Long, List<TopicField>> topicFieldsMap,
                               DatabaseSource mysqlDs) {
        List<Long> instanceIds = parseIdList(detail.getYbList());
        if (instanceIds.isEmpty()) {
            log.warn("明细无实例ID: detailId={}, ybList={}", detail.getId(), detail.getYbList());
            return;
        }

        // 连接MySQL data_collect
        DataSource mysqlDataSource = DynamicDataSourceManager.getInstance().getDataSource(
                mysqlDs.getId(), mysqlDs.getDbType(), mysqlDs.getHost(),
                mysqlDs.getPort() != null ? mysqlDs.getPort() : 3306,
                "data_collect", mysqlDs.getUsername(), mysqlDs.getPassword(), mysqlDs.getSid());

        // 连接达梦主库
        DataSource dmDataSource = getDmDataSource();

        int totalRows = 0;
        try (Connection mysqlConn = mysqlDataSource.getConnection();
             Connection dmConn = dmDataSource.getConnection()) {
            // 确保使用 ETL schema
            try (Statement stmt = dmConn.createStatement()) {
                stmt.execute("SET SCHEMA ETL");
            }

            for (TopicConfig topic : topics) {
                List<TopicField> fields = topicFieldsMap.getOrDefault(topic.getId(), Collections.emptyList());
                if (fields.isEmpty()) {
                    log.warn("主题无字段定义: topicId={}, topicCode={}", topic.getId(), topic.getTopicCode());
                    continue;
                }

                String targetTable = topic.getTableName();
                if (targetTable == null || targetTable.isEmpty()) {
                    log.warn("主题无目标表名: topicId={}", topic.getId());
                    continue;
                }

                // 构建INSERT语句
                String insertSql = buildInsertSql(targetTable, detail.getId(), fields);
                log.info("INSERT SQL: {}", insertSql);

                for (Long instanceId : instanceIds) {
                    // 表名 = t + 实例ID，只按 topic 条件过滤
                    String tableName = "t" + instanceId;
                    String querySql = "SELECT msg_info FROM " + tableName
                            + " WHERE topic = ?";
                    try (PreparedStatement ps = mysqlConn.prepareStatement(querySql)) {
                            ps.setString(1, topic.getTopicCode());
                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    byte[] raw = rs.getBytes("msg_info");
                                    if (raw == null || raw.length == 0) continue;

                                    Object unpacked = tryUnpack(raw);
                                    if (unpacked == null) {
                                        log.warn("msgpack解包失败: instanceId={}, topic={}", instanceId, topic.getTopicCode());
                                        continue;
                                    }

                                    // 映射字段值
                                    List<Object> values = mapToFieldValues(unpacked, fields);

                                    // 写入达梦
                                    try (PreparedStatement insertPs = dmConn.prepareStatement(insertSql)) {
                                        insertPs.setLong(1, instanceId);
                                        for (int i = 0; i < values.size(); i++) {
                                            insertPs.setObject(i + 2, values.get(i));
                                        }
                                        insertPs.executeUpdate();
                                        totalRows++;

                                    }catch (Exception e){
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }

                }
            }
        } catch (Exception e) {
            log.error("处理明细失败: detailId={}", detail.getId(), e);
            throw new RuntimeException("处理明细失败: " + e.getMessage(), e);
        }
        log.info("明细处理完成: detailId={}, ybName={}, 写入{}条", detail.getId(), detail.getYbName(), totalRows);
    }


    /**
     * 通用解包：先取原始字节，再试 ISO-8859-1 文本还原，返回可 JSON 化的对象
     */
    private Object tryUnpack(byte[] raw) {
        // 第一次尝试：直接用原始字节解包
        Object result = doUnpack(raw);
        if (result != null) return result;

        // 第二次尝试：按 ISO-8859-1 还原字节再解包（兼容 latin1 文本存储）
        String s = new String(raw, StandardCharsets.ISO_8859_1);
        return doUnpack(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    private Object doUnpack(byte[] bytes) {
        try {
            MessageUnpacker unpacker = org.msgpack.core.MessagePack.newDefaultUnpacker(bytes);
            org.msgpack.value.Value v = unpacker.unpackValue();
            unpacker.close();
            return toObject(v);
        } catch (Exception e) {
            return null;
        }
    }

    /** Value → Java 对象（递归） */
    private Object toObject(org.msgpack.value.Value v) {
        if (v == null || v.isNilValue()) return null;
        if (v.isIntegerValue()) return v.asIntegerValue().toLong();
        if (v.isFloatValue()) return v.asFloatValue().toDouble();
        if (v.isBooleanValue()) return v.asBooleanValue();
        if (v.isStringValue()) return v.asStringValue().toString();
        if (v.isArrayValue()) {
            List<Object> list = new ArrayList<>();
            for (org.msgpack.value.Value item : v.asArrayValue()) {
                list.add(toObject(item));
            }
            return list;
        }
        if (v.isMapValue()) {
            Map<Object, Object> map = new LinkedHashMap<>();
            for (Map.Entry<org.msgpack.value.Value, org.msgpack.value.Value> entry : v.asMapValue().entrySet()) {
                map.put(toObject(entry.getKey()), toObject(entry.getValue()));
            }
            return map;
        }
        return v.toString();
    }

    /**
     * 按 T_TOPIC_FIELD 字段顺序，从解包数据（List）中按位置取值
     */
    @SuppressWarnings("unchecked")
    private List<Object> mapToFieldValues(Object unpacked, List<TopicField> fields) {
        List<Object> result = new ArrayList<>();
        List<Object> dataList;
        if (unpacked instanceof List) {
            dataList = (List<Object>) unpacked;
        } else {
            // 单值包装成列表
            dataList = Collections.singletonList(unpacked);
        }
        for (int i = 0; i < fields.size(); i++) {
            Object val = i < dataList.size() ? dataList.get(i) : null;
            result.add(val);
        }
        return result;
    }

    /** 构建INSERT语句 */
    private String buildInsertSql(String tableName, Long schemeId, List<TopicField> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append("ETL."+tableName).append(" (SCHEME_ID");
        for (TopicField f : fields) {
            sb.append(", ").append(f.getFieldName());
        }
        sb.append(") VALUES (?");
        for (int i = 0; i < fields.size(); i++) {
            sb.append(", ?");
        }
        sb.append(")");
        return sb.toString();
    }

    /** 获取达梦主库数据源 */
    public DataSource getDmDataSource() {
        DatabaseSource dmDs = databaseSourceMapper.selectById(1L); // 达梦数据源id=1
        if (dmDs == null) {
            throw new IllegalStateException("达梦数据源不存在(id=1)");
        }
        return DynamicDataSourceManager.getInstance().getDataSource(
                dmDs.getId(), dmDs.getDbType(), dmDs.getHost(),
                dmDs.getPort() != null ? dmDs.getPort() : 5236,
                dmDs.getDatabaseName(), dmDs.getUsername(), dmDs.getPassword(), dmDs.getSid());
    }

    /** 解析逗号分隔的ID列表 */
    private List<Long> parseIdList(String csv) {
        List<Long> ids = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return ids;
        for (String s : csv.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) {
                try {
                    ids.add(Long.parseLong(s));
                } catch (NumberFormatException e) {
                    log.warn("无效ID: {}", s);
                }
            }
        }
        return ids;
    }
}
