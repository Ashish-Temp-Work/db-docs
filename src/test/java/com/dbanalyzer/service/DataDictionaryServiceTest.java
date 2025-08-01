package com.dbanalyzer.service;

import com.dbanalyzer.model.ColumnInfo;
import com.dbanalyzer.model.TableInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataDictionaryServiceTest {

    @Mock
    private DatabaseMetadataService metadataService;

    @InjectMocks
    private DataDictionaryService dataDictionaryService;

    private TableInfo tableInfo;

    @BeforeEach
    void setUp() throws Exception {
        tableInfo = new TableInfo();
        tableInfo.setTableName("users");
        tableInfo.setComment("User table");

        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("id");
        columnInfo.setDataType("INTEGER");
        columnInfo.setPrimaryKey(true);
        tableInfo.setColumns(Collections.singletonList(columnInfo));
        tableInfo.setIndexes(Collections.emptyList());
        tableInfo.setConstraints(Collections.emptyList());

        when(metadataService.getTableInfo(anyLong(), any(), any())).thenReturn(tableInfo);
    }

    @Test
    void whenGenerateMarkdownDictionary_thenReturnsCorrectMarkdown() throws Exception {
        String markdown = dataDictionaryService.generateMarkdownDictionary(1L, List.of("public.users"));

        assertTrue(markdown.contains("# Database Data Dictionary"));
        assertTrue(markdown.contains("## Table: users"));
        assertTrue(markdown.contains("| Column Name | Data Type | Nullable | Primary Key | Default Value | Comment |"));
        assertTrue(markdown.contains("| id | INTEGER | NO | YES |  |  |"));
    }

    @Test
    void whenGeneratePdfDictionary_thenReturnsPdfBytes() throws Exception {
        byte[] pdf = dataDictionaryService.generatePdfDictionary(1L, List.of("public.users"));

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
