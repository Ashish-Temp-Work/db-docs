package com.dbanalyzer.service;

import com.dbanalyzer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMetadataService.class);

    @Autowired
    private DatabaseConnectionService connectionService;

    public List<DatabaseObject> getDatabaseObjects(Long connectionId) throws Exception {
        DatabaseConnection dbConnection = connectionService.findById(connectionId);
        logger.debug("dbConnection is " + dbConnection);
        if (dbConnection == null) {
            throw new IllegalArgumentException("Connection not found");
        }

        List<DatabaseObject> objects = new ArrayList<>();

        try (Connection connection = connectionService.getConnection(dbConnection)) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = dbConnection.getSchema();

            // Get Tables
            try (ResultSet tables = metaData.getTables(null, schema, null, new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableSchema = tables.getString("TABLE_SCHEM");
                    String comment = tables.getString("REMARKS");
                    objects.add(new DatabaseObject(tableName, "TABLE", tableSchema, comment));
                }
            }

            // Get Views
            try (ResultSet views = metaData.getTables(null, schema, null, new String[]{"VIEW"})) {
                while (views.next()) {
                    String viewName = views.getString("TABLE_NAME");
                    String viewSchema = views.getString("TABLE_SCHEM");
                    String comment = views.getString("REMARKS");
                    objects.add(new DatabaseObject(viewName, "VIEW", viewSchema, comment));
                }
            }

            // Get Procedures
            try (ResultSet procedures = metaData.getProcedures(null, schema, null)) {
                while (procedures.next()) {
                    String procName = procedures.getString("PROCEDURE_NAME");
                    String procSchema = procedures.getString("PROCEDURE_SCHEM");
                    String comment = procedures.getString("REMARKS");
                    objects.add(new DatabaseObject(procName, "PROCEDURE", procSchema, comment));
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }

        return objects;
    }

    public TableInfo getTableInfo(Long connectionId, String tableName, String schema) throws Exception {
        DatabaseConnection dbConnection = connectionService.findById(connectionId);
        if (dbConnection == null) {
            throw new IllegalArgumentException("Connection not found");
        }

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setSchema(schema);

        try (Connection connection = connectionService.getConnection(dbConnection)) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Get Table Comment
            try (ResultSet tables = metaData.getTables(null, schema, tableName, new String[]{"TABLE"})) {
                if (tables.next()) {
                    tableInfo.setComment(tables.getString("REMARKS"));
                }
            }

            // Get Columns
            List<ColumnInfo> columns = new ArrayList<>();
            try (ResultSet columnsRs = metaData.getColumns(null, schema, tableName, null)) {
                while (columnsRs.next()) {
                    ColumnInfo column = new ColumnInfo();
                    column.setColumnName(columnsRs.getString("COLUMN_NAME"));
                    column.setDataType(columnsRs.getString("TYPE_NAME"));
                    column.setNullable(columnsRs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    column.setDefaultValue(columnsRs.getString("COLUMN_DEF"));
                    column.setComment(columnsRs.getString("REMARKS"));
                    column.setOrdinalPosition(columnsRs.getInt("ORDINAL_POSITION"));
                    columns.add(column);
                }
            }
            tableInfo.setColumns(columns);

            // Get Primary Keys
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, schema, tableName)) {
                while (primaryKeys.next()) {
                    String columnName = primaryKeys.getString("COLUMN_NAME");
                    columns.stream()
                            .filter(col -> col.getColumnName().equals(columnName))
                            .findFirst()
                            .ifPresent(col -> col.setPrimaryKey(true));
                }
            }

            // Get Indexes
            List<IndexInfo> indexes = new ArrayList<>();
            try (ResultSet indexInfo = metaData.getIndexInfo(null, schema, tableName, false, false)) {
                while (indexInfo.next()) {
                    IndexInfo index = new IndexInfo();
                    index.setIndexName(indexInfo.getString("INDEX_NAME"));
                    index.setColumnName(indexInfo.getString("COLUMN_NAME"));
                    index.setUnique(!indexInfo.getBoolean("NON_UNIQUE"));
                    index.setIndexType(indexInfo.getString("TYPE"));
                    indexes.add(index);
                }
            }
            tableInfo.setIndexes(indexes);

            // Get Foreign Keys
            List<ConstraintInfo> constraints = new ArrayList<>();
            try (ResultSet foreignKeys = metaData.getImportedKeys(null, schema, tableName)) {
                while (foreignKeys.next()) {
                    ConstraintInfo constraint = new ConstraintInfo();
                    constraint.setConstraintName(foreignKeys.getString("FK_NAME"));
                    constraint.setConstraintType("FOREIGN KEY");
                    constraint.setColumnName(foreignKeys.getString("FKCOLUMN_NAME"));
                    constraint.setReferencedTable(foreignKeys.getString("PKTABLE_NAME"));
                    constraint.setReferencedColumn(foreignKeys.getString("PKCOLUMN_NAME"));
                    constraints.add(constraint);
                }
            }
            tableInfo.setConstraints(constraints);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }

        return tableInfo;
    }
}