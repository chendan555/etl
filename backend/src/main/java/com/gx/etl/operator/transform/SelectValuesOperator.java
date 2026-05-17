package com.gx.etl.operator.transform;

import com.alibaba.fastjson.JSONArray;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 字段选择算子 - 选择/重命名/删除/转换字段
 * 参考 Kettle SelectValuesMeta 实现
 */
@Component
public class SelectValuesOperator extends AbstractOperator {

    @Override
    public String getId() { return "SelectValues"; }

    @Override
    public String getName() { return "字段选择"; }

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

        // 获取要删除的字段
        Object removeObj = ctx.get("remove");
        Set<String> removeFields = new HashSet<>();
        if (removeObj instanceof List) {
            for (Object item : (List<?>) removeObj) {
                removeFields.add(item.toString());
            }
        } else if (removeObj instanceof String && !((String) removeObj).isEmpty()) {
            try {
                JSONArray arr = JSONArray.parseArray((String) removeObj);
                for (int i = 0; i < arr.size(); i++) {
                    removeFields.add(arr.getString(i));
                }
            } catch (Exception e) {
                removeFields.add((String) removeObj);
            }
        }

        // 解析字段选择/重命名/类型转换配置
        List<FieldMeta> fieldMetas = new ArrayList<>();
        if (tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> td : tableData) {
                String name = getString(td, "name");
                if (name == null || name.isEmpty()) continue;
                FieldMeta meta = new FieldMeta();
                meta.name = name;
                meta.rename = getString(td, "rename");
                meta.type = getString(td, "type");
                meta.length = getInt(td, "length", -1);
                meta.precision = getInt(td, "precision", -1);
                meta.conversionMask = getString(td, "conversion_mask");
                meta.encoding = getString(td, "encoding");
                meta.decimalSymbol = getString(td, "decimal_symbol");
                meta.groupingSymbol = getString(td, "grouping_symbol");
                fieldMetas.add(meta);
            }
        }

        // 确定输出列
        List<String> newColumns;
        if (!fieldMetas.isEmpty()) {
            // 使用 tableData 中定义的字段顺序
            newColumns = new ArrayList<>();
            for (FieldMeta meta : fieldMetas) {
                String colName = (meta.rename != null && !meta.rename.isEmpty()) ? meta.rename : meta.name;
                newColumns.add(colName);
            }
        } else {
            // 没有 tableData，按 remove 过滤
            newColumns = new ArrayList<>();
            for (String col : input.getColumnNames()) {
                if (!removeFields.contains(col)) {
                    newColumns.add(col);
                }
            }
        }

        // 构建字段映射：原名 -> FieldMeta
        Map<String, FieldMeta> metaByName = new LinkedHashMap<>();
        for (FieldMeta meta : fieldMetas) {
            metaByName.put(meta.name, meta);
        }

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = new DataRow();
            if (!fieldMetas.isEmpty()) {
                for (FieldMeta meta : fieldMetas) {
                    Object val = row.get(meta.name);
                    String targetName = (meta.rename != null && !meta.rename.isEmpty()) ? meta.rename : meta.name;
                    // 类型转换
                    val = convertType(val, meta);
                    newRow.put(targetName, val);
                }
            } else {
                for (String col : input.getColumnNames()) {
                    if (!removeFields.contains(col)) {
                        newRow.put(col, row.get(col));
                    }
                }
            }
            result.add(newRow);
        }

        return DataSet.of(newColumns, result);
    }

    private Object convertType(Object val, FieldMeta meta) {
        if (val == null || meta.type == null || meta.type.isEmpty()) return val;
        String type = meta.type.toUpperCase();
        String strVal = val.toString();

        // 去除空字符处理
        if (meta.encoding != null) {
            switch (meta.encoding) {
                case "去除字符串首部空字符":
                    strVal = strVal.replaceAll("^\\s+", "");
                    break;
                case "去除字符串尾部空字符":
                    strVal = strVal.replaceAll("\\s+$", "");
                    break;
                case "去除字符串首尾空字符":
                    strVal = strVal.trim();
                    break;
            }
        }

        try {
            if (type.contains("INTEGER") || type.contains("INT") || type.equals("NUMBER") && meta.precision <= 0) {
                if (meta.length > 0 && meta.length < String.valueOf(Long.parseLong(strVal.split("\\.")[0])).length()) {
                    return Long.parseLong(strVal.split("\\.")[0]);
                }
                return strVal.contains(".") ? (long) Double.parseDouble(strVal) : Long.parseLong(strVal);
            } else if (type.contains("NUMBER") || type.contains("DOUBLE") || type.contains("FLOAT") || type.contains("DECIMAL")) {
                double d = Double.parseDouble(strVal);
                if (meta.precision >= 0) {
                    return BigDecimal.valueOf(d).setScale(meta.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                return d;
            } else if (type.contains("DATE") || type.contains("TIMESTAMP")) {
                if (meta.conversionMask != null && !meta.conversionMask.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat(meta.conversionMask);
                    if (val instanceof Date) {
                        return sdf.format((Date) val);
                    }
                    return sdf.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strVal));
                }
                return val;
            } else if (type.contains("STRING") || type.contains("VARCHAR")) {
                if (meta.length > 0 && strVal.length() > meta.length) {
                    return strVal.substring(0, meta.length);
                }
                return strVal;
            } else if (type.contains("BOOLEAN") || type.contains("BOOL")) {
                return "Y".equalsIgnoreCase(strVal) || "true".equalsIgnoreCase(strVal) || "1".equals(strVal);
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null && !v.toString().isEmpty() ? v.toString() : null;
    }

    private int getInt(Map<String, Object> map, String key, int def) {
        Object v = map.get(key);
        if (v == null) return def;
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return def; }
    }

    private static class FieldMeta {
        String name, rename, type, conversionMask, encoding, decimalSymbol, groupingSymbol;
        int length, precision;
    }
}
