package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Bindings;
import java.util.*;

@Component
public class JavaScriptCodeOperator extends AbstractOperator {

    @Override
    public String getId() { return "JavaScriptCode"; }

    @Override
    public String getName() { return "JavaScript代码"; }

    @Override
    public String getCategory() { return "script"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("script", "JavaScript代码", OperatorSchema.PropertyType.CODE, true)
                .property("outputField", "输出字段名", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String script = ctx.getString("script");
        String outputField = ctx.getString("outputField");

        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (outputField != null && !outputField.isEmpty() && !columns.contains(outputField)) {
            columns.add(outputField);
        }

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        if (engine == null) {
            engine = new ScriptEngineManager().getEngineByName("js");
        }
        if (engine == null) {
            throw new RuntimeException("JavaScript引擎不可用");
        }

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            try {
                Bindings bindings = engine.createBindings();
                // Expose row fields as variables
                for (Map.Entry<String, Object> entry : row.getFields().entrySet()) {
                    bindings.put(entry.getKey(), entry.getValue());
                }
                bindings.put("row", row);
                bindings.put("result", null);

                engine.eval(script, bindings);

                Object resultVal = bindings.get("result");
                if (outputField != null && !outputField.isEmpty()) {
                    newRow.put(outputField, resultVal);
                }
            } catch (Exception e) {
                if (outputField != null && !outputField.isEmpty()) {
                    newRow.put(outputField, "ERROR: " + e.getMessage());
                }
            }
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }
}
