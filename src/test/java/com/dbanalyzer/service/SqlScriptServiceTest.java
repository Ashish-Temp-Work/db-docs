package com.dbanalyzer.service;

import com.dbanalyzer.model.ColumnInfo;
import com.dbanalyzer.model.TableInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SqlScriptServiceTest {

    @Mock
    private DatabaseMetadataService metadataService;

    @InjectMocks
    private SqlScriptService sqlScriptService;

    private TableInfo tableInfo;

    @BeforeEach
    void setUp() throws Exception {
        tableInfo = new TableInfo();
        tableInfo.setSchema("public");
        tableInfo.setTableName("users");

        ColumnInfo idColumn = new ColumnInfo();
        idColumn.setColumnName("id");
        idColumn.setDataType("INTEGER");
        idColumn.setPrimaryKey(true);
        idColumn.setNullable(false);

        ColumnInfo nameColumn = new ColumnInfo();
        nameColumn.setColumnName("name");
        nameColumn.setDataType("VARCHAR(255)");
        nameColumn.setNullable(true);

        tableInfo.setColumns(List.of(idColumn, nameColumn));
        tableInfo.setIndexes(Collections.emptyList());
        tableInfo.setConstraints(Collections.emptyList());

        when(metadataService.getTableInfo(anyLong(), any(), any())).thenReturn(tableInfo);
    }

    @Test
    void whenGenerateSqlScripts_thenReturnsZipFileWithSql() throws Exception {
        byte[] zipBytes = sqlScriptService.generateSqlScripts(1L, List.of("public.users"));

        assertNotNull(zipBytes);

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            var entry = zis.getNextEntry();
            assertNotNull(entry);
            assertEquals("users.sql", entry.getName());

            String content = new String(zis.readAllBytes());
            assertTrue(content.contains("CREATE TABLE public.users"));
            assertTrue(content.contains("id INTEGER NOT NULL"));
            assertTrue(content.contains("name VARCHAR(255)"));
            assertTrue(content.contains("ALTER TABLE public.users ADD PRIMARY KEY (id)"));
        }
    }
}
