package com.dbanalyzer.model;

public enum DatabaseType {
    MYSQL("MySQL", 3306, "com.mysql.cj.jdbc.Driver", "jdbc:mysql://"),
    POSTGRESQL("PostgreSQL", 5432, "org.postgresql.Driver", "jdbc:postgresql://"),
    SQLSERVER("SQL Server", 1433, "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://"),
    ORACLE("Oracle", 1521, "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@"),
    DB2("IBM DB2", 50000, "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://");

    private final String displayName;
    private final int defaultPort;
    private final String driverClassName;
    private final String urlPrefix;

    DatabaseType(String displayName, int defaultPort, String driverClassName, String urlPrefix) {
        this.displayName = displayName;
        this.defaultPort = defaultPort;
        this.driverClassName = driverClassName;
        this.urlPrefix = urlPrefix;
    }

    public String getDisplayName() { return displayName; }
    public int getDefaultPort() { return defaultPort; }
    public String getDriverClassName() { return driverClassName; }
    public String getUrlPrefix() { return urlPrefix; }
}