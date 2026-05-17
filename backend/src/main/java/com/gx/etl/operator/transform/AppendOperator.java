package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

@Component
public class AppendOperator extends AbstractOperator {
    @Override public String getId() { return "Append"; }
    @Override public String getName() { return "追加"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("headStepname", "头步骤名", OperatorSchema.PropertyType.STRING)
                .property("tailStepname", "尾步骤名", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        return input;
    }
}
