package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ValidatorOperator extends AbstractOperator {
    @Override public String getId() { return "Validator"; }
    @Override public String getName() { return "数据校验"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("validationType", "校验类型(notNull/numeric/email/regex)", OperatorSchema.PropertyType.SELECT, true)
                .property("regex", "正则表达式", OperatorSchema.PropertyType.STRING)
                .property("errorField", "错误标识字段", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String type = ctx.getString("validationType");
        String regex = ctx.getString("regex");
        String errorField = ctx.getString("errorField");
        if (fieldName == null || type == null) return input;
        if (errorField == null || errorField.isEmpty()) errorField = "_validation_error";

        List<String> columns = input.getColumnNames();
        if (!columns.contains(errorField)) columns.add(errorField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            String error = null;
            switch (type.toLowerCase()) {
                case "notnull":
                    if (val == null || val.toString().isEmpty()) error = "值不能为空";
                    break;
                case "numeric":
                    if (val != null) {
                        try { Double.parseDouble(val.toString()); }
                        catch (Exception e) { error = "非数字"; }
                    }
                    break;
                case "email":
                    if (val != null && !val.toString().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
                        error = "邮箱格式错误";
                    break;
                case "regex":
                    if (val != null && regex != null && !val.toString().matches(regex))
                        error = "正则不匹配";
                    break;
            }
            newRow.put(errorField, error);
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
