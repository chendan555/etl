package com.gx.etl.operator.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A single row of data flowing between operators.
 */
public class DataRow {

    private final Map<String, Object> fields;

    public DataRow() {
        this.fields = new LinkedHashMap<>();
    }

    public DataRow(Map<String, Object> fields) {
        this.fields = new LinkedHashMap<>(fields);
    }

    public Object get(String fieldName) {
        return fields.get(fieldName);
    }

    public void put(String fieldName, Object value) {
        fields.put(fieldName, value);
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public void remove(String fieldName) {
        fields.remove(fieldName);
    }

    public DataRow copy() {
        return new DataRow(this.fields);
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}
