package com.gx.etl.operator.input;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Component
public class TextFileInputOperator extends AbstractOperator {

    @Override
    public String getId() { return "TextFileInput"; }

    @Override
    public String getName() { return "文本文件输入"; }

    @Override
    public String getCategory() { return "input"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("filePath", "文件路径", OperatorSchema.PropertyType.STRING, true)
                .property("separator", "分隔符", OperatorSchema.PropertyType.STRING)
                .property("encoding", "编码", OperatorSchema.PropertyType.SELECT)
                .property("header", "包含表头", OperatorSchema.PropertyType.BOOLEAN);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String filePath = ctx.getString("filePath");
        String separator = ctx.getString("separator");
        if (separator == null || separator.isEmpty()) separator = ",";
        String encoding = ctx.getString("encoding");
        if (encoding == null || encoding.isEmpty()) encoding = "UTF-8";
        Boolean header = ctx.getBoolean("header");
        if (header == null) header = true;

        List<String> columns = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName(encoding)))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(separator, -1);
                if (firstLine && header) {
                    for (String p : parts) {
                        columns.add(p.trim());
                    }
                    firstLine = false;
                    continue;
                }
                if (firstLine) {
                    for (int i = 0; i < parts.length; i++) {
                        columns.add("col" + (i + 1));
                    }
                    firstLine = false;
                }
                DataRow row = new DataRow();
                for (int i = 0; i < Math.min(parts.length, columns.size()); i++) {
                    row.put(columns.get(i), parts[i].trim());
                }
                rows.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        }

        return DataSet.of(columns, rows);
    }
}
