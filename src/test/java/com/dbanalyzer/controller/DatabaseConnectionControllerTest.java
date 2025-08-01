package com.dbanalyzer.controller;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseType;
import com.dbanalyzer.service.DatabaseConnectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatabaseConnectionController.class)
public class DatabaseConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseConnectionService connectionService;

    @Autowired
    private ObjectMapper objectMapper;

    private DatabaseConnection connection;

    @BeforeEach
    void setUp() {
        connection = new DatabaseConnection();
        connection.setId(1L);
        connection.setName("Test Connection");
        connection.setDatabaseType(DatabaseType.POSTGRESQL);
        connection.setHost("localhost");
        connection.setPort(5432);
        connection.setDatabaseName("testdb");
        connection.setUsername("user");
        connection.setPassword("password");
    }

    @Test
    void whenNewConnection_thenReturnsNewConnectionView() throws Exception {
        mockMvc.perform(get("/connections/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections/new"))
                .andExpect(model().attributeExists("connection"))
                .andExpect(model().attributeExists("databaseTypes"));
    }

    @Test
    void whenListConnections_thenReturnsListView() throws Exception {
        when(connectionService.findAll()).thenReturn(Collections.singletonList(connection));
        mockMvc.perform(get("/connections/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections/list"))
                .andExpect(model().attribute("connections", Collections.singletonList(connection)));
    }

    @Test
    void whenEditConnection_thenReturnsEditView() throws Exception {
        when(connectionService.findById(1L)).thenReturn(connection);
        mockMvc.perform(get("/connections/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections/edit"))
                .andExpect(model().attribute("connection", connection));
    }

    @Test
    void whenSaveConnection_thenRedirectsToIndex() throws Exception {
        mockMvc.perform(post("/connections/save")
                        .flashAttr("connection", connection))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void whenUpdateConnection_thenRedirectsToEditList() throws Exception {
        mockMvc.perform(post("/connections/update/1")
                        .flashAttr("connection", connection))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connections/edit"));
    }

    @Test
    void whenDeleteConnection_thenReturnsOk() throws Exception {
        mockMvc.perform(delete("/connections/1"))
                .andExpect(status().isOk());
    }

    @Test
    void whenTestConnection_withValidConnection_thenReturnsSuccess() throws Exception {
        when(connectionService.testConnection(any(DatabaseConnection.class))).thenReturn(true);
        mockMvc.perform(post("/connections/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(connection)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Connection successful!"));
    }

    @Test
    void whenGetDefaultPort_thenReturnsPort() throws Exception {
        mockMvc.perform(get("/connections/default-port/POSTGRESQL"))
                .andExpect(status().isOk())
                .andExpect(content().string("5432"));
    }
}
