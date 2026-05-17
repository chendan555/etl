package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

@Component
public class SetVariableOperator extends AbstractOperator {
    @Override public String getId() { return "SetVariable"; }
    @Override public String getName() { return "设置变量"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("variableName", "变量名", OperatorSchema.PropertyType.STRING, true)
                .property("variableValue", "变量值", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String variableName = ctx.getString("variableName");
        String variableValue = ctx.getString("variableValue");
        if (variableName != null) {
            System.setProperty(variableName, variableValue != null ? variableValue : "");
        }
        return input;
    }
}
