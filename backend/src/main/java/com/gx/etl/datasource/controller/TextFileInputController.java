package com.gx.etl.datasource.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 文本文件输入算子配置所需的API端点。
 * 为前端 TextFileInput 组件的字段获取提供数据。
 */
@RestController
@RequestMapping("/api/textFileInputController")
public class TextFileInputController {

    @PostMapping("/getFields")
    public Result<?> getFields(@RequestParam Map<String, Object> allParams) {
        try {
            String path = extractFilePath(allParams);
            if (path == null || path.trim().isEmpty()) {
                return Result.fail("文件路径不能为空");
            }

            File file = new File(path);
            if (!file.exists()) {
                return Result.fail("文件不存在: " + path);
            }

            String charset = getStringParam(allParams, "content.encoding", "UTF-8");
            String effectiveSeparator = getSeparatorParam(allParams);
            String headerStr = getStringParam(allParams, "content.header", "Y");
            boolean useHeader = "Y".equalsIgnoreCase(headerStr) || "true".equalsIgnoreCase(headerStr);
            String nrHeaderLinesStr = getStringParam(allParams, "content.nrHeaderLines", "1");
            int effectiveHeaderLines = 1;
            try { effectiveHeaderLines = Integer.parseInt(nrHeaderLinesStr); } catch (Exception ignored) {}

            List<Map<String, Object>> fields = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(charset)))) {
                List<String> headerNames = new ArrayList<>();
                int readLines = 0;

                String line;
                while ((line = br.readLine()) != null && readLines < Math.max(effectiveHeaderLines, 1)) {
                    String[] parts = split(line, effectiveSeparator);
                    if (readLines == 0 && useHeader) {
                        for (int i = 0; i < parts.length; i++) {
                            String name = parts[i].trim();
                            if (name.isEmpty()) {
                                name = "field" + (i + 1);
                            }
                            headerNames.add(name);
                        }
                    } else {
                        for (int i = 0; i < parts.length; i++) {
                            headerNames.add("field" + (i + 1));
                        }
                    }
                    readLines++;
                }

                if (headerNames.isEmpty()) {
                    for (int i = 1; i <= 10; i++) {
                        headerNames.add("field" + i);
                    }
                }

                for (String name : headerNames) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", name);
                    item.put("type", "VARCHAR");
                    fields.add(item);
                }

                return Result.ok(fields);
            }
        } catch (Exception e) {
            return Result.fail("读取文本文件字段失败: " + e.getMessage());
        }
    }

    private String extractFilePath(Map<String, Object> params) {
        String path = null;
        // 优先从 inputFiles.fileName 获取（JSON 请求体下是嵌套 Map）
        Object inputFiles = params.get("inputFiles");
        if (inputFiles instanceof Map) {
            Object fileName = ((Map<?, ?>) inputFiles).get("fileName");
            if (fileName != null) {
                path = extractFromArrayOrString(fileName);
            }
        }
        // form-data 拍平后是普通字符串参数（含旧字段名兼容）
        if (path == null || path.trim().isEmpty()) {
            path = firstNonBlank(
                    getStringParam(params, "inputFiles.fileName", null),
                    getStringParam(params, "fileName", null),
                    getStringParam(params, "filefield", null),
                    getStringParam(params, "filePath", null)
            );
        }
        return normalizePath(path);
    }

    /**
     * 还原被 JSON.stringify 包装过的路径：前端 FormData 对数组/对象会整体序列化，
     * 路径可能以 ["..."] 或 "..." 形式到达，且反斜杠被转义成 \\。
     */
    private String normalizePath(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        boolean jsonWrapped = false;
        if (s.startsWith("[")) {
            // 取 JSON 数组中的第一个字符串元素
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
        // JSON 包装过的才需要反转义；原始路径（单反斜杠）原样保留
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
                else sb.append(n); // \\ \" \/ 等
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 读取分隔符参数。getStringParam 会 trim，单独的空格会被裁掉，
     * 因此分隔符需要专用解析：空白串视为空格分隔，并支持"空格"/"space"/"tab"等别名。
     */
    private String getSeparatorParam(Map<String, Object> params) {
        Object raw = params.get("content.separator");
        if (raw == null) raw = params.get("separator");
        if (raw == null) return ",";
        String s = raw.toString();
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            // 非空但纯空白（如" "）→ 空格分隔；空串 → 默认逗号
            return s.isEmpty() ? "," : " ";
        }
        if ("空格".equals(trimmed) || "space".equalsIgnoreCase(trimmed)) return " ";
        if ("\\t".equals(trimmed) || "tab".equalsIgnoreCase(trimmed)) return "\t";
        return trimmed;
    }

    private String getStringParam(Map<String, Object> params, String key, String defaultValue) {
        Object value = params.get(key);
        if (value == null) return defaultValue;
        String str = value.toString().trim();
        return str.isEmpty() ? defaultValue : str;
    }

    private String extractFromArrayOrString(Object value) {
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (str.startsWith("[") && str.endsWith("]")) {
                str = str.substring(1, str.length() - 1).trim();
                if (str.startsWith("\"") && str.endsWith("\"")) {
                    str = str.substring(1, str.length() - 1);
                }
            }
            return str;
        }
        if (value instanceof List && !((List<?>) value).isEmpty()) {
            return ((List<?>) value).get(0).toString();
        }
        return value.toString();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) return value.trim();
        }
        return null;
    }

    private String[] split(String line, String separator) {
        if (line == null) {
            return new String[0];
        }
        if (" ".equals(separator)) {
            // 空格分隔：连续空格视为一个分隔符（对齐排版的数据更友好）
            String trimmed = line.trim();
            return trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        }
        if ("\t".equals(separator)) {
            return line.split("\t", -1);
        }
        return line.split(java.util.regex.Pattern.quote(separator), -1);
    }
}
