package com.dbanalyzer.controller;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseObject;
import com.dbanalyzer.service.DatabaseConnectionService;
import com.dbanalyzer.service.DatabaseMetadataService;
import com.dbanalyzer.service.SqlScriptService;
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

@WebMvcTest(SqlScriptController.class)
public class SqlScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseConnectionService connectionService;

    @MockBean
    private DatabaseMetadataService metadataService;

    @MockBean
    private SqlScriptService sqlScriptService;

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
        mockMvc.perform(get("/sql-scripts"))
                .andExpect(status().isOk())
                .andExpect(view().name("sql-scripts/select-connection"))
                .andExpect(model().attributeExists("connections"));
    }

    @Test
    void whenSelectObjects_thenReturnsSelectObjectsView() throws Exception {
        when(connectionService.findById(1L)).thenReturn(connection);
        when(metadataService.getDatabaseObjects(1L)).thenReturn(Collections.singletonList(dbObject));

        mockMvc.perform(get("/sql-scripts/objects/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("sql-scripts/select-objects"))
                .andExpect(model().attribute("connection", connection))
                .andExpect(model().attribute("objects", Collections.singletonList(dbObject)));
    }

    @Test
    void whenGenerateScripts_thenReturnsZipFile() throws Exception {
        byte[] zipBytes = "ZIP".getBytes();
        when(sqlScriptService.generateSqlScripts(anyLong(), any(List.class))).thenReturn(zipBytes);

        mockMvc.perform(post("/sql-scripts/generate/1")
                        .param("selectedObjects", "public.users"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"sql-scripts.zip\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(zipBytes));
    }
}
