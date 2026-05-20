package com.gx.etl.operator.output;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * 目标表输出算子 - 使用流程绑定的表，字段映射通过 _tableData 配置
 * 直接使用 ETL 系统自身的数据库连接
 */
@Component
public class BoundTableOutputOperator extends AbstractOperator {

    @Resource
    private DataSource dataSource;

    @Override
    public String getId() { return "BoundTableOutput"; }

    @Override
    public String getName() { return "目标表输出"; }

    @Override
    public String getCategory() { return "output"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("boundTable", "目标表", OperatorSchema.PropertyType.SELECT, true)
                .property("commit", "提交数量", OperatorSchema.PropertyType.NUMBER)
                .property("truncateTable", "清空目标表", OperatorSchema.PropertyType.BOOLEAN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String tableName = ctx.getString("boundTable");
        if (tableName == null || tableName.isEmpty()) {
            return input;
        }

        String fullTableName = "\"" + tableName + "\"";

        Boolean truncate = ctx.getBoolean("truncateTable");
        int commitSize = ctx.getInt("commit") != null ? ctx.getInt("commit") : 1000;

        List<String> sourceColumns = input.getColumnNames();

        // 读取前端配置的字段映射
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");

        List<String> insertColumns = new ArrayList<>();
        List<String> mappedSourceColumns = new ArrayList<>();

        if (tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> mapping : tableData) {
                String targetCol = getString(mapping, "tableChartsName");
                String sourceCol = getString(mapping, "name");
                if (targetCol != null && !targetCol.isEmpty() && sourceCol != null && !sourceCol.isEmpty()) {
                    insertColumns.add(targetCol);
                    mappedSourceColumns.add(sourceCol);
                }
            }
        }

        try (Connection conn = dataSource.getConnection()) {
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
                        ps.setObject(i + 1, row.get(sourceCol));
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
            throw new RuntimeException("目标表输出执行失败: " + e.getMessage(), e);
        }

        return DataSet.empty();
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null && !v.toString().isEmpty() ? v.toString() : null;
    }
}
