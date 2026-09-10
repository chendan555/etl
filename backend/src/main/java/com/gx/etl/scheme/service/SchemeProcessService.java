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

    /** 已确认过 topic 索引的实例表，避免每次引接重复检查与 DDL */
    private static final Set<String> TOPIC_INDEX_ENSURED_TABLES = ConcurrentHashMap.newKeySet();

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
        // 批量写入批次大小：每累积 BATCH_SIZE 行 executeBatch + commit 一次
        final int BATCH_SIZE = 1000;
        Connection mysqlConn = null;
        Connection dmConn = null;
        try {
            mysqlConn = mysqlDataSource.getConnection();
            dmConn = dmDataSource.getConnection();
            // 确保使用 ETL schema
            try (Statement stmt = dmConn.createStatement()) {
                stmt.execute("SET SCHEMA ETL");
            }
            // 关闭自动提交：批量 executeBatch + 分批 commit，十几万级数据写入性能大幅提升
            dmConn.setAutoCommit(false);

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

                // 构建INSERT语句（同一主题只 prepare 一次，循环内复用）
                String insertSql = buildInsertSql(targetTable, detail.getId(), fields);
                log.info("INSERT SQL: {}", insertSql);

                try (PreparedStatement insertPs = dmConn.prepareStatement(insertSql)) {
                    int pending = 0;
                    for (Long instanceId : instanceIds) {
                        // 表名 = t + 实例ID，只按 topic 条件过滤
                        String tableName = "t" + instanceId;
                        // 采集端的 t{实例ID} 表通常没有 topic 索引；引接按 topic 过滤，
                        // 缺索引会全表扫描。此处由本系统在查询前自动补齐索引。
                        ensureTopicIndex(mysqlConn, tableName);
                        String querySql = "SELECT msg_info FROM " + tableName + " WHERE topic = ?";
                        // MySQL 流式读取：十几万行不会一次性载入内存
                        try (PreparedStatement ps = mysqlConn.prepareStatement(querySql,
                                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                            ps.setFetchSize(Integer.MIN_VALUE);
                            ps.setString(1, topic.getTopicCode());
                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    Object unpacked = tryUnpack(rs);
                                    if (unpacked == null) {
                                        log.warn("msgpack解包失败: instanceId={}, topic={}", instanceId, topic.getTopicCode());
                                        continue;
                                    }

                                    // 映射字段值（可能一行，也可能因数组套数组展开成多行）
                                    List<List<Object>> valueRows = mapToFieldRows(unpacked, fields);

                                    for (List<Object> values : valueRows) {
                                        insertPs.setLong(1, instanceId);
                                        for (int i = 0; i < values.size(); i++) {
                                            insertPs.setObject(i + 2, values.get(i));
                                        }
                                        insertPs.addBatch();
                                        pending++;
                                        if (pending >= BATCH_SIZE) {
                                            insertPs.executeBatch();
                                            insertPs.clearBatch();
                                            dmConn.commit();
                                            totalRows += pending;
                                            pending = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 收尾：提交剩余未满一批的数据
                    if (pending > 0) {
                        insertPs.executeBatch();
                        insertPs.clearBatch();
                        dmConn.commit();
                        totalRows += pending;
                    }
                }
            }
        } catch (Exception e) {
            // 回滚当前未提交批次（已提交批次保留）
            if (dmConn != null) {
                try {
                    dmConn.rollback();
                } catch (Exception ignore) {
                    // 忽略回滚异常
                }
            }
            log.error("处理明细失败: detailId={}", detail.getId(), e);
            throw new RuntimeException("处理明细失败: " + e.getMessage(), e);
        } finally {
            if (dmConn != null) {
                try {
                    dmConn.setAutoCommit(true);
                } catch (Exception ignore) {
                    // 忽略
                }
                try { dmConn.close(); } catch (Exception ignore) { }
            }
            if (mysqlConn != null) {
                try { mysqlConn.close(); } catch (Exception ignore) { }
            }
        }
        log.info("明细处理完成: detailId={}, ybName={}, 写入{}条", detail.getId(), detail.getYbName(), totalRows);
    }


    /**
     * 确保实例表按 topic 查询能走索引。
     * t{实例ID} 表由采集端创建/维护，通常没有 topic 索引；而引接查询是 WHERE topic = ?，
     * 缺索引会全表扫描（十几万行时每次查询都很慢）。因此由本系统在查询前自动补齐索引：
     * 先查 information_schema 判断是否已有 topic 索引，没有则 ALTER TABLE 添加 idx_topic。
     * 无权限或加索引失败时仅记录告警，不影响引接流程。
     */
    private void ensureTopicIndex(Connection mysqlConn, String tableName) {
        if (TOPIC_INDEX_ENSURED_TABLES.contains(tableName)) return;
        try {
            boolean hasTopicIndex;
            try (PreparedStatement ps = mysqlConn.prepareStatement(
                    "SELECT COUNT(*) FROM information_schema.statistics "
                            + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = 'topic'")) {
                ps.setString(1, tableName);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    hasTopicIndex = rs.getInt(1) > 0;
                }
            }
            if (!hasTopicIndex) {
                long startMs = System.currentTimeMillis();
                try (Statement st = mysqlConn.createStatement()) {
                    st.execute("ALTER TABLE `" + tableName + "` ADD INDEX idx_topic (topic)");
                }
                log.info("已为实例表 {} 自动创建 topic 索引，耗时 {}ms", tableName, System.currentTimeMillis() - startMs);
            }
            TOPIC_INDEX_ENSURED_TABLES.add(tableName);
        } catch (Exception e) {
            // 表不存在、无 DDL 权限等情况：仅告警，引接继续（退化为全表扫描）
            log.warn("为实例表 {} 自动创建 topic 索引失败，将按无索引查询: {}", tableName, e.getMessage());
        }
    }

    /**
     * 通用解包：先取原始字节（BLOB / latin1 文本均正确），再试 JDBC 文本通道还原。
     * 与 data-collect-web DataCollectService.tryUnpack 逻辑保持一致：
     * 1) rs.getBytes 原始字节直接解，必须消费完整 msgpack 才合法；
     * 2) 失败后 rs.getString 按列字符集取文本，再按 ISO-8859-1 还原字节解包，
     *    兼容「msgpack 字节被文本化后落库」的双重编码存储。
     */
    private Object tryUnpack(ResultSet rs) throws java.sql.SQLException {
        byte[] raw = rs.getBytes("msg_info");
        if (raw != null && raw.length > 0) {
            Object result = doUnpack(raw);
            if (result != null) return result;
        }
        String s = rs.getString("msg_info");
        if (s != null && !s.isEmpty()) {
            Object result = doUnpack(s.getBytes(StandardCharsets.ISO_8859_1));
            if (result != null) return result;
        }
        return null;
    }

    private Object doUnpack(byte[] bytes) {
        try {
            MessageUnpacker unpacker = org.msgpack.core.MessagePack.newDefaultUnpacker(bytes);
            if (!unpacker.hasNext()) return null;
            org.msgpack.value.Value v = unpacker.unpackValue();
            // 只有完整消费全部字节才算合法（避免双重编码残片被误判成功）
            if (unpacker.hasNext()) return null;
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
     * 按 T_TOPIC_FIELD 字段顺序生成待插入的行（支持一行或多行）。
     * 导入主题时每个字段已记录其在 msgpack 中的位置：
     * - simpleType：arrayIndex = 值在根数组中的下标
     * - complexType 展开子字段：arrayIndex = 父字段在根数组中的下标（该位置是嵌套数组），
     *   subIndex = 子字段在嵌套数组内的下标
     *
     * 复杂字段两种嵌套形态（按位置取子值，subIndex 对应）：
     * - 【情况 B】一层数组：[1,1,1,[1,1,1]] → 单个对象，取 nested[subIndex]，产出 1 行
     * - 【情况 A】数组套数组：[1,1,1,[[1,1,1],[1,1,1]]] → 多个对象，
     *   每个内层数组按 subIndex 取一个值，逐组产出多行（其余字段每行重复）
     *
     * 兼容旧数据（arrayIndex 为空）：退回按下标顺序取值，complexType 用 matchByType，单行。
     */
    @SuppressWarnings("unchecked")
    private List<List<Object>> mapToFieldRows(Object unpacked, List<TopicField> fields) {
        List<Object> dataList;
        if (unpacked instanceof List) {
            dataList = (List<Object>) unpacked;
        } else {
            dataList = Collections.singletonList(unpacked);
        }

        // 每个字段解析为「单值」或「多行值」；多行字段决定总行数
        List<Object> singleValues = new ArrayList<>();
        Map<Integer, List<Object>> multiValuesMap = new HashMap<>();
        int rowCount = 1;
        int dataIndex = 0;

        for (int fieldIdx = 0; fieldIdx < fields.size(); fieldIdx++) {
            TopicField field = fields.get(fieldIdx);
            Integer arrayIndex = field.getArrayIndex();
            boolean isComplex = "Y".equalsIgnoreCase(field.getComplexType());
            Integer subIndex = field.getSubIndex();

            if (arrayIndex == null) {
                // 旧格式：按下标顺序取值
                Object val = dataIndex < dataList.size() ? dataList.get(dataIndex) : null;
                if (isComplex && val instanceof List) {
                    val = matchByType((List<?>) val, field.getFieldType());
                }
                singleValues.add(val);
                dataIndex++;
                continue;
            }

            Object rootVal = arrayIndex < dataList.size() ? dataList.get(arrayIndex) : null;

            if (isComplex && subIndex != null && isNestedArray(rootVal)) {
                // 情况 A：数组套数组 → 多行，每个内层数组按 subIndex 取一个值
                List<?> groups = (List<?>) rootVal;
                List<Object> vals = new ArrayList<>(groups.size());
                for (Object group : groups) {
                    if (group instanceof List) {
                        List<?> inner = (List<?>) group;
                        vals.add(subIndex < inner.size() ? inner.get(subIndex) : null);
                    } else {
                        vals.add(null);
                    }
                }
                multiValuesMap.put(fieldIdx, vals);
                rowCount = Math.max(rowCount, vals.size());
                singleValues.add(null); // 占位，实际用 multiValuesMap
            } else if (isComplex && subIndex != null && rootVal instanceof List) {
                // 情况 B：一层数组 → 单行，取 nested[subIndex]
                List<?> nested = (List<?>) rootVal;
                singleValues.add(subIndex < nested.size() ? nested.get(subIndex) : null);
            } else {
                // simpleType 或非数组值：所有行共用同一值
                singleValues.add(rootVal);
            }
        }

        // 组装行：多行字段按行取，其余字段每行重复
        List<List<Object>> rows = new ArrayList<>(rowCount);
        for (int r = 0; r < rowCount; r++) {
            List<Object> row = new ArrayList<>(fields.size());
            for (int fieldIdx = 0; fieldIdx < fields.size(); fieldIdx++) {
                List<Object> multi = multiValuesMap.get(fieldIdx);
                if (multi != null) {
                    row.add(r < multi.size() ? multi.get(r) : null);
                } else {
                    row.add(singleValues.get(fieldIdx));
                }
            }
            rows.add(row);
        }
        return rows;
    }

    /** 是否为「数组套数组」（至少一个元素本身是数组）——用于区分多对象与单对象嵌套 */
    private boolean isNestedArray(Object value) {
        if (!(value instanceof List)) return false;
        List<?> list = (List<?>) value;
        if (list.isEmpty()) return false;
        for (Object item : list) {
            if (item instanceof List) return true;
        }
        return false;
    }

    /**
     * 在嵌套数组中按类型匹配取值。
     * 遍历数组，找到与目标类型匹配的元素。
     */
    private Object matchByType(List<?> nested, String targetType) {
        if (nested == null || nested.isEmpty()) return null;
        if (targetType == null || targetType.trim().isEmpty()) {
            return nested.isEmpty() ? null : nested.get(nested.size() - 1);
        }
        String t = targetType.trim().toLowerCase();
        for (Object item : nested) {
            if (item == null) continue;
            if ("string".equals(t) && item instanceof String) return item;
            if (("long".equals(t) || "int".equals(t) || "integer".equals(t)) && item instanceof Number) {
                return ((Number) item).longValue();
            }
            if (("double".equals(t) || "float".equals(t)) && item instanceof Number) {
                return ((Number) item).doubleValue();
            }
            if ("boolean".equals(t) && item instanceof Boolean) return item;
        }
        // 兜底：取最后一个非 null 元素
        for (int i = nested.size() - 1; i >= 0; i--) {
            if (nested.get(i) != null) return nested.get(i);
        }
        return null;
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
