package com.gx.etl.operator.output;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * 表输出算子 - 支持主键策略(自增/UUID16/UUID32)，批量插入，字段映射
 * 参考 Kettle TableOutputMeta 实现
 */
@Component
public class TableOutputOperator extends AbstractOperator {

    @Override
    public String getId() { return "TableOutput"; }

    @Override
    public String getName() { return "表输出"; }

    @Override
    public String getCategory() { return "output"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("connection", "数据库连接", OperatorSchema.PropertyType.SELECT, true)
                .property("schema", "模式名", OperatorSchema.PropertyType.STRING)
                .property("table", "目标表名", OperatorSchema.PropertyType.STRING, true)
                .property("commit", "提交数量", OperatorSchema.PropertyType.NUMBER)
                .property("truncateTable", "清空目标表", OperatorSchema.PropertyType.BOOLEAN)
                .property("keyStrategy", "主键策略", OperatorSchema.PropertyType.SELECT)
                .property("primaryKeyColumn", "主键字段", OperatorSchema.PropertyType.STRING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String connectionStr = ctx.getString("connection");
        String tableName = ctx.getString("table");
        if (tableName == null || tableName.isEmpty()) tableName = ctx.getString("tableName");
        String schema = ctx.getString("schema");

        if (connectionStr == null || connectionStr.isEmpty() || tableName == null || tableName.isEmpty()) {
            return input;
        }

        // DM 数据库需要 schema 前缀
        String fullTableName = (schema != null && !schema.isEmpty())
                ? "\"" + schema + "\".\"" + tableName + "\""
                : "\"" + tableName + "\"";

        Long connectionId;
        try {
            connectionId = Long.parseLong(connectionStr);
        } catch (NumberFormatException e) {
            connectionId = ctx.findDataSourceIdByName(connectionStr);
        }

        Boolean truncate = ctx.getBoolean("truncateTable");
        int commitSize = ctx.getInt("commit") != null ? ctx.getInt("commit") : 1000;
        String keyStrategy = ctx.getString("keyStrategy");
        String primaryKeyColumn = ctx.getString("primaryKeyColumn");

        DataSource ds = ctx.getDataSource(connectionId);
        List<String> sourceColumns = input.getColumnNames();

        // 读取前端配置的字段映射
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");

        // 构建映射：目标列名列表 + 对应的源列名列表
        List<String> insertColumns = new ArrayList<>();
        List<String> mappedSourceColumns = new ArrayList<>();

        if (tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> mapping : tableData) {
                // tableData 中: tableChartsName=目标列, name=源列
                String targetCol = getString(mapping, "tableChartsName");
                String sourceCol = getString(mapping, "name");
                if (targetCol != null && !targetCol.isEmpty() && sourceCol != null && !sourceCol.isEmpty()) {
                    insertColumns.add(targetCol);
                    mappedSourceColumns.add(sourceCol);
                }
            }
        }

        // 兼容旧格式：从 scysbg 读取
        if (insertColumns.isEmpty()) {
            List<Map<String, Object>> scysbg = (List<Map<String, Object>>) ctx.get("scysbg");
            if (scysbg != null && !scysbg.isEmpty()) {
                for (Map<String, Object> mapping : scysbg) {
                    String targetCol = getString(mapping, "fieldDatabase");
                    String sourceCol = getString(mapping, "fieldStream");
                    if (targetCol != null && !targetCol.isEmpty() && sourceCol != null && !sourceCol.isEmpty()) {
                        insertColumns.add(targetCol);
                        mappedSourceColumns.add(sourceCol);
                    }
                }
            }
        }

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            // 没有映射时，查目标表实际列名，按位置映射
            if (insertColumns.isEmpty()) {
                List<String> targetColumns = new ArrayList<>();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + fullTableName + " WHERE 1=0")) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        targetColumns.add(rsmd.getColumnName(i));
                    }
                }
                if (!targetColumns.isEmpty()) {
                    int count = Math.min(sourceColumns.size(), targetColumns.size());
                    for (int i = 0; i < count; i++) {
                        insertColumns.add(targetColumns.get(i));
                        mappedSourceColumns.add(sourceColumns.get(i));
                    }
                } else {
                    insertColumns = sourceColumns;
                    mappedSourceColumns = sourceColumns;
                }
            }

            if (Boolean.TRUE.equals(truncate)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("DELETE FROM " + fullTableName);
                }
            }

            // 处理主键策略
            boolean hasKeyStrategy = keyStrategy != null && !keyStrategy.isEmpty() && !"none".equals(keyStrategy);
            boolean hasPKCol = primaryKeyColumn != null && !primaryKeyColumn.isEmpty();

            // 如果有主键策略，确保主键列在插入列中
            if (hasKeyStrategy && hasPKCol) {
                if (!insertColumns.contains(primaryKeyColumn)) {
                    insertColumns.add(0, primaryKeyColumn);
                    mappedSourceColumns.add(0, "__auto_key__");
                }
            }

            StringBuilder sql = new StringBuilder("INSERT INTO ").append(fullTableName).append(" (");
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < insertColumns.size(); i++) {
                if (i > 0) { sql.append(", "); placeholders.append(", "); }
                sql.append("\"").append(insertColumns.get(i)).append("\"");
                placeholders.append("?");
            }
            sql.append(") VALUES (").append(placeholders).append(")");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int count = 0;
                for (DataRow row : input) {
                    for (int i = 0; i < insertColumns.size(); i++) {
                        String sourceCol = mappedSourceColumns.get(i);
                        if ("__auto_key__".equals(sourceCol)) {
                            // 自动生成主键
                            ps.setObject(i + 1, generateKey(keyStrategy));
                        } else {
                            ps.setObject(i + 1, row.get(sourceCol));
                        }
                    }
                    ps.addBatch();
                    count++;
                    if (count % commitSize == 0) {
                        ps.executeBatch();
                        conn.commit();
                    }
                }
                if (count % commitSize != 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }

            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("表输出执行失败: " + e.getMessage(), e);
        }

        return DataSet.empty();
    }

    /**
     * 根据策略生成主键值
     * none=无, auto_increment=自增, uuid16=UUID16位, uuid32=UUID32位
     */
    private Object generateKey(String strategy) {
        if (strategy == null) return null;
        switch (strategy.toLowerCase()) {
            case "auto_increment":
                return null; // 由数据库自增生成
            case "uuid16":
                return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            case "uuid32":
                return UUID.randomUUID().toString().replace("-", "");
            default:
                return null;
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null && !v.toString().isEmpty() ? v.toString() : null;
    }
}
