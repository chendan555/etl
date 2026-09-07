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
        // 优先取平铺键（旧配置），回退到"内容"页保存的 content.* 键（算子组件 dom 配置）
        String filePath = resolveFilePath(ctx);
        // 注意：分隔符不能走 firstNonBlank（会把纯空格当空值跳过）
        String sepRaw = ctx.getString("separator");
        if (sepRaw == null || sepRaw.isEmpty()) sepRaw = ctx.getString("content.separator");
        String separator = normalizeSeparator(sepRaw);
        String encoding = firstNonBlank(ctx.getString("encoding"), ctx.getString("content.encoding"));
        if (encoding == null || encoding.isEmpty()) encoding = "UTF-8";
        Boolean header = resolveHeader(ctx);
        if (header == null) header = true;

        List<String> columns = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName(encoding)))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] parts = splitLine(line, separator);
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

    /**
     * 解析文件路径：filePath → content.filePath → inputFiles 表格第一行 → fileOrPath；
     * 值可能被 JSON 序列化成 ["..."] 形式，统一还原。
     */
    private String resolveFilePath(OperatorContext ctx) {
        String path = unwrapJson(firstNonBlank(
                ctx.getString("filePath"),
                ctx.getString("content.filePath"),
                ctx.getString("fileOrPath")));
        if (path != null && !path.trim().isEmpty()) return path.trim();
        Object inputFiles = ctx.get("inputFiles");
        if (inputFiles instanceof List && !((List<?>) inputFiles).isEmpty()) {
            Object row = ((List<?>) inputFiles).get(0);
            if (row instanceof Map) {
                Object name = ((Map<?, ?>) row).get("inputFiles.fileName");
                if (name != null) {
                    String p = unwrapJson(name.toString());
                    if (p != null && !p.trim().isEmpty()) return p.trim();
                }
            }
        }
        return path;
    }

    private Boolean resolveHeader(OperatorContext ctx) {
        Boolean header = ctx.getBoolean("header");
        if (header != null) return header;
        String h = ctx.getString("content.header");
        if (h == null || h.isEmpty()) return null;
        return "Y".equalsIgnoreCase(h) || "true".equalsIgnoreCase(h);
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return null;
    }

    /** 还原被 JSON.stringify 包装的值：["..."] 或 "..." → 原始字符串并反转义 */
    private String unwrapJson(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        boolean jsonWrapped = false;
        if (s.startsWith("[")) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(s);
            if (m.find()) {
                return unescapeJson(m.group(1));
            }
            s = s.substring(1, s.length() - 1).trim();
            jsonWrapped = true;
        }
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
            jsonWrapped = true;
        }
        return jsonWrapped ? unescapeJson(s) : s;
    }

    private String unescapeJson(String s) {
        if (s.indexOf('\\') < 0) return s;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                if (n == 'n') sb.append('\n');
                else if (n == 't') sb.append('\t');
                else if (n == 'r') sb.append('\r');
                else sb.append(n);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 分隔符归一化：未配置默认逗号；纯空白配置视为空格分隔；
     * 支持"空格"/"space"/"tab"/"\t"等别名。
     */
    private String normalizeSeparator(String s) {
        if (s == null || s.isEmpty()) return ",";
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return " ";
        if ("空格".equals(trimmed) || "space".equalsIgnoreCase(trimmed)) return " ";
        if ("\\t".equals(trimmed) || "tab".equalsIgnoreCase(trimmed)) return "\t";
        return trimmed;
    }

    private String[] splitLine(String line, String separator) {
        if (" ".equals(separator)) {
            // 空格分隔：连续空格视为一个分隔符
            String trimmed = line.trim();
            return trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        }
        // quote 防止 | 等正则元字符被当作正则解析
        return line.split(java.util.regex.Pattern.quote(separator), -1);
    }
}
