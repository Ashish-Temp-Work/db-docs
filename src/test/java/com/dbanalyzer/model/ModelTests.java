package com.dbanalyzer.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTests {

    @Test
    void testColumnInfo() {
        ColumnInfo info = new ColumnInfo();
        info.setColumnName("id");
        info.setDataType("INT");
        info.setNullable(false);
        info.setDefaultValue("0");
        info.setPrimaryKey(true);
        info.setComment("Primary Key");
        info.setOrdinalPosition(1);

        assertEquals("id", info.getColumnName());
        assertEquals("INT", info.getDataType());
        assertFalse(info.isNullable());
        assertEquals("0", info.getDefaultValue());
        assertTrue(info.isPrimaryKey());
        assertEquals("Primary Key", info.getComment());
        assertEquals(1, info.getOrdinalPosition());
    }

    @Test
    void testConstraintInfo() {
        ConstraintInfo info = new ConstraintInfo();
        info.setConstraintName("fk_user_id");
        info.setConstraintType("FOREIGN KEY");
        info.setColumnName("user_id");
        info.setReferencedTable("users");
        info.setReferencedColumn("id");

        assertEquals("fk_user_id", info.getConstraintName());
        assertEquals("FOREIGN KEY", info.getConstraintType());
        assertEquals("user_id", info.getColumnName());
        assertEquals("users", info.getReferencedTable());
        assertEquals("id", info.getReferencedColumn());
    }

    @Test
    void testDatabaseConnection() {
        DatabaseConnection conn = new DatabaseConnection();
        conn.setId(1L);
        conn.setName("Test");
        conn.setDescription("Test Description");
        conn.setDatabaseType(DatabaseType.MYSQL);
        conn.setHost("localhost");
        conn.setPort(3306);
        conn.setDatabaseName("testdb");
        conn.setSchema("public");
        conn.setUsername("user");
        conn.setPassword("pass");

        LocalDateTime now = LocalDateTime.now();
        conn.setCreatedAt(now);
        conn.setUpdatedAt(now);

        assertEquals(1L, conn.getId());
        assertEquals("Test", conn.getName());
        assertEquals("Test Description", conn.getDescription());
        assertEquals(DatabaseType.MYSQL, conn.getDatabaseType());
        assertEquals("localhost", conn.getHost());
        assertEquals(3306, conn.getPort());
        assertEquals("testdb", conn.getDatabaseName());
        assertEquals("public", conn.getSchema());
        assertEquals("user", conn.getUsername());
        assertEquals("pass", conn.getPassword());
        assertEquals(now, conn.getCreatedAt());
        assertEquals(now, conn.getUpdatedAt());
    }

    @Test
    void testDatabaseConnectionPrePersistUpdate() {
        DatabaseConnection conn = new DatabaseConnection();
        conn.onCreate();
        assertNotNull(conn.getCreatedAt());
        assertNotNull(conn.getUpdatedAt());

        LocalDateTime oldUpdate = conn.getUpdatedAt();
        conn.onUpdate();
        assertTrue(conn.getUpdatedAt().isAfter(oldUpdate) || conn.getUpdatedAt().isEqual(oldUpdate));
    }

    @Test
    void testDatabaseObject() {
        DatabaseObject obj = new DatabaseObject("users", "TABLE", "public", "User table");
        obj.setSelected(true);

        assertEquals("users", obj.getName());
        assertEquals("TABLE", obj.getType());
        assertEquals("public", obj.getSchema());
        assertEquals("User table", obj.getComment());
        assertTrue(obj.isSelected());
    }

    @Test
    void testDatabaseType() {
        assertEquals("MySQL", DatabaseType.MYSQL.getDisplayName());
        assertEquals(3306, DatabaseType.MYSQL.getDefaultPort());
        assertEquals("com.mysql.cj.jdbc.Driver", DatabaseType.MYSQL.getDriverClassName());
        assertEquals("jdbc:mysql://", DatabaseType.MYSQL.getUrlPrefix());
    }

    @Test
    void testIndexInfo() {
        IndexInfo info = new IndexInfo();
        info.setIndexName("idx_username");
        info.setColumnName("username");
        info.setUnique(true);
        info.setIndexType("BTREE");

        assertEquals("idx_username", info.getIndexName());
        assertEquals("username", info.getColumnName());
        assertTrue(info.isUnique());
        assertEquals("BTREE", info.getIndexType());
    }

    @Test
    void testTableInfo() {
        TableInfo info = new TableInfo();
        info.setTableName("posts");
        info.setSchema("blog");
        info.setComment("Blog posts");
        info.setColumns(Collections.emptyList());
        info.setIndexes(Collections.emptyList());
        info.setConstraints(Collections.emptyList());

        assertEquals("posts", info.getTableName());
        assertEquals("blog", info.getSchema());
        assertEquals("Blog posts", info.getComment());
        assertTrue(info.getColumns().isEmpty());
        assertTrue(info.getIndexes().isEmpty());
        assertTrue(info.getConstraints().isEmpty());
    }
}
