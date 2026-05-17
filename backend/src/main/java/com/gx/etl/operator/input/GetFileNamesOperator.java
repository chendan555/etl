package com.gx.etl.operator.input;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class GetFileNamesOperator extends AbstractOperator {
    @Override public String getId() { return "GetFileNames"; }
    @Override public String getName() { return "获取文件名"; }
    @Override public String getCategory() { return "input"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("directory", "目录路径", OperatorSchema.PropertyType.STRING, true)
                .property("fileMask", "文件通配符", OperatorSchema.PropertyType.STRING)
                .property("includeSubdirs", "包含子目录", OperatorSchema.PropertyType.BOOLEAN);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String directory = ctx.getString("directory");
        String fileMask = ctx.getString("fileMask");
        Boolean includeSubdirs = ctx.getBoolean("includeSubdirs");
        if (directory == null || directory.isEmpty()) return DataSet.empty();

        List<String> columns = Arrays.asList("filename", "path", "size", "lastModified");
        List<DataRow> rows = new ArrayList<>();

        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) return DataSet.empty();

        collectFiles(dir, fileMask, Boolean.TRUE.equals(includeSubdirs), rows);

        return DataSet.of(columns, rows);
    }

    private void collectFiles(File dir, String mask, boolean recursive, List<DataRow> rows) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isFile()) {
                if (mask == null || mask.isEmpty() || matchMask(f.getName(), mask)) {
                    DataRow row = new DataRow();
                    row.put("filename", f.getName());
                    row.put("path", f.getAbsolutePath());
                    row.put("size", f.length());
                    row.put("lastModified", new Date(f.lastModified()).toString());
                    rows.add(row);
                }
            } else if (f.isDirectory() && recursive) {
                collectFiles(f, mask, true, rows);
            }
        }
    }

    private boolean matchMask(String name, String mask) {
        String regex = mask.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        return name.matches(regex);
    }
}
