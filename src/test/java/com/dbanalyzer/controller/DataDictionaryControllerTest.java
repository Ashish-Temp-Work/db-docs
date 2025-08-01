package com.dbanalyzer.controller;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseObject;
import com.dbanalyzer.service.DataDictionaryService;
import com.dbanalyzer.service.DatabaseConnectionService;
import com.dbanalyzer.service.DatabaseMetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataDictionaryController.class)
public class DataDictionaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseConnectionService connectionService;

    @MockBean
    private DatabaseMetadataService metadataService;

    @MockBean
    private DataDictionaryService dataDictionaryService;

    private DatabaseConnection connection;
    private DatabaseObject dbObject;

    @BeforeEach
    void setUp() {
        connection = new DatabaseConnection();
        connection.setId(1L);
        connection.setName("Test Connection");

        dbObject = new DatabaseObject("users", "TABLE", "public", "User table");
    }

    @Test
    void whenSelectConnection_thenReturnsSelectConnectionView() throws Exception {
        when(connectionService.findAll()).thenReturn(Collections.singletonList(connection));
        mockMvc.perform(get("/data-dictionary"))
                .andExpect(status().isOk())
                .andExpect(view().name("data-dictionary/select-connection"))
                .andExpect(model().attributeExists("connections"));
    }

    @Test
    void whenSelectObjects_thenReturnsSelectObjectsView() throws Exception {
        when(connectionService.findById(1L)).thenReturn(connection);
        when(metadataService.getDatabaseObjects(1L)).thenReturn(Collections.singletonList(dbObject));

        mockMvc.perform(get("/data-dictionary/objects/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("data-dictionary/select-objects"))
                .andExpect(model().attribute("connection", connection))
                .andExpect(model().attribute("objects", Collections.singletonList(dbObject)));
    }

    @Test
    void whenGenerateDictionary_asMarkdown_thenReturnsMarkdownFile() throws Exception {
        when(dataDictionaryService.generateMarkdownDictionary(anyLong(), any(List.class))).thenReturn("# Test");

        mockMvc.perform(post("/data-dictionary/generate/1")
                        .param("format", "markdown")
                        .param("selectedObjects", "public.users"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"data-dictionary.md\""))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("# Test"));
    }

    @Test
    void whenGenerateDictionary_asPdf_thenReturnsPdfFile() throws Exception {
        byte[] pdfBytes = "PDF".getBytes();
        when(dataDictionaryService.generatePdfDictionary(anyLong(), any(List.class))).thenReturn(pdfBytes);

        mockMvc.perform(post("/data-dictionary/generate/1")
                        .param("format", "pdf")
                        .param("selectedObjects", "public.users"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"data-dictionary.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfBytes));
    }
}
