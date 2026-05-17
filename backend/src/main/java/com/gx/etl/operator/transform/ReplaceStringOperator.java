package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串替换算子 - 支持正则/普通替换，整个单词匹配，大小写敏感
 * 参考 Kettle ReplaceStringMeta 实现
 */
@Component
public class ReplaceStringOperator extends AbstractOperator {

    @Override
    public String getId() { return "ReplaceString"; }

    @Override
    public String getName() { return "字符串替换"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");

        // 解析替换配置
        List<ReplaceDef> defs = new ArrayList<>();
        if (tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> td : tableData) {
                String inField = getString(td, "in_stream_name");
                if (inField == null || inField.isEmpty()) continue;
                ReplaceDef def = new ReplaceDef();
                def.inField = inField;
                def.outField = getString(td, "out_stream_name");
                if (def.outField == null || def.outField.isEmpty()) def.outField = inField;
                def.useRegex = "Y".equalsIgnoreCase(getString(td, "use_regex"));
                def.replaceString = getString(td, "replace_string");
                def.replaceByString = getString(td, "replace_by_string");
                def.setEmptyString = "Y".equalsIgnoreCase(getString(td, "set_empty_string"));
                def.replaceFieldByString = getString(td, "replace_field_by_string");
                def.wholeWord = "Y".equalsIgnoreCase(getString(td, "whole_word"));
                def.caseSensitive = "Y".equalsIgnoreCase(getString(td, "case_sensitive"));
                defs.add(def);
            }
        }

        // 兼容旧格式（单字段配置）
        if (defs.isEmpty()) {
            String field = ctx.getString("field");
            if (field != null && !field.isEmpty()) {
                ReplaceDef def = new ReplaceDef();
                def.inField = field;
                def.outField = field;
                def.useRegex = Boolean.TRUE.equals(ctx.getBoolean("useRegex"));
                def.replaceString = ctx.getString("search");
                def.replaceByString = ctx.getString("replace");
                def.caseSensitive = true;
                defs.add(def);
            }
        }

        if (defs.isEmpty()) return input;

        List<String> columns = new ArrayList<>(input.getColumnNames());
        // 确保输出字段在列中
        for (ReplaceDef def : defs) {
            if (!columns.contains(def.outField)) columns.add(def.outField);
        }

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            for (ReplaceDef def : defs) {
                Object val = row.get(def.inField);
                if (val == null) {
                    if (!def.outField.equals(def.inField)) newRow.put(def.outField, null);
                    continue;
                }
                String str = val.toString();

                if (def.setEmptyString) {
                    str = "";
                } else {
                    String replaceWith = def.replaceByString;
                    // 如果指定了 replaceFieldByString，用该字段的值作为替换内容
                    if (def.replaceFieldByString != null && !def.replaceFieldByString.isEmpty()) {
                        Object fieldVal = row.get(def.replaceFieldByString);
                        replaceWith = fieldVal != null ? fieldVal.toString() : "";
                    }

                    if (def.replaceString != null) {
                        if (def.useRegex) {
                            int flags = def.caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
                            if (def.wholeWord) {
                                String patternStr = "\\b" + Pattern.quote(def.replaceString) + "\\b";
                                str = Pattern.compile(patternStr, flags).matcher(str)
                                        .replaceAll(Matcher.quoteReplacement(replaceWith != null ? replaceWith : ""));
                            } else {
                                str = Pattern.compile(def.replaceString, flags).matcher(str)
                                        .replaceAll(Matcher.quoteReplacement(replaceWith != null ? replaceWith : ""));
                            }
                        } else {
                            if (def.wholeWord) {
                                int flags = def.caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
                                String patternStr = "\\b" + Pattern.quote(def.replaceString) + "\\b";
                                str = Pattern.compile(patternStr, flags).matcher(str)
                                        .replaceAll(Matcher.quoteReplacement(replaceWith != null ? replaceWith : ""));
                            } else {
                                if (def.caseSensitive) {
                                    str = str.replace(def.replaceString, replaceWith != null ? replaceWith : "");
                                } else {
                                    str = Pattern.compile(Pattern.quote(def.replaceString), Pattern.CASE_INSENSITIVE)
                                            .matcher(str).replaceAll(Matcher.quoteReplacement(replaceWith != null ? replaceWith : ""));
                                }
                            }
                        }
                    }
                }
                newRow.put(def.outField, str);
            }
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null && !v.toString().isEmpty() ? v.toString() : null;
    }

    private static class ReplaceDef {
        String inField, outField, replaceString, replaceByString, replaceFieldByString;
        boolean useRegex, setEmptyString, wholeWord, caseSensitive;
    }
}
