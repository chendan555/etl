package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Component
public class ColumnEncryptionOperator extends AbstractOperator {
    @Override public String getId() { return "ColumnEncryption"; }
    @Override public String getName() { return "字段加密"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("algorithm", "算法(MD5/SHA256)", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String algorithm = ctx.getString("algorithm");
        if (fieldName == null || fieldName.isEmpty()) return input;
        if (algorithm == null || algorithm.isEmpty()) algorithm = "MD5";

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            if (val != null) {
                try {
                    MessageDigest md = MessageDigest.getInstance(algorithm);
                    byte[] hash = md.digest(val.toString().getBytes(StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hash) sb.append(String.format("%02x", b));
                    newRow.put(fieldName, sb.toString());
                } catch (Exception e) {
                    // keep original
                }
            }
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
