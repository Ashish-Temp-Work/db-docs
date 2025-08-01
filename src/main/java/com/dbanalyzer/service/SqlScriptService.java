package com.dbanalyzer.service;

import com.dbanalyzer.model.TableInfo;
import com.dbanalyzer.model.ColumnInfo;
import com.dbanalyzer.model.IndexInfo;
import com.dbanalyzer.model.ConstraintInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SqlScriptService {

    private static final Logger logger = LoggerFactory.getLogger(SqlScriptService.class);

    @Autowired
    private DatabaseMetadataService metadataService;

    public byte[] generateSqlScripts(Long connectionId, List<String> selectedObjects) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String objectName : selectedObjects) {
                String[] parts = objectName.split("\\.");
                String schema = parts.length > 1 ? parts[0] : null;
                String tableName = parts.length > 1 ? parts[1] : parts[0];

                TableInfo tableInfo = metadataService.getTableInfo(connectionId, tableName, schema);
                String sql = generateCreateTableScript(tableInfo);

                ZipEntry entry = new ZipEntry(tableName + ".sql");
                zos.putNextEntry(entry);
                zos.write(sql.getBytes());
                zos.closeEntry();
            }
        }

        return baos.toByteArray();
    }

    private String generateCreateTableScript(TableInfo tableInfo) {
        StringBuilder sql = new StringBuilder();

        // CREATE TABLE
        sql.append("CREATE TABLE ");
        if (tableInfo.getSchema() != null && !tableInfo.getSchema().isEmpty()) {
            sql.append(tableInfo.getSchema()).append(".");
        }
        sql.append(tableInfo.getTableName()).append(" (\n");

        // Columns
        for (int i = 0; i < tableInfo.getColumns().size(); i++) {
            ColumnInfo column = tableInfo.getColumns().get(i);
            sql.append("    ").append(column.getColumnName())
                    .append(" ").append(column.getDataType());

            if (!column.isNullable()) {
                sql.append(" NOT NULL");
            }

            if (column.getDefaultValue() != null && !column.getDefaultValue().isEmpty()) {
                sql.append(" DEFAULT ").append(column.getDefaultValue());
            }

            if (i < tableInfo.getColumns().size() - 1) {
                sql.append(",");
            }
            sql.append("\n");
        }

        sql.append(");\n\n");

        // Primary Key
        List<ColumnInfo> pkColumns = tableInfo.getColumns().stream()
                .filter(ColumnInfo::isPrimaryKey)
                .toList();

        if (!pkColumns.isEmpty()) {
            sql.append("ALTER TABLE ");
            if (tableInfo.getSchema() != null && !tableInfo.getSchema().isEmpty()) {
                sql.append(tableInfo.getSchema()).append(".");
            }
            sql.append(tableInfo.getTableName()).append(" ADD PRIMARY KEY (");

            for (int i = 0; i < pkColumns.size(); i++) {
                sql.append(pkColumns.get(i).getColumnName());
                if (i < pkColumns.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(");\n\n");
        }

        // Indexes
        if (tableInfo.getIndexes() != null && !tableInfo.getIndexes().isEmpty()) {
            for (IndexInfo index : tableInfo.getIndexes()) {
                if (index.getIndexName() != null && !index.getIndexName().equalsIgnoreCase("PRIMARY")) {
                    sql.append("CREATE ");
                    if (index.isUnique()) {
                        sql.append("UNIQUE ");
                    }
                    sql.append("INDEX ").append(index.getIndexName())
                            .append(" ON ");

                    if (tableInfo.getSchema() != null && !tableInfo.getSchema().isEmpty()) {
                        sql.append(tableInfo.getSchema()).append(".");
                    }
                    sql.append(tableInfo.getTableName())
                            .append(" (").append(index.getColumnName()).append(");\n");
                }
            }
            sql.append("\n");
        }

        // Foreign Keys
        if (tableInfo.getConstraints() != null && !tableInfo.getConstraints().isEmpty()) {
            for (ConstraintInfo constraint : tableInfo.getConstraints()) {
                if ("FOREIGN KEY".equals(constraint.getConstraintType())) {
                    sql.append("ALTER TABLE ");
                    if (tableInfo.getSchema() != null && !tableInfo.getSchema().isEmpty()) {
                        sql.append(tableInfo.getSchema()).append(".");
                    }
                    sql.append(tableInfo.getTableName())
                            .append(" ADD CONSTRAINT ").append(constraint.getConstraintName())
                            .append(" FOREIGN KEY (").append(constraint.getColumnName())
                            .append(") REFERENCES ").append(constraint.getReferencedTable())
                            .append(" (").append(constraint.getReferencedColumn()).append(");\n");
                }
            }
        }

        return sql.toString();
    }
}