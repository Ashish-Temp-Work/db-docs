package com.dbanalyzer.service;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseType;
import com.dbanalyzer.repository.DatabaseConnectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseConnectionServiceTest {

    @Mock
    private DatabaseConnectionRepository repository;

    @InjectMocks
    private DatabaseConnectionService connectionService;

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
    void whenFindAll_thenReturnsConnectionList() {
        when(repository.findAll()).thenReturn(Collections.singletonList(connection));
        List<DatabaseConnection> connections = connectionService.findAll();
        assertEquals(1, connections.size());
        assertEquals("Test Connection", connections.get(0).getName());
    }

    @Test
    void whenFindById_thenReturnsConnection() {
        when(repository.findById(1L)).thenReturn(Optional.of(connection));
        DatabaseConnection found = connectionService.findById(1L);
        assertNotNull(found);
        assertEquals("Test Connection", found.getName());
    }

    @Test
    void whenFindById_withNonExistentId_thenReturnsNull() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        DatabaseConnection found = connectionService.findById(99L);
        assertNull(found);
    }

    @Test
    void whenSave_thenReturnsSavedConnection() {
        when(repository.save(any(DatabaseConnection.class))).thenReturn(connection);
        DatabaseConnection saved = connectionService.save(new DatabaseConnection());
        assertNotNull(saved);
    }

    @Test
    void whenDeleteById_thenRepositoryMethodIsCalled() {
        doNothing().when(repository).deleteById(1L);
        connectionService.deleteById(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
