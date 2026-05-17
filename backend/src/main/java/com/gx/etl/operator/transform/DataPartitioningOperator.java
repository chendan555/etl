package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataPartitioningOperator extends AbstractOperator {
    @Override public String getId() { return "DataPartitioning"; }
    @Override public String getName() { return "数据分区"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("partitionSize", "每分区行数", OperatorSchema.PropertyType.NUMBER, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // 数据分区只返回第一个分区（简化实现）
        Integer size = ctx.getInt("partitionSize");
        if (size == null || size <= 0) return input;

        List<DataRow> rows = new ArrayList<>();
        int count = 0;
        for (DataRow row : input) {
            if (count >= size) break;
            rows.add(row);
            count++;
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
