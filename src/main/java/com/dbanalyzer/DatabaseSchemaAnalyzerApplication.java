package com.dbanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class DatabaseSchemaAnalyzerApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaAnalyzerApplication.class);
    private final Environment environment;

    public DatabaseSchemaAnalyzerApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(DatabaseSchemaAnalyzerApplication.class, args);
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
        String port = environment.getProperty("server.port");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String url = "http://localhost:" + port + contextPath;

        logger.info("Application is running on: {}", url);

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            logger.error("Failed to open browser automatically: {}", e.getMessage());
        }
    }
}