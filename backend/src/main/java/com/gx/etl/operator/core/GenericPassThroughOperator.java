package com.gx.etl.operator.core;

/**
 * 通用透传算子，用于未注册的算子类型。
 * 直接将输入数据透传给下游，不执行任何操作。
 */
public class GenericPassThroughOperator extends AbstractOperator {

    private final String id;

    public GenericPassThroughOperator(String id) {
        this.id = id;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return id; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return null;
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        return input;
    }
}
