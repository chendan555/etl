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
                .property("sql", "SQL语句", OperatorSchema.PropertyType.CODE, true);
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
        DataSource ds = ctx.getDataSource(connectionId);

        System.out.println("[TableInput] connectionStr=" + connectionStr + ", connectionId=" + connectionId);
        System.out.println("[TableInput] 执行SQL: " + sql);

        List<String> columns = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("[TableInput] 数据库连接: " + conn.getMetaData().getURL());

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                columns.add(meta.getColumnLabel(i));
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

        return DataSet.of(columns, rows);
    }
}
