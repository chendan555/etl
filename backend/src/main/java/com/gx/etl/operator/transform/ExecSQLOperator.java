package com.gx.etl.operator.transform;

import com.alibaba.fastjson.JSONArray;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * 执行SQL脚本算子 - 支持单次执行和逐行执行，支持参数绑定
 * 参考 Kettle ExecSQLMeta 实现
 */
@Component
public class ExecSQLOperator extends AbstractOperator {

    @Override
    public String getId() { return "ExecSQL"; }

    @Override
    public String getName() { return "执行SQL脚本"; }

    @Override
    public String getCategory() { return "script"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("connection", "数据库连接", OperatorSchema.PropertyType.SELECT, true)
                .property("sql", "SQL语句", OperatorSchema.PropertyType.CODE, true)
                .property("executedEachInputRow", "每行执行一次", OperatorSchema.PropertyType.BOOLEAN)
                .property("singleStatement", "单语句", OperatorSchema.PropertyType.BOOLEAN)
                .property("replaceVariables", "变量替换", OperatorSchema.PropertyType.BOOLEAN)
                .property("setParams", "设置参数", OperatorSchema.PropertyType.BOOLEAN)
                .property("insert_field", "插入状态字段", OperatorSchema.PropertyType.STRING)
                .property("update_field", "更新状态字段", OperatorSchema.PropertyType.STRING)
                .property("delete_field", "删除状态字段", OperatorSchema.PropertyType.STRING)
                .property("read_field", "读状态字段", OperatorSchema.PropertyType.STRING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String connectionStr = ctx.getString("connection");
        String sql = ctx.getString("sql");
        if (sql == null || sql.isEmpty()) return input;

        // 变量替换
        if ("Y".equalsIgnoreCase(ctx.getString("replaceVariables"))) {
            // 简单的系统属性替换
            for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
                sql = sql.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }

        Long connectionId = null;
        DataSource ds = null;
        if (connectionStr != null && !connectionStr.isEmpty()) {
            try {
                connectionId = Long.parseLong(connectionStr);
            } catch (NumberFormatException e) {
                connectionId = ctx.findDataSourceIdByName(connectionStr);
            }
            ds = ctx.getDataSource(connectionId);
        }

        boolean perRow = "Y".equalsIgnoreCase(ctx.getString("executedEachInputRow"));
        boolean setParams = "Y".equalsIgnoreCase(ctx.getString("setParams"));

        // 获取参数字段
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");
        List<String> argFields = new ArrayList<>();
        if (tableData != null) {
            for (Map<String, Object> td : tableData) {
                String name = getString(td, "name");
                if (name != null && !name.isEmpty()) argFields.add(name);
            }
        }

        // 获取结果状态字段
        String insertField = ctx.getString("insert_field");
        String updateField = ctx.getString("update_field");
        String deleteField = ctx.getString("delete_field");
        String readField = ctx.getString("read_field");

        if (ds == null) return input;

        try (Connection conn = ds.getConnection()) {
            if (perRow) {
                // 逐行执行
                List<String> columns = new ArrayList<>(input.getColumnNames());
                List<DataRow> result = new ArrayList<>();
                for (DataRow row : input) {
                    DataRow newRow = row.copy();
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        // 绑定参数
                        if (setParams && !argFields.isEmpty()) {
                            for (int i = 0; i < argFields.size(); i++) {
                                Object val = row.get(argFields.get(i));
                                ps.setObject(i + 1, val);
                            }
                        }
                        boolean hasResult = ps.execute();
                        if (hasResult) {
                            try (ResultSet rs = ps.getResultSet()) {
                                ResultSetMetaData meta = rs.getMetaData();
                                int colCount = meta.getColumnCount();
                                if (rs.next()) {
                                    if (readField != null && !readField.isEmpty()) {
                                        if (!columns.contains(readField)) columns.add(readField);
                                        newRow.put(readField, rs.getObject(1));
                                    }
                                }
                            }
                            setRowCountField(newRow, columns, readField, 1);
                        } else {
                            int updateCount = ps.getUpdateCount();
                            setRowCountField(newRow, columns, insertField, updateCount);
                        }
                    } catch (SQLException e) {
                        // 记录错误但继续执行
                        if (readField != null && !readField.isEmpty()) {
                            if (!columns.contains(readField)) columns.add(readField);
                            newRow.put(readField, "ERROR: " + e.getMessage());
                        }
                    }
                    result.add(newRow);
                }
                return DataSet.of(columns, result);
            } else {
                // 单次执行
                if (setParams && !argFields.isEmpty() && !input.isEmpty()) {
                    // 使用第一行数据绑定参数
                    DataRow firstRow = input.iterator().next();
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        for (int i = 0; i < argFields.size(); i++) {
                            Object val = firstRow.get(argFields.get(i));
                            ps.setObject(i + 1, val);
                        }
                        boolean hasResult = ps.execute();
                        if (hasResult) {
                            return buildResultFromResultSet(ps.getResultSet(), input);
                        }
                    }
                } else {
                    try (Statement stmt = conn.createStatement()) {
                        boolean hasResult = stmt.execute(sql);
                        if (hasResult) {
                            try (ResultSet rs = stmt.getResultSet()) {
                                return buildResultFromResultSet(rs, input);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL执行失败: " + e.getMessage(), e);
        }

        return input;
    }

    private DataSet buildResultFromResultSet(ResultSet rs, DataSet input) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= colCount; i++) {
            columns.add(meta.getColumnLabel(i));
        }
        List<DataRow> rows = new ArrayList<>();
        while (rs.next()) {
            DataRow row = new DataRow();
            for (int i = 1; i <= colCount; i++) {
                row.put(columns.get(i - 1), rs.getObject(i));
            }
            rows.add(row);
        }
        if (rows.isEmpty()) return input;
        return DataSet.of(columns, rows);
    }

    private void setRowCountField(DataRow row, List<String> columns, String field, Object value) {
        if (field != null && !field.isEmpty()) {
            if (!columns.contains(field)) columns.add(field);
            row.put(field, value);
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }
}
