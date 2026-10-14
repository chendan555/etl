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
import java.util.concurrent.atomic.AtomicBoolean;
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
                    stmt.execute("SET SCHEMA " + dmSchema);
                }
                for (TopicConfig topic : topics) {
                    String tableName = topic.getTableName();
                    if (tableName == null || tableName.isEmpty() || allInstanceIds.isEmpty()) continue;
                    try {
                        String deleteSql = "DELETE FROM " + tableName
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

    /** 引接并行线程数（任务按实例展开）。独立成键，避免影响流程批处理的 batch.execution.parallel-size */
    @Value("${scheme.ingest.parallel-size:${batch.execution.parallel-size:5}}")
    private int poolSize;

    /** 达梦数据表所在 schema：数据表已随库迁移至 CASIC_ASSESS（原 ETL schema 已废弃）。再迁移只改此配置，勿在代码里写死 schema 名 */
    @Value("${scheme.ingest.dm-schema:CASIC_ASSESS}")
    private String dmSchema;

    public void initProgress(Long schemeId) {
        // 进度按实例数计：每条明细(样本)的 YB_LIST 是逗号分隔的实例ID，汇总所有待处理明细的实例总数
        List<SchemeDetail> details = schemeDetailMapper.selectList(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .eq(SchemeDetail::getStatus, 0));
        int total = 0;
        for (SchemeDetail detail : details) {
            total += parseIdListRaw(detail.getYbList()).size();
        }
        log.info("initProgress: schemeId={}, detailCount={}, instanceTotal={}", schemeId, details.size(), total);
        ProcessProgress progress = new ProcessProgress(total);
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

        // 6. 预构建各主题的 INSERT 计划（SQL 与实例无关，整个方案复用一份）
        List<TopicInsertPlan> plans = buildTopicInsertPlans(topics, topicFieldsMap);
        if (plans.isEmpty()) {
            // 与原逻辑一致：无可用主题时不写入数据，明细直接按完成处理
            log.error("无可用主题(字段定义或目标表名缺失)，本次引接不写入数据: schemeId={}", schemeId);
        }

        // 7. 任务拍平到 (明细, 实例) 粒度并行：单实例几十万~百万行时，各实例互不阻塞
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        int taskCount = plans.isEmpty() ? 0
                : details.stream().mapToInt(d -> parseIdList(d.getYbList()).size()).sum();
        log.info("引接任务: schemeId={}, 明细{}条, 实例任务{}个, 并行线程{}",
                schemeId, details.size(), taskCount, poolSize);
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (SchemeDetail detail : details) {
            List<Long> instanceIds = parseIdList(detail.getYbList());
            if (plans.isEmpty() || instanceIds.isEmpty()) {
                if (instanceIds.isEmpty()) {
                    log.warn("明细无实例ID，直接标记完成: detailId={}, ybList={}", detail.getId(), detail.getYbList());
                }
                detail.setStatus(1);
                schemeDetailMapper.updateById(detail);
                continue;
            }
            // 该明细的所有实例都结束后统一回写明细状态（任一实例失败则整条明细标失败）
            AtomicInteger remaining = new AtomicInteger(instanceIds.size());
            AtomicBoolean failed = new AtomicBoolean(false);
            for (Long instanceId : instanceIds) {
                executor.submit(() -> {
                    try {
                        processInstance(detail, instanceId, plans, mysqlDs);
                    } catch (Exception e) {
                        log.error("处理实例失败: detailId={}, ybName={}, instanceId={}",
                                detail.getId(), detail.getYbName(), instanceId, e);
                        failed.set(true);
                    } finally {
                        if (progress != null) progress.incrementAndGet();
                        if (remaining.decrementAndGet() == 0) {
                            detail.setStatus(failed.get() ? 2 : 1);
                            schemeDetailMapper.updateById(detail);
                        }
                        latch.countDown();
                    }
                });
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("引接处理被中断: schemeId=" + schemeId, e);
        } finally {
            executor.shutdown();
        }

        // 8. 所有明细完成后，更新方案状态
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

    /** JDBC 批量执行大小：每累积这么多行 executeBatch 一次（减少网络往返） */
    private static final int INSERT_BATCH_SIZE = 5000;
    /** 提交窗口行数：每累积这么多行 commit 一次。事务有界（失败最多回滚一个窗口），又不必小批频繁刷盘 */
    private static final int COMMIT_ROWS = 50000;

    /**
     * 处理单个实例：对每个主题查MySQL、解包msgpack、批量写入达梦。
     * 并行粒度为实例，任务独享一对 MySQL/达梦连接；实例跑完所有主题后由调用方进度 +1。
     */
    private void processInstance(SchemeDetail detail, Long instanceId,
                                 List<TopicInsertPlan> plans, DatabaseSource mysqlDs) {
        // 连接MySQL data_collect
        DataSource mysqlDataSource = DynamicDataSourceManager.getInstance().getDataSource(
                mysqlDs.getId(), mysqlDs.getDbType(), mysqlDs.getHost(),
                mysqlDs.getPort() != null ? mysqlDs.getPort() : 3306,
                "data_collect", mysqlDs.getUsername(), mysqlDs.getPassword(), mysqlDs.getSid());

        // 连接达梦主库
        DataSource dmDataSource = getDmDataSource();

        int totalRows = 0;
        Connection mysqlConn = null;
        Connection dmConn = null;
        try {
            mysqlConn = mysqlDataSource.getConnection();
            dmConn = dmDataSource.getConnection();
            // 切到数据表所在 schema（引接连接账号默认 schema 不一定是数据表所在库）
            try (Statement stmt = dmConn.createStatement()) {
                stmt.execute("SET SCHEMA " + dmSchema);
            }
            // 关闭自动提交：批量 executeBatch，攒够提交窗口再 commit
            dmConn.setAutoCommit(false);

            int uncommitted = 0; // 已 executeBatch、尚未 commit 的行数
            for (TopicInsertPlan plan : plans) {
                // 表名 = t + 实例ID，只按 topic 条件过滤
                String tableName = "t" + instanceId;
                // 采集端的 t{实例ID} 表通常没有 topic 索引；引接按 topic 过滤，
                // 缺索引会全表扫描。此处由本系统在查询前自动补齐索引。
                ensureTopicIndex(mysqlConn, tableName);
                String querySql = "SELECT msg_info FROM " + tableName + " WHERE topic = ?";
                // MySQL 流式读取：百万行不会一次性载入内存
                try (PreparedStatement insertPs = dmConn.prepareStatement(plan.insertSql);
                     PreparedStatement ps = mysqlConn.prepareStatement(querySql,
                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                    ps.setFetchSize(Integer.MIN_VALUE);
                    ps.setString(1, plan.topicCode);
                    try (ResultSet rs = ps.executeQuery()) {
                        int pending = 0;
                        while (rs.next()) {
                            Object unpacked = tryUnpack(rs);
                            if (unpacked == null) {
                                log.warn("msgpack解包失败: instanceId={}, topic={}", instanceId, plan.topicCode);
                                continue;
                            }

                            // 映射字段值（可能一行，也可能因数组套数组展开成多行）
                            List<List<Object>> valueRows = mapToFieldRows(unpacked, plan.fields);

                            for (List<Object> values : valueRows) {
                                insertPs.setLong(1, instanceId);
                                for (int i = 0; i < values.size(); i++) {
                                    insertPs.setObject(i + 2, values.get(i));
                                }
                                insertPs.addBatch();
                                pending++;
                                if (pending >= INSERT_BATCH_SIZE) {
                                    insertPs.executeBatch();
                                    insertPs.clearBatch();
                                    totalRows += pending;
                                    uncommitted += pending;
                                    pending = 0;
                                    if (uncommitted >= COMMIT_ROWS) {
                                        dmConn.commit();
                                        uncommitted = 0;
                                    }
                                }
                            }
                        }
                        // 收尾：该主题剩余未满一批的数据先落库（是否 commit 由提交窗口决定）
                        if (pending > 0) {
                            insertPs.executeBatch();
                            insertPs.clearBatch();
                            totalRows += pending;
                            uncommitted += pending;
                        }
                    }
                }
            }
            // 实例所有主题写完，提交剩余窗口
            if (uncommitted > 0) {
                dmConn.commit();
            }
        } catch (Exception e) {
            // 回滚当前未提交窗口（已提交数据保留）
            if (dmConn != null) {
                try {
                    dmConn.rollback();
                } catch (Exception ignore) {
                    // 忽略回滚异常
                }
            }
            log.error("处理实例失败: detailId={}, instanceId={}", detail.getId(), instanceId, e);
            throw new RuntimeException("处理实例失败: " + e.getMessage(), e);
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
        log.info("实例处理完成: detailId={}, instanceId={}, 写入{}条", detail.getId(), instanceId, totalRows);
    }

    /** 预构建各主题的 INSERT 计划：字段定义或目标表名缺失的主题跳过并告警 */
    private List<TopicInsertPlan> buildTopicInsertPlans(List<TopicConfig> topics,
                                                        Map<Long, List<TopicField>> topicFieldsMap) {
        List<TopicInsertPlan> plans = new ArrayList<>();
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

            String insertSql = buildInsertSql(targetTable, fields);
            log.info("INSERT SQL: {}", insertSql);
            plans.add(new TopicInsertPlan(topic.getTopicCode(), insertSql, fields));
        }
        return plans;
    }

    /** 主题的写入准备信息：INSERT语句 + 字段定义（与实例无关，同一明细的所有实例复用） */
    private static class TopicInsertPlan {
        final String topicCode;
        final String insertSql;
        final List<TopicField> fields;

        TopicInsertPlan(String topicCode, String insertSql, List<TopicField> fields) {
            this.topicCode = topicCode;
            this.insertSql = insertSql;
            this.fields = fields;
        }
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

    /** 构建INSERT语句（注意：SCHEME_ID 列实际存的是实例ID，由执行时绑定） */
    private String buildInsertSql(String tableName, List<TopicField> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (SCHEME_ID");
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
