package com.dbanalyzer.model;

public class DatabaseObject {
    private String name;
    private String type;
    private String schema;
    private String comment;
    private boolean selected;

    public DatabaseObject() {}

    public DatabaseObject(String name, String type, String schema, String comment) {
        this.name = name;
        this.type = type;
        this.schema = schema;
        this.comment = comment;
        this.selected = false;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSchema() { return schema; }
    public void setSchema(String schema) { this.schema = schema; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}