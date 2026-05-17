package com.gx.etl.operator.input;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Component
public class CsvFileInputOperator extends AbstractOperator {
    @Override public String getId() { return "CsvFileInput"; }
    @Override public String getName() { return "CSV文件输入"; }
    @Override public String getCategory() { return "input"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fileName", "文件路径", OperatorSchema.PropertyType.STRING, true)
                .property("delimiter", "分隔符", OperatorSchema.PropertyType.STRING)
                .property("encoding", "编码", OperatorSchema.PropertyType.STRING)
                .property("headerRow", "首行为标题", OperatorSchema.PropertyType.BOOLEAN);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fileName = ctx.getString("fileName");
        String delimiter = ctx.getString("delimiter");
        String encoding = ctx.getString("encoding");
        Boolean headerRow = ctx.getBoolean("headerRow");
        if (fileName == null || fileName.isEmpty()) return DataSet.empty();
        if (delimiter == null || delimiter.isEmpty()) delimiter = ",";
        if (encoding == null || encoding.isEmpty()) encoding = "UTF-8";

        try {
            List<String> columns = new ArrayList<>();
            List<DataRow> rows = new ArrayList<>();
            Charset charset = Charset.forName(encoding);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset))) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(java.util.regex.Pattern.quote(delimiter), -1);
                    if (first && Boolean.TRUE.equals(headerRow)) {
                        for (String p : parts) columns.add(p.trim());
                        first = false;
                        continue;
                    }
                    if (first) {
                        for (int i = 0; i < parts.length; i++) columns.add("col" + (i + 1));
                        first = false;
                    }
                    DataRow row = new DataRow();
                    for (int i = 0; i < Math.min(parts.length, columns.size()); i++) {
                        row.put(columns.get(i), parts[i].trim());
                    }
                    rows.add(row);
                }
            }
            return DataSet.of(columns, rows);
        } catch (Exception e) {
            throw new RuntimeException("CSV文件读取失败: " + e.getMessage(), e);
        }
    }
}
