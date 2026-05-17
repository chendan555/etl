package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

@Component
public class UpdateOperator extends AbstractOperator {
    @Override public String getId() { return "Update"; }
    @Override public String getName() { return "更新"; }
    @Override public String getCategory() { return "output"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("connection", "数据库连接", OperatorSchema.PropertyType.SELECT, true)
                .property("tableName", "目标表名", OperatorSchema.PropertyType.STRING, true)
                .property("keyField", "关键字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        return input;
    }
}
