package com.gx.etl.scheme.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gx.etl.config.DynamicDataSourceManager;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.mapper.DatabaseSourceMapper;
import com.gx.etl.scheme.entity.TopicConfig;
import com.gx.etl.scheme.entity.TopicField;
import com.gx.etl.scheme.mapper.TopicConfigMapper;
import com.gx.etl.scheme.mapper.TopicFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 从外部 MySQL 模型库获取主题清单并导入主题库。
 * 业务逻辑移植自 DataCollectWeb 的 DataCollectService（topics/structure）：
 * 模型库表结构 topic(name, type, remark)、simulatmutual(id, name)、dataitem(name, meaning, type, simulatmutual_id)。
 */
@Service
public class TopicImportService {

    private static final Logger log = LoggerFactory.getLogger(TopicImportService.class);

    /** 模型库名只能是字母/数字/下划线（会拼进反引号 SQL，防注入） */
    private static final Pattern MODEL_DB_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    @Resource
    private DatabaseSourceMapper databaseSourceMapper;

    @Resource
    private TopicConfigMapper topicConfigMapper;

    @Resource
    private TopicFieldMapper topicFieldMapper;

    /** 主数据源（本地达梦），用于为导入的主题创建数据表 */
    @Resource
    private javax.sql.DataSource primaryDataSource;

    /**
     * 预览：连接 MySQL 模型库，按关键字过滤后分页返回主题清单（字段结构只查当前页，避免主题多时慢）。
     *
     * @param addPrefix   主题别名加默认前缀 TOPIC_MSG_
     * @param stripPrefix 主题别名去掉原前缀（TOPIC_XX_）
     * @param keyword     关键字，匹配主题技术名/备注，空=全部
     * @return {records, total}
     */
    public Map<String, Object> preview(Long dbId, String modelDb, boolean addPrefix, boolean stripPrefix,
                                       String keyword, Integer pageIndex, Integer pageSize) {
        requireModelDb(modelDb);
        DatabaseSource ds = loadMysqlDatasource(dbId);
        DataSource dataSource = getDataSource(ds);
        int idx = pageIndex != null && pageIndex > 0 ? pageIndex : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<Map<String, Object>> rows = new ArrayList<>();
        long total = 0;
        try (Connection conn = dataSource.getConnection()) {
            List<Map<String, Object>> topics = fetchTopics(conn, modelDb);
            // 关键字过滤（技术名/备注，不区分大小写）
            List<Map<String, Object>> filtered = new ArrayList<>();
            for (Map<String, Object> topic : topics) {
                String name = topic.get("name") == null ? "" : String.valueOf(topic.get("name"));
                String remark = topic.get("remark") == null ? "" : String.valueOf(topic.get("remark"));
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String kw = keyword.trim().toLowerCase();
                    if (!name.toLowerCase().contains(kw) && !remark.toLowerCase().contains(kw)) {
                        continue;
                    }
                }
                filtered.add(topic);
            }
            total = filtered.size();
            // 分页切片，只为当前页取字段结构（每个主题一次查询）
            int from = Math.min((idx - 1) * size, filtered.size());
            int to = Math.min(from + size, filtered.size());
            for (Map<String, Object> topic : filtered.subList(from, to)) {
                rows.add(buildTopicRow(conn, modelDb, topic, addPrefix, stripPrefix));
            }
        } catch (java.sql.SQLException e) {
            log.error("获取MySQL主题失败: dbId=" + dbId + ", modelDb=" + modelDb, e);
            throw new IllegalStateException("获取MySQL主题失败: " + e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("total", total);
        return result;
    }

    /**
     * 导入：把 MySQL 模型库的主题写入主题库（按主题名去重更新），字段结构一并写入。
     *
     * @param rawNames   要导入的 MySQL 原始主题名（topic.name），null/空 = 全部
     * @param addPrefix  主题别名加默认前缀 TOPIC_MSG_
     * @param stripPrefix 主题别名去掉原前缀（TOPIC_XX_）
     * @return {imported, updated, failed:[{topicName, error}]}
     */
    public Map<String, Object> importTopics(Long dbId, String modelDb, boolean addPrefix, boolean stripPrefix,
                                            List<String> rawNames) {
        requireModelDb(modelDb);
        DatabaseSource ds = loadMysqlDatasource(dbId);
        DataSource dataSource = getDataSource(ds);

        Set<String> selected = null;
        if (rawNames != null && !rawNames.isEmpty()) {
            selected = new HashSet<>(rawNames);
        }

        int imported = 0;
        int updated = 0;
        List<Map<String, Object>> failed = new ArrayList<>();
        Set<String> usedTableNames = listUsedTableNames();

        try (Connection conn = dataSource.getConnection()) {
            List<Map<String, Object>> topics = fetchTopics(conn, modelDb);
            for (Map<String, Object> topic : topics) {
                String rawName = String.valueOf(topic.get("name"));
                if (selected != null && !selected.contains(rawName)) {
                    continue;
                }
                try {
                    Map<String, Object> row = buildTopicRow(conn, modelDb, topic, addPrefix, stripPrefix);
                    String topicName = (String) row.get("topicName");
                    String topicCode = (String) row.get("topicCode");
                    List<Map<String, Object>> fields = (List<Map<String, Object>>) row.get("fields");

                    TopicConfig existing = topicConfigMapper.selectOne(
                            new LambdaQueryWrapper<TopicConfig>().eq(TopicConfig::getTopicName, topicName)
                    );
                    TopicConfig topicConfig;
                    boolean isNew = false;
                    if (existing != null) {
                        existing.setTopicCode(topicCode);
                        existing.setUpdateTime(LocalDateTime.now());
                        topicConfigMapper.updateById(existing);
                        topicConfig = existing;
                        updated++;
                    } else {
                        topicConfig = new TopicConfig();
                        topicConfig.setTopicName(topicName);
                        topicConfig.setTopicCode(topicCode);
                        topicConfig.setTableName(generateTableName(topicCode, usedTableNames));
                        topicConfigMapper.insert(topicConfig);
                        usedTableNames.add(topicConfig.getTableName());
                        imported++;
                        isNew = true;
                    }

                    // 替换字段列表
                    topicFieldMapper.delete(
                            new LambdaQueryWrapper<TopicField>().eq(TopicField::getTopicId, topicConfig.getId())
                    );
                    for (int i = 0; i < fields.size(); i++) {
                        Map<String, Object> f = fields.get(i);
                        TopicField field = new TopicField();
                        field.setTopicId(topicConfig.getId());
                        field.setFieldName((String) f.get("fieldName"));
                        field.setFieldAlias((String) f.get("fieldAlias"));
                        field.setSortOrder(i);
                        // 存 fieldType 和 complexType 标记
                        field.setFieldType((String) f.getOrDefault("fieldType", "VARCHAR"));
                        Boolean isComplex = (Boolean) f.get("complexType");
                        field.setComplexType(Boolean.TRUE.equals(isComplex) ? "Y" : "N");
                        // 记录字段在 msgpack 根数组/嵌套数组中的位置引用，供引接时取值
                        Object arrIdx = f.get("arrayIndex");
                        Object subIdx = f.get("subIndex");
                        field.setArrayIndex(arrIdx instanceof Number ? ((Number) arrIdx).intValue() : null);
                        field.setSubIndex(subIdx instanceof Number ? ((Number) subIdx).intValue() : null);
                        topicFieldMapper.insert(field);
                    }

                    // 新导入的主题直接建数据表；已存在主题不重建，避免 DROP 丢数据
                    if (isNew && !topicConfig.getTableName().isEmpty() && !fields.isEmpty()) {
                        createDynamicTable(topicConfig.getTableName(), fields, topicName);
                    }
                } catch (Exception e) {
                    log.error("导入主题失败: {}", rawName, e);
                    Map<String, Object> fail = new HashMap<>();
                    fail.put("topicName", rawName);
                    fail.put("error", e.getMessage());
                    failed.add(fail);
                }
            }
        } catch (java.sql.SQLException e) {
            log.error("导入MySQL主题失败: dbId=" + dbId + ", modelDb=" + modelDb, e);
            throw new IllegalStateException("导入MySQL主题失败: " + e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("imported", imported);
        result.put("updated", updated);
        result.put("failed", failed);
        return result;
    }

    /**
     * 组装单个主题行：
     * 主题名称 = MySQL 备注汉字（remark，为空时回退别名）；主题别名 = 技术名（可加/去前缀）。
     */
    private Map<String, Object> buildTopicRow(Connection conn, String modelDb, Map<String, Object> topic,
                                              boolean addPrefix, boolean stripPrefix) throws java.sql.SQLException {
        String rawName = topic.get("name") == null ? "" : String.valueOf(topic.get("name"));
        String remark = topic.get("remark") == null ? "" : String.valueOf(topic.get("remark"));
        String topicCode = computeAlias(rawName, addPrefix, stripPrefix);
        String topicName = !remark.trim().isEmpty() ? remark.trim() : topicCode;
        List<Map<String, Object>> fields = fetchStructure(conn, modelDb, topic.get("type") == null ? null : String.valueOf(topic.get("type")));

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("rawName", rawName);
        item.put("topicName", topicName);
        item.put("topicCode", topicCode);
        item.put("remark", remark);
        item.put("fields", fields);
        item.put("fieldCount", fields.size());
        return item;
    }

    /** 计算主题别名：先按选项去掉原前缀 TOPIC_XX_，再按选项补默认前缀 TOPIC_MSG_（不重复添加） */
    private String computeAlias(String rawName, boolean addPrefix, boolean stripPrefix) {
        String base = rawName == null ? "" : rawName.trim();
        if (stripPrefix) {
            base = base.replaceFirst("(?i)^TOPIC_[^_]+_", "");
        }
        if (addPrefix && !base.toUpperCase().startsWith("TOPIC_MSG_")) {
            base = "TOPIC_MSG_" + base;
        }
        return base;
    }

    // ========== 私有方法：连接与查询（移植自 DataCollectWeb） ==========

    /** 主题清单：模型库.topic 的 name/type/remark */
    private List<Map<String, Object>> fetchTopics(Connection conn, String modelDb) throws java.sql.SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT name, type, remark FROM `" + modelDb + "`.topic ORDER BY name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("name", rs.getString(1));
                m.put("type", rs.getString(2));
                m.put("remark", rs.getString(3));
                list.add(m);
            }
        }
        return list;
    }

    /**
     * 字段结构：topic.type → simulatmutual.name → dataitem
     * 区分 simpleType（直接字段）和 complexType（嵌套对象，需通过 compdataitem 展开子字段）
     */
    private List<Map<String, Object>> fetchStructure(Connection conn, String modelDb, String type)
            throws java.sql.SQLException {
        List<Map<String, Object>> fields = new ArrayList<>();
        if (type == null || type.isEmpty()) {
            return fields;
        }
        // 查询 dataitem 时带出 instruction_type 和 compdata_id
        String sql = "SELECT b.name, b.meaning, b.type, b.instruction_type, b.compdata_id "
                + "FROM `" + modelDb + "`.simulatmutual a "
                + "LEFT JOIN `" + modelDb + "`.dataitem b ON a.id = b.simulatmutual_id "
                + "WHERE a.name = ?";
        Set<String> usedNames = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                int index = 0;
                int parentIndex = 0; // 父字段在 msgpack 根数组中的位置（simpleType 与 complexType 父字段各占一位）
                while (rs.next()) {
                    String name = rs.getString(1);
                    String meaning = rs.getString(2);
                    String fieldType = rs.getString(3);
                    String instrType = rs.getString(4);
                    String compdataId = rs.getString(5);

                    String fieldName = (name != null && !name.trim().isEmpty()) ? name.trim()
                            : ((meaning != null && !meaning.trim().isEmpty()) ? meaning.trim() : "field_" + index);
                    String fieldAlias = (meaning != null && !meaning.trim().isEmpty()) ? meaning.trim()
                            : (name == null ? "" : name.trim());

                    if ("complexType".equalsIgnoreCase(instrType)
                            && fieldType != null && !fieldType.trim().isEmpty()) {
                        // complexType：通过 compdataitem 展开子字段
                        // 注意：父字段（instruction_type='complexType'）自身的 compdata_id 为空，
                        // 子字段才有 compdata_id；因此此处只判断 instruction_type 与 type。
                        // msgpack 中该父字段位置是一个嵌套数组，展开的子字段共享父位置
                        // （arrayIndex=父位置），按在嵌套数组中的顺序记 subIndex。
                        int parentPos = parentIndex;
                        List<Map<String, Object>> subFields = fetchComplexSubFields(conn, modelDb, fieldType.trim());
                        int subIndex = 0;
                        for (Map<String, Object> sub : subFields) {
                            String subFieldName = fieldName + "_" + sub.get("fieldName");
                            String subFieldAlias = fieldAlias + "/" + sub.get("fieldAlias");
                            String unique = subFieldName;
                            int suffix = 2;
                            while (usedNames.contains(unique)) {
                                unique = subFieldName + "_" + suffix++;
                            }
                            usedNames.add(unique);
                            Map<String, Object> f = new LinkedHashMap<>();
                            f.put("fieldName", unique);
                            f.put("fieldAlias", subFieldAlias);
                            f.put("fieldType", sub.get("fieldType"));
                            f.put("complexType", true);
                            f.put("parentFieldName", fieldName);
                            f.put("arrayIndex", parentPos);
                            f.put("subIndex", subIndex++);
                            fields.add(f);
                            index++;
                        }
                    } else {
                        // simpleType：直接字段，值在 msgpack 根数组的 parentIndex 位置
                        String unique = fieldName;
                        int suffix = 2;
                        while (usedNames.contains(unique)) {
                            unique = fieldName + "_" + suffix++;
                        }
                        usedNames.add(unique);
                        Map<String, Object> f = new LinkedHashMap<>();
                        f.put("fieldName", unique);
                        f.put("fieldAlias", fieldAlias);
                        f.put("fieldType", fieldType);
                        f.put("complexType", false);
                        f.put("arrayIndex", parentIndex);
                        f.put("subIndex", null);
                        fields.add(f);
                        index++;
                    }
                    parentIndex++;
                }
            }
        }
        return fields;
    }

    /**
     * complexType 子字段展开：
     * dataitem.type → compdataitem.type 匹配 → compdataitem.id
     * → dataitem.compdata_id = compdataitem.id → 子字段
     */
    private List<Map<String, Object>> fetchComplexSubFields(Connection conn, String modelDb, String typeName)
            throws java.sql.SQLException {
        List<Map<String, Object>> subFields = new ArrayList<>();
        // 查 compdataitem 拿到 id（通过 type 匹配）
        String compId = null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM `" + modelDb + "`.compdataitem WHERE type = ?")) {
            ps.setString(1, typeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) compId = String.valueOf(rs.getInt(1));
            }
        }
        if (compId == null || compId.trim().isEmpty()) return subFields;

        // 用 compdataitem.id 回查 dataitem.compdata_id 匹配的子字段
        // 注意：dataitem 表没有 simu_id / id 列（列：simulatmutual_id, meaning, name,
        // type, instruction_type, compdata_id），因此不排序，按数据库自然顺序返回。
        String sql = "SELECT name, meaning, type FROM `" + modelDb + "`.dataitem "
                + "WHERE compdata_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, compId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> f = new LinkedHashMap<>();
                    f.put("fieldName", rs.getString(1));
                    f.put("fieldAlias", rs.getString(2));
                    f.put("fieldType", rs.getString(3));
                    subFields.add(f);
                }
            }
        }
        return subFields;
    }

    private DatabaseSource loadMysqlDatasource(Long dbId) {
        if (dbId == null) {
            throw new IllegalArgumentException("请选择MySQL数据源");
        }
        DatabaseSource ds = databaseSourceMapper.selectById(dbId);
        if (ds == null) {
            throw new IllegalArgumentException("数据源不存在: " + dbId);
        }
        if (!"MYSQL".equalsIgnoreCase(ds.getDbType())) {
            throw new IllegalArgumentException("仅支持MySQL数据源，当前数据源类型: " + ds.getDbType());
        }
        return ds;
    }

    private DataSource getDataSource(DatabaseSource ds) {
        int port = ds.getPort() != null ? ds.getPort() : 3306;
        return DynamicDataSourceManager.getInstance().getDataSource(
                ds.getId(), ds.getDbType(), ds.getHost(), port,
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid());
    }

    private void requireModelDb(String modelDb) {
        if (modelDb == null || modelDb.trim().isEmpty()) {
            throw new IllegalArgumentException("请填写模型库名");
        }
        if (!MODEL_DB_PATTERN.matcher(modelDb.trim()).matches()) {
            throw new IllegalArgumentException("模型库名只能包含字母、数字、下划线");
        }
    }

    /**
     * 在本地达梦为导入的主题建数据表（与手动保存主题的建表逻辑一致）：
     * SCHEME_ID + 各字段均为 VARCHAR(500)，并写表/列注释。
     */
    private void createDynamicTable(String tableName, List<Map<String, Object>> fields, String tableComment) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        sb.append("SCHEME_ID VARCHAR(500)");
        for (Map<String, Object> f : fields) {
            sb.append(", ").append(f.get("fieldName")).append(" VARCHAR(500)");
        }
        sb.append(")");
        try (Connection conn = primaryDataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // 达梦不支持 DROP TABLE IF EXISTS，先尝试删除
            try {
                stmt.execute("DROP TABLE " + tableName);
            } catch (Exception ignore) {
                // 表不存在时忽略
            }
            stmt.execute(sb.toString());
            stmt.execute("COMMENT ON TABLE " + tableName + " IS '" + tableComment + "'");
            for (Map<String, Object> f : fields) {
                Object alias = f.get("fieldAlias");
                if (alias != null && !alias.toString().isEmpty()) {
                    stmt.execute("COMMENT ON COLUMN " + tableName + "." + f.get("fieldName")
                            + " IS '" + alias + "'");
                }
            }
            log.info("导入主题建表成功: {}", tableName);
        } catch (java.sql.SQLException e) {
            log.error("导入主题建表失败: " + tableName, e);
            throw new IllegalStateException("建表失败: " + e.getMessage(), e);
        }
    }

    /** 生成数据表名：t_ + 小写净化主题名，避开已占用的表名 */
    private String generateTableName(String topicName, Set<String> usedTableNames) {        String base = "t_" + topicName.toLowerCase().replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_");
        if (base.length() > 120) {
            base = base.substring(0, 120);
        }
        String candidate = base;
        int suffix = 2;
        while (usedTableNames.contains(candidate)) {
            candidate = base + "_" + suffix++;
        }
        return candidate;
    }

    private Set<String> listUsedTableNames() {
        Set<String> names = new HashSet<>();
        for (TopicConfig t : topicConfigMapper.selectList(null)) {
            if (t.getTableName() != null && !t.getTableName().isEmpty()) {
                names.add(t.getTableName());
            }
        }
        return names;
    }
}
