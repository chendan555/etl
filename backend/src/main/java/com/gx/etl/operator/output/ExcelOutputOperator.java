package com.gx.etl.operator.output;

import com.alibaba.excel.EasyExcel;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExcelOutputOperator extends AbstractOperator {

    @Override
    public String getId() { return "ExcelOutput"; }

    @Override
    public String getName() { return "Excel输出"; }

    @Override
    public String getCategory() { return "output"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("filePath", "文件路径", OperatorSchema.PropertyType.STRING, true)
                .property("sheetName", "Sheet名称", OperatorSchema.PropertyType.STRING)
                .property("encoding", "编码", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String filePath = ctx.getString("filePath");
        String sheetName = ctx.getString("sheetName");
        if (sheetName == null || sheetName.isEmpty()) sheetName = "Sheet1";

        List<String> columns = input.getColumnNames();
        List<List<Object>> dataRows = new ArrayList<>();

        // Header row
        List<Object> header = new ArrayList<>(columns);
        dataRows.add(header);

        // Data rows
        for (DataRow row : input) {
            List<Object> dataRow = new ArrayList<>();
            for (String col : columns) {
                dataRow.add(row.get(col));
            }
            dataRows.add(dataRow);
        }

        EasyExcel.write(filePath).sheet(sheetName).doWrite(dataRows);
        return DataSet.empty();
    }
}
