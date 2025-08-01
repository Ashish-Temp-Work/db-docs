[![Build, Release, and Deploy Site](https://github.com/Ashish-Temp-Work/db-docs/actions/workflows/main-action.yml/badge.svg)](https://github.com/Ashish-Temp-Work/db-docs/actions/workflows/main-action.yml)

[![Create Release](https://github.com/Ashish-Temp-Work/db-docs/actions/workflows/main.yml/badge.svg)](https://github.com/Ashish-Temp-Work/db-docs/actions/workflows/main.yml)


# Database Schema Analyzer

A comprehensive Java web application built with Spring Boot for analyzing database schemas, generating data dictionaries, and creating SQL scripts.

## Features

- **Multi-Database Support**: Connect to MySQL, PostgreSQL, SQL Server, Oracle, and IBM DB2
- **Connection Management**: Create, edit, test, and manage database connections
- **Schema Analysis**: Extract detailed information about tables, views, procedures, columns, indexes, and constraints
- **Data Dictionary Generation**: Export comprehensive documentation in Markdown or PDF format
- **SQL Script Generation**: Generate CREATE scripts for database objects in ZIP format
- **Modern Web UI**: Responsive design with intuitive user interface

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Spring Data JPA
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Database**: H2 (embedded), with drivers for MySQL, PostgreSQL, SQL Server, Oracle, DB2
- **Build Tool**: Maven
- **Documentation**: iText7 for PDF generation

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone or download the project
2. Navigate to the project directory
3. Build the application:
   ```bash
   mvn clean package
   ```
4. Run the application:
   ```bash
   java -jar target/database-schema-analyzer-1.0.0.jar
   ```
5. Open your browser and go to: `http://localhost:8080`

### Usage

1. **Create Database Connection**
    - Click "Create New Connection" from the home page
    - Fill in database details (host, port, database name, credentials)
    - Test the connection before saving

2. **Generate Data Dictionary**
    - Click "Create Data Dictionary"
    - Select a database connection
    - Choose objects to include
    - Select export format (Markdown or PDF)
    - Download the generated documentation

3. **Generate SQL Scripts**
    - Click "Create SQL Scripts"
    - Select a database connection
    - Choose objects to include
    - Download the ZIP file containing SQL scripts

## Configuration

The application uses the following default settings:

- **Port**: 8080
- **H2 Database**: File-based storage in `./data/dbanalyzer`
- **H2 Console**: Available at `/h2-console` (username: `sa`, password: empty)

You can modify these settings in `src/main/resources/application.yml`

## Supported Databases

| Database | Default Port | Driver |
|----------|--------------|--------|
| MySQL | 3306 | mysql-connector-java |
| PostgreSQL | 5432 | postgresql |
| SQL Server | 1433 | mssql-jdbc |
| Oracle | 1521 | ojdbc11 |
| IBM DB2 | 50000 | db2jcc |

## Project Structure

```
src/
├── main/
│   ├── java/com/dbanalyzer/
│   │   ├── controller/          # REST controllers
│   │   ├── model/              # Entity classes
│   │   ├── repository/         # Data repositories
│   │   ├── service/            # Business logic
│   │   └── DatabaseSchemaAnalyzerApplication.java
│   └── resources/
│       ├── static/             # CSS, JS, images
│       ├── templates/          # Thymeleaf templates
│       └── application.yml     # Configuration
```
