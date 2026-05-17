package com.gx.etl.operator.input;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExcelInputOperator extends AbstractOperator {

    @Override
    public String getId() { return "ExcelInput"; }

    @Override
    public String getName() { return "Excel输入"; }

    @Override
    public String getCategory() { return "input"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("filePath", "文件路径", OperatorSchema.PropertyType.STRING, true)
                .property("sheetIndex", "Sheet索引", OperatorSchema.PropertyType.NUMBER)
                .property("startRow", "起始行", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String filePath = ctx.getString("filePath");
        Integer sheetIndex = ctx.getInt("sheetIndex");
        Integer startRow = ctx.getInt("startRow");

        List<String> columns = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();
        boolean[] headerRead = {false};

        EasyExcel.read(filePath, new AnalysisEventListener<Map<Integer, String>>() {
            int rowNum = 0;

            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                rowNum++;
                if (startRow != null && rowNum < startRow) return;

                if (!headerRead[0]) {
                    data.values().forEach(v -> columns.add(v != null ? v : ""));
                    headerRead[0] = true;
                    return;
                }

                DataRow row = new DataRow();
                for (int i = 0; i < columns.size(); i++) {
                    row.put(columns.get(i), data.getOrDefault(i, ""));
                }
                rows.add(row);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {}
        }).sheet(sheetIndex != null ? sheetIndex : 0).doRead();

        return DataSet.of(columns, rows);
    }
}
