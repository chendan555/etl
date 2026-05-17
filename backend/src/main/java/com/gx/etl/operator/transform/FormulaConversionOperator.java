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
                .property("formula", "公式(如: fieldA + fieldB * 2)", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String formula = ctx.getString("formula");
        String resultField = ctx.getString("resultField");
        if (formula == null || resultField == null) return input;

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            try {
                String expr = formula;
                for (String col : columns) {
                    Object val = row.get(col);
                    expr = expr.replace(col, val != null ? val.toString() : "0");
                }
                newRow.put(resultField, evaluateSimpleExpr(expr));
            } catch (Exception e) {
                newRow.put(resultField, null);
            }
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }

    private int exprPos;

    private double evaluateSimpleExpr(String expr) {
        expr = expr.replaceAll("\\s+", "");
        this.exprPos = 0;
        return parseExpr(expr);
    }

    private double parseExpr(String expr) {
        double result = parseTerm(expr);
        while (exprPos < expr.length()) {
            char op = expr.charAt(exprPos);
            if (op == '+') { exprPos++; result += parseTerm(expr); }
            else if (op == '-') { exprPos++; result -= parseTerm(expr); }
            else break;
        }
        return result;
    }

    private double parseTerm(String expr) {
        double result = parseFactor(expr);
        while (exprPos < expr.length()) {
            char op = expr.charAt(exprPos);
            if (op == '*') { exprPos++; result *= parseFactor(expr); }
            else if (op == '/') { exprPos++; result /= parseFactor(expr); }
            else break;
        }
        return result;
    }

    private double parseFactor(String expr) {
        if (expr.charAt(exprPos) == '(') {
            exprPos++;
            double result = parseExpr(expr);
            exprPos++;
            return result;
        }
        int start = exprPos;
        if (expr.charAt(exprPos) == '-') exprPos++;
        while (exprPos < expr.length() && (Character.isDigit(expr.charAt(exprPos)) || expr.charAt(exprPos) == '.')) exprPos++;
        return Double.parseDouble(expr.substring(start, exprPos));
    }
}
