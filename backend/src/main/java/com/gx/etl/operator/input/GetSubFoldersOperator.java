package com.gx.etl.operator.input;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class GetSubFoldersOperator extends AbstractOperator {
    @Override public String getId() { return "GetSubFolders"; }
    @Override public String getName() { return "获取子目录"; }
    @Override public String getCategory() { return "input"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("directory", "目录路径", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String directory = ctx.getString("directory");
        if (directory == null || directory.isEmpty()) return DataSet.empty();

        List<String> columns = Arrays.asList("foldername", "path");
        List<DataRow> rows = new ArrayList<>();

        File dir = new File(directory);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    DataRow row = new DataRow();
                    row.put("foldername", f.getName());
                    row.put("path", f.getAbsolutePath());
                    rows.add(row);
                }
            }
        }
        return DataSet.of(columns, rows);
    }
}
