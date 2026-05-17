package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MergeRowsOperator extends AbstractOperator {
    @Override public String getId() { return "MergeRows"; }
    @Override public String getName() { return "记录合并"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("flagField", "标志字段", OperatorSchema.PropertyType.STRING)
                .property("keyFields", "关键字段(逗号分隔)", OperatorSchema.PropertyType.STRING)
                .property("valueFields", "值字段(逗号分隔)", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // 记录合并：直接透传，实际合并由引擎处理多上游
        return input;
    }
}
