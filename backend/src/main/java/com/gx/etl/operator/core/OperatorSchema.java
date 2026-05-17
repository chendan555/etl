package com.gx.etl.operator.core;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Schema definition for operator configuration.
 * Returned to the frontend for dynamic form rendering.
 */
public class OperatorSchema {

    private List<Property> properties = new ArrayList<>();

    public static OperatorSchema builder() {
        return new OperatorSchema();
    }

    public OperatorSchema property(String key, String label, PropertyType type, boolean required) {
        properties.add(new Property(key, label, type, required, null, null));
        return this;
    }

    public OperatorSchema property(String key, String label, PropertyType type) {
        properties.add(new Property(key, label, type, false, null, null));
        return this;
    }

    public OperatorSchema property(String key, String label, PropertyType type, List<Option> options) {
        properties.add(new Property(key, label, type, false, options, null));
        return this;
    }

    public OperatorSchema property(String key, String label, PropertyType type, String group) {
        properties.add(new Property(key, label, type, false, null, group));
        return this;
    }

    public OperatorSchema property(String key, String label, PropertyType type, boolean required, String httpUrl) {
        Property prop = new Property(key, label, type, required, null, null);
        prop.setHttpUrl(httpUrl);
        properties.add(prop);
        return this;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public enum PropertyType {
        STRING, NUMBER, BOOLEAN, SELECT, TEXTAREA, CODE, TABLE_COLUMN_MAP
    }

    public static class Property {
        private String key;
        private String label;
        private PropertyType type;
        private boolean required;
        private List<Option> options;
        private String group;
        private String httpUrl;

        public Property(String key, String label, PropertyType type, boolean required,
                        List<Option> options, String group) {
            this.key = key;
            this.label = label;
            this.type = type;
            this.required = required;
            this.options = options;
            this.group = group;
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public PropertyType getType() { return type; }
        public boolean isRequired() { return required; }
        public List<Option> getOptions() { return options; }
        public String getGroup() { return group; }
        public String getHttpUrl() { return httpUrl; }
        public void setHttpUrl(String httpUrl) { this.httpUrl = httpUrl; }
    }

    public static class Option {
        private String label;
        private Object value;

        public Option(String label, Object value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public Object getValue() { return value; }

        public static Option of(String label, Object value) {
            return new Option(label, value);
        }
    }
}
