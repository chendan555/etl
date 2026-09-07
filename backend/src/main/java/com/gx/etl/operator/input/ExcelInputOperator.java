package com.gx.etl.operator.input;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.io.File;
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
        String filePath = firstNonBlank(ctx.getString("filePath"),
                ctx.getString("filefield"), ctx.getString("fileName"));
        if (filePath != null && filePath.startsWith("[") && filePath.endsWith("]")) {
            filePath = filePath.substring(1, filePath.length() - 1).trim();
            if (filePath.startsWith("\"") && filePath.endsWith("\"")) {
                filePath = filePath.substring(1, filePath.length() - 1);
            }
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Excel输入文件路径不能为空，请先上传Excel文件");
        }
        final String resolvedFilePath = filePath;
        File file = new File(resolvedFilePath);
        if (!file.isFile()) {
            throw new IllegalArgumentException("Excel输入文件不存在: " + resolvedFilePath);
        }

        Integer sheetIndex = firstNonNull(getOptionalInt(ctx, "sheetIndex"),
                getOptionalInt(ctx, "index"), getOptionalInt(ctx, "sheetNo"),
                getOptionalInt(ctx, "sheetName"));
        Integer startRow = firstNonNull(getOptionalInt(ctx, "startRow"),
                getOptionalInt(ctx, "startrow"));
        Integer startColumn = firstNonNull(getOptionalInt(ctx, "startColumn"),
                getOptionalInt(ctx, "startcol"));
        int firstColumnIndex = Math.max(0, (startColumn != null ? startColumn : 1) - 1);

        List<String> columns = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();
        boolean[] headerRead = {false};

        EasyExcel.read(resolvedFilePath, new AnalysisEventListener<Map<Integer, String>>() {
            int rowNum = 0;

            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                rowNum++;
                int effectiveStartRow = startRow != null ? Math.max(1, startRow) : 1;
                if (rowNum < effectiveStartRow) return;

                if (!headerRead[0]) {
                    for (int i = firstColumnIndex; i < data.size(); i++) {
                        String value = data.get(i);
                        columns.add(value != null ? value : "");
                    }
                    headerRead[0] = true;
                    return;
                }

                DataRow row = new DataRow();
                for (int i = 0; i < columns.size(); i++) {
                    int sourceIndex = firstColumnIndex + i;
                    row.put(columns.get(i), data.getOrDefault(sourceIndex, ""));
                }
                rows.add(row);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {}
        }).sheet(sheetIndex != null ? sheetIndex : 0)
                .headRowNumber(0)
                .doRead();

        return DataSet.of(columns, rows);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) return value.trim();
        }
        return null;
    }

    private Integer firstNonNull(Integer... values) {
        for (Integer value : values) {
            if (value != null) return value;
        }
        return null;
    }

    private Integer getOptionalInt(OperatorContext ctx, String key) {
        String value = ctx.getString(key);
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
