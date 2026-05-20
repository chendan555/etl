package com.gx.etl.operator.input;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class TableInputOperator extends AbstractOperator {

    @Override
    public String getId() { return "TableInput"; }

    @Override
    public String getName() { return "表输入"; }

    @Override
    public String getCategory() { return "input"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("connection", "数据库连接", OperatorSchema.PropertyType.SELECT, true)
                .property("sql", "SQL语句", OperatorSchema.PropertyType.CODE, true)
                .property("table", "表名(用于获取字段注释)", OperatorSchema.PropertyType.STRING, false);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String connectionStr = ctx.getString("connection");
        Long connectionId;
        try {
            connectionId = Long.parseLong(connectionStr);
        } catch (NumberFormatException e) {
            // 前端传的是数据源名称而非ID，按名称查找
            connectionId = ctx.findDataSourceIdByName(connectionStr);
        }
        String sql = ctx.getString("sql");
        String tableName = ctx.getString("table");
        DataSource ds = ctx.getDataSource(connectionId);

        System.out.println("[TableInput] connectionStr=" + connectionStr + ", connectionId=" + connectionId);
        System.out.println("[TableInput] 执行SQL: " + sql);

        // MySQL 不支持双引号标识符，转换为反引号
        String dbType = ctx.getDataSourceDbType(connectionId);
        if ("MYSQL".equalsIgnoreCase(dbType)) {
            sql = sql.replace("\"", "`");
        }

        List<String> columns = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();
        Map<String, String> columnComments = new LinkedHashMap<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("[TableInput] 数据库连接: " + conn.getMetaData().getURL());

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                columns.add(meta.getColumnLabel(i));
            }

            // 获取字段注释
            if (tableName != null && !tableName.isEmpty()) {
                try {
                    DatabaseMetaData dbMeta = conn.getMetaData();
                    try (ResultSet colRs = dbMeta.getColumns(null, null, tableName.toUpperCase(), null)) {
                        while (colRs.next()) {
                            String colName = colRs.getString("COLUMN_NAME");
                            String remarks = colRs.getString("REMARKS");
                            if (remarks != null && !remarks.isEmpty()) {
                                columnComments.put(colName, remarks);
                            }
                        }
                    }
                    System.out.println("[TableInput] JDBC metadata获取注释: " + columnComments.size() + " 条");
                } catch (SQLException e) {
                    System.out.println("[TableInput] JDBC metadata获取注释失败: " + e.getMessage());
                }
                // DM数据库 fallback：通过系统视图获取注释
                if (columnComments.isEmpty()) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet crs = stmt.executeQuery(
                                 "SELECT COLUMN_NAME, COMMENTS FROM ALL_COL_COMMENTS WHERE TABLE_NAME = '"
                                         + tableName.toUpperCase() + "' AND COMMENTS IS NOT NULL")) {
                        while (crs.next()) {
                            columnComments.put(crs.getString("COLUMN_NAME"), crs.getString("COMMENTS"));
                        }
                        System.out.println("[TableInput] 系统视图获取注释: " + columnComments.size() + " 条");
                    } catch (SQLException e2) {
                        System.out.println("[TableInput] 系统视图获取注释失败: " + e2.getMessage());
                    }
                }
            }

            while (rs.next()) {
                DataRow row = new DataRow();
                for (int i = 1; i <= colCount; i++) {
                    row.put(columns.get(i - 1), rs.getObject(i));
                }
                rows.add(row);
            }
            System.out.println("[TableInput] 查询完成，返回 " + rows.size() + " 行");
        } catch (SQLException e) {
            throw new RuntimeException("表输入执行失败: " + e.getMessage(), e);
        }

        DataSet dataSet = DataSet.of(columns, rows);
        dataSet.setColumnComments(columnComments);
        return dataSet;
    }
}
