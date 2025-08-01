package com.dbanalyzer.service;

import com.dbanalyzer.model.DatabaseObject;
import com.dbanalyzer.model.TableInfo;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DataDictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DataDictionaryService.class);

    @Autowired
    private DatabaseMetadataService metadataService;

    public String generateMarkdownDictionary(Long connectionId, List<String> selectedObjects) throws Exception {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Database Data Dictionary\n\n");

        for (String objectName : selectedObjects) {
            // Assuming format: "schema.objectName"
            String[] parts = objectName.split("\\.");
            String schema = parts.length > 1 ? parts[0] : null;
            String tableName = parts.length > 1 ? parts[1] : parts[0];

            TableInfo tableInfo = metadataService.getTableInfo(connectionId, tableName, schema);

            markdown.append("## Table: ").append(tableName).append("\n\n");
            if (tableInfo.getComment() != null && !tableInfo.getComment().isEmpty()) {
                markdown.append("**Description:** ").append(tableInfo.getComment()).append("\n\n");
            }

            markdown.append("### Columns\n\n");
            markdown.append("| Column Name | Data Type | Nullable | Primary Key | Default Value | Comment |\n");
            markdown.append("|-------------|-----------|----------|-------------|---------------|----------|\n");

            for (var column : tableInfo.getColumns()) {
                markdown.append("| ").append(column.getColumnName())
                        .append(" | ").append(column.getDataType())
                        .append(" | ").append(column.isNullable() ? "YES" : "NO")
                        .append(" | ").append(column.isPrimaryKey() ? "YES" : "NO")
                        .append(" | ").append(column.getDefaultValue() != null ? column.getDefaultValue() : "")
                        .append(" | ").append(column.getComment() != null ? column.getComment() : "")
                        .append(" |\n");
            }

            if (!tableInfo.getIndexes().isEmpty()) {
                markdown.append("\n### Indexes\n\n");
                markdown.append("| Index Name | Column | Unique |\n");
                markdown.append("|------------|--------|--------|\n");

                for (var index : tableInfo.getIndexes()) {
                    markdown.append("| ").append(index.getIndexName())
                            .append(" | ").append(index.getColumnName())
                            .append(" | ").append(index.isUnique() ? "YES" : "NO")
                            .append(" |\n");
                }
            }

            if (!tableInfo.getConstraints().isEmpty()) {
                markdown.append("\n### Constraints\n\n");
                markdown.append("| Constraint Name | Type | Column | Referenced Table | Referenced Column |\n");
                markdown.append("|-----------------|------|--------|------------------|-------------------|\n");

                for (var constraint : tableInfo.getConstraints()) {
                    markdown.append("| ").append(constraint.getConstraintName())
                            .append(" | ").append(constraint.getConstraintType())
                            .append(" | ").append(constraint.getColumnName())
                            .append(" | ").append(constraint.getReferencedTable() != null ? constraint.getReferencedTable() : "")
                            .append(" | ").append(constraint.getReferencedColumn() != null ? constraint.getReferencedColumn() : "")
                            .append(" |\n");
                }
            }

            markdown.append("\n---\n\n");
        }

        return markdown.toString();
    }

    public byte[] generatePdfDictionary(Long connectionId, List<String> selectedObjects) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Database Data Dictionary").setBold().setFontSize(20));

            for (String objectName : selectedObjects) {
                String[] parts = objectName.split("\\.");
                String schema = parts.length > 1 ? parts[0] : null;
                String tableName = parts.length > 1 ? parts[1] : parts[0];

                TableInfo tableInfo = metadataService.getTableInfo(connectionId, tableName, schema);

                document.add(new Paragraph("Table: " + tableName).setBold().setFontSize(16));

                if (tableInfo.getComment() != null && !tableInfo.getComment().isEmpty()) {
                    document.add(new Paragraph("Description: " + tableInfo.getComment()));
                }

                // Columns table
                Table table = new Table(6);
                table.addHeaderCell(new Cell().add(new Paragraph("Column Name").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Data Type").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Nullable").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Primary Key").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Default Value").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Comment").setBold()));

                for (var column : tableInfo.getColumns()) {
                    table.addCell(new Cell().add(new Paragraph(column.getColumnName())));
                    table.addCell(new Cell().add(new Paragraph(column.getDataType())));
                    table.addCell(new Cell().add(new Paragraph(column.isNullable() ? "YES" : "NO")));
                    table.addCell(new Cell().add(new Paragraph(column.isPrimaryKey() ? "YES" : "NO")));
                    table.addCell(new Cell().add(new Paragraph(column.getDefaultValue() != null ? column.getDefaultValue() : "")));
                    table.addCell(new Cell().add(new Paragraph(column.getComment() != null ? column.getComment() : "")));
                }

                document.add(table);
                document.add(new Paragraph("\n"));
            }
        }

        return baos.toByteArray();
    }
}