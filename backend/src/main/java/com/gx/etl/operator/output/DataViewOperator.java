package com.gx.etl.operator.output;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

@Component
public class DataViewOperator extends AbstractOperator {

    @Override
    public String getId() { return "DataView"; }

    @Override
    public String getName() { return "数据查看"; }

    @Override
    public String getCategory() { return "output"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // 数据查看组件直接透传上游数据
        return input;
    }
}
