package com.gx.etl.operator.core;

import java.util.*;
import java.util.function.Predicate;

/**
 * Iterable dataset for streaming data between operators.
 * Supports both Iterator-based streaming and batch collection.
 */
public class DataSet implements Iterable<DataRow> {

    private List<String> columnNames;
    /** 字段注释：columnName -> comment */
    private Map<String, String> columnComments;
    private Iterator<DataRow> iterator;
    private List<DataRow> cachedRows;
    private boolean materialized;

    public DataSet(List<String> columnNames, Iterator<DataRow> iterator) {
        this.columnNames = columnNames != null ? columnNames : new ArrayList<>();
        this.columnComments = new LinkedHashMap<>();
        this.iterator = iterator;
        this.materialized = false;
    }

    public DataSet(List<String> columnNames, List<DataRow> rows) {
        this.columnNames = columnNames != null ? columnNames : new ArrayList<>();
        this.columnComments = new LinkedHashMap<>();
        this.cachedRows = rows;
        this.materialized = true;
    }

    public static DataSet empty() {
        return new DataSet(new ArrayList<>(), new ArrayList<>());
    }

    public static DataSet of(List<String> columnNames, List<DataRow> rows) {
        return new DataSet(columnNames, rows);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public Map<String, String> getColumnComments() {
        return columnComments;
    }

    public void setColumnComments(Map<String, String> columnComments) {
        this.columnComments = columnComments != null ? columnComments : new LinkedHashMap<>();
    }

    /** 设置单个字段注释 */
    public void setColumnComment(String columnName, String comment) {
        if (columnComments == null) {
            columnComments = new LinkedHashMap<>();
        }
        columnComments.put(columnName, comment);
    }

    /** 获取单个字段注释 */
    public String getColumnComment(String columnName) {
        return columnComments != null ? columnComments.get(columnName) : null;
    }

    @Override
    public Iterator<DataRow> iterator() {
        if (materialized) {
            return cachedRows.iterator();
        }
        return iterator;
    }

    /**
     * Materialize the entire dataset into memory (for Sort, Join, Unique etc.)
     */
    public List<DataRow> collect() {
        if (materialized) {
            return cachedRows;
        }
        cachedRows = new ArrayList<>();
        iterator.forEachRemaining(cachedRows::add);
        materialized = true;
        return cachedRows;
    }

    public long count() {
        if (materialized) {
            return cachedRows.size();
        }
        long count = 0;
        Iterator<DataRow> it = iterator;
        while (it.hasNext()) {
            count++;
            it.next();
        }
        return count;
    }

    public DataSet filter(Predicate<DataRow> predicate) {
        List<DataRow> result = new ArrayList<>();
        for (DataRow row : this) {
            if (predicate.test(row)) {
                result.add(row);
            }
        }
        DataSet ds = new DataSet(columnNames, result);
        ds.setColumnComments(this.columnComments);
        return ds;
    }

    public boolean isEmpty() {
        Iterator<DataRow> it = iterator();
        return !it.hasNext();
    }

    /** 是否已物化为内存列表（物化后可安全多次遍历） */
    public boolean isMaterialized() {
        return materialized;
    }
}
