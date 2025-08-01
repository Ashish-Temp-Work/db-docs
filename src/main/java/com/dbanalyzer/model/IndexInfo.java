package com.dbanalyzer.model;

public class IndexInfo {
    private String indexName;
    private String columnName;
    private boolean unique;
    private String indexType;

    // Constructors
    public IndexInfo() {}

    // Getters and Setters
    public String getIndexName() { return indexName; }
    public void setIndexName(String indexName) { this.indexName = indexName; }

    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }

    public boolean isUnique() { return unique; }
    public void setUnique(boolean unique) { this.unique = unique; }

    public String getIndexType() { return indexType; }
    public void setIndexType(String indexType) { this.indexType = indexType; }
}
