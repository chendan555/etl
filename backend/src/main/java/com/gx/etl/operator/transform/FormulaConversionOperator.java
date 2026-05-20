package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FormulaConversionOperator extends AbstractOperator {
    @Override public String getId() { return "FormulaConversion"; }
    @Override public String getName() { return "公式转换"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("formula", "公式(用?占位，如: ?+?*2)", OperatorSchema.PropertyType.STRING, true)
                .property("mapFields", "字段映射(key=?, value=字段名列表)", OperatorSchema.PropertyType.STRING, true)
                .property("newFieldName", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String formula = ctx.getString("formula");
        String resultField = ctx.getString("newFieldName");
        if (formula == null || formula.isEmpty() || resultField == null || resultField.isEmpty()) {
            return input;
        }

        // 解析 mapFields: List<String> 字段名列表，按顺序对应?
        List<String> mapFields = (List<String>) ctx.get("mapFields");
        if (mapFields == null || mapFields.isEmpty()) return input;

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            try {
                String expr = formula;
                for (String col : mapFields) {
                    Object val = row.get(col);
                    String numStr = val != null ? val.toString() : "0";
                    expr = expr.replaceFirst("\\?", numStr);
                }
                newRow.put(resultField, evaluate(expr));
            } catch (Exception e) {
                newRow.put(resultField, null);
            }
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }

    private String expr;
    private int pos;

    private double evaluate(String expression) {
        this.expr = expression.replaceAll("\\s+", "");
        this.pos = 0;
        return parseExpr();
    }

    private double parseExpr() {
        double result = parseTerm();
        while (pos < expr.length()) {
            char op = expr.charAt(pos);
            if (op == '+') { pos++; result += parseTerm(); }
            else if (op == '-') { pos++; result -= parseTerm(); }
            else break;
        }
        return result;
    }

    private double parseTerm() {
        double result = parseFactor();
        while (pos < expr.length()) {
            char op = expr.charAt(pos);
            if (op == '*') { pos++; result *= parseFactor(); }
            else if (op == '/') { pos++; result /= parseFactor(); }
            else if (op == '%') { pos++; result %= parseFactor(); }
            else break;
        }
        return result;
    }

    private double parseFactor() {
        if (expr.charAt(pos) == '(') {
            pos++;
            double result = parseExpr();
            pos++; // skip ')'
            return result;
        }
        int start = pos;
        if (expr.charAt(pos) == '-') pos++;
        while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) pos++;
        return Double.parseDouble(expr.substring(start, pos));
    }
}
