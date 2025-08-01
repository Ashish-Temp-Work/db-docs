package com.dbanalyzer.service;

import com.dbanalyzer.controller.DataDictionaryController;
import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.repository.DatabaseConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Service
public class DatabaseConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionService.class);

    @Autowired
    private DatabaseConnectionRepository repository;

    public List<DatabaseConnection> findAll() {
        return repository.findAll();
    }

    public DatabaseConnection findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public DatabaseConnection save(DatabaseConnection connection) {
        return repository.save(connection);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean testConnection(DatabaseConnection dbConnection) {
        try {
            String url = buildConnectionUrl(dbConnection);
            Class.forName(dbConnection.getDatabaseType().getDriverClassName());

            try (Connection connection = DriverManager.getConnection(
                    url, dbConnection.getUsername(), dbConnection.getPassword())) {
                return connection.isValid(10);
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public Connection getConnection(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        String url = buildConnectionUrl(dbConnection);
        Class.forName(dbConnection.getDatabaseType().getDriverClassName());
        return DriverManager.getConnection(url, dbConnection.getUsername(), dbConnection.getPassword());
    }

    private String buildConnectionUrl(DatabaseConnection dbConnection) {
        String baseUrl = dbConnection.getDatabaseType().getUrlPrefix();

        switch (dbConnection.getDatabaseType()) {
            case MYSQL:
                return baseUrl + dbConnection.getHost() + ":" + dbConnection.getPort() +
                        "/" + dbConnection.getDatabaseName() + "?useSSL=false&allowPublicKeyRetrieval=true";
            case POSTGRESQL:
                return baseUrl + dbConnection.getHost() + ":" + dbConnection.getPort() +
                        "/" + dbConnection.getDatabaseName();
            case SQLSERVER:
                return baseUrl + dbConnection.getHost() + ":" + dbConnection.getPort() +
                        ";databaseName=" + dbConnection.getDatabaseName();
            case ORACLE:
                return baseUrl + dbConnection.getHost() + ":" + dbConnection.getPort() +
                        ":" + dbConnection.getDatabaseName();
            case DB2:
                return baseUrl + dbConnection.getHost() + ":" + dbConnection.getPort() +
                        "/" + dbConnection.getDatabaseName();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbConnection.getDatabaseType());
        }
    }
}
