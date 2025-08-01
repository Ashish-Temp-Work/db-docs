package com.dbanalyzer.model;

import java.util.List;

public class TableInfo {
    private String tableName;
    private String schema;
    private String comment;
    private List<ColumnInfo> columns;
    private List<IndexInfo> indexes;
    private List<ConstraintInfo> constraints;

    // Constructors
    public TableInfo() {}

    // Getters and Setters
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getSchema() { return schema; }
    public void setSchema(String schema) { this.schema = schema; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public List<ColumnInfo> getColumns() { return columns; }
    public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }

    public List<IndexInfo> getIndexes() { return indexes; }
    public void setIndexes(List<IndexInfo> indexes) { this.indexes = indexes; }

    public List<ConstraintInfo> getConstraints() { return constraints; }
    public void setConstraints(List<ConstraintInfo> constraints) { this.constraints = constraints; }
}
