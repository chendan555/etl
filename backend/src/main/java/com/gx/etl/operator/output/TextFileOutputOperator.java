package com.gx.etl.operator.output;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

@Component
public class TextFileOutputOperator extends AbstractOperator {
    @Override public String getId() { return "TextFileOutput"; }
    @Override public String getName() { return "文本文件输出"; }
    @Override public String getCategory() { return "output"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fileName", "文件路径", OperatorSchema.PropertyType.STRING, true)
                .property("delimiter", "分隔符", OperatorSchema.PropertyType.STRING)
                .property("encoding", "编码", OperatorSchema.PropertyType.STRING)
                .property("append", "追加模式", OperatorSchema.PropertyType.BOOLEAN);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fileName = ctx.getString("fileName");
        String delimiter = ctx.getString("delimiter");
        String encoding = ctx.getString("encoding");
        Boolean append = ctx.getBoolean("append");
        if (fileName == null || fileName.isEmpty()) return input;
        if (delimiter == null || delimiter.isEmpty()) delimiter = ",";
        if (encoding == null || encoding.isEmpty()) encoding = "UTF-8";

        List<String> columns = input.getColumnNames();

        try {
            Charset charset = Charset.forName(encoding);
            boolean appendMode = Boolean.TRUE.equals(append);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, appendMode), charset))) {
                // 写标题行
                if (!appendMode) {
                    bw.write(String.join(delimiter, columns));
                    bw.newLine();
                }
                for (DataRow row : input) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < columns.size(); i++) {
                        if (i > 0) sb.append(delimiter);
                        Object val = row.get(columns.get(i));
                        sb.append(val != null ? val.toString() : "");
                    }
                    bw.write(sb.toString());
                    bw.newLine();
                }
            }
            return input;
        } catch (Exception e) {
            throw new RuntimeException("文本文件输出失败: " + e.getMessage(), e);
        }
    }
}
