package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

/**
 * 多源数据融合2算子
 * 与 MultisourceDataFuse 逻辑完全一致，支持双输入源融合。
 * 原始实现: gx-plugin-multisourcedatafuse2（结构与 Fuse 相同，仅类名不同）
 *
 * 本算子复用 MultisourceDataFuseOperator 的核心逻辑。
 */
@Component
public class MultisourceDataFuse2Operator extends AbstractOperator {

    private final MultisourceDataFuseOperator delegate = new MultisourceDataFuseOperator();

    @Override public String getId() { return "MultisourceDataFuse2"; }
    @Override public String getName() { return "多源数据融合2"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("joinType", "连接方式", OperatorSchema.PropertyType.SELECT,
                        java.util.Arrays.asList(
                                OperatorSchema.Option.of("INNER", "INNER"),
                                OperatorSchema.Option.of("FULL OUTER", "FULL OUTER")
                        ))
                .property("keyFields", "关联键字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("upstreamNames", "上游数据源名称(逗号分隔)", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        return delegate.execute(ctx, input);
    }
}
