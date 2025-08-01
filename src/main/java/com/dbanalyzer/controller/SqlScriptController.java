package com.dbanalyzer.controller;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseObject;
import com.dbanalyzer.service.DatabaseConnectionService;
import com.dbanalyzer.service.DatabaseMetadataService;
import com.dbanalyzer.service.SqlScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sql-scripts")
public class SqlScriptController {

    private static final Logger logger = LoggerFactory.getLogger(SqlScriptController.class);

    @Autowired
    private DatabaseConnectionService connectionService;

    @Autowired
    private DatabaseMetadataService metadataService;

    @Autowired
    private SqlScriptService sqlScriptService;

    @GetMapping
    public String selectConnection(Model model) {
        model.addAttribute("connections", connectionService.findAll());
        return "sql-scripts/select-connection";
    }

    @GetMapping("/objects/{connectionId}")
    public String selectObjects(@PathVariable Long connectionId, Model model) {
        try {
            DatabaseConnection connection = connectionService.findById(connectionId);
            List<DatabaseObject> objects = metadataService.getDatabaseObjects(connectionId);

            model.addAttribute("connection", connection);
            model.addAttribute("objects", objects);
            model.addAttribute("connectionId", connectionId);
            return "sql-scripts/select-objects";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving database objects: " + e.getMessage());
            logger.error(e.getMessage(),e);
            return "sql-scripts/select-connection";
        }
    }

    @PostMapping("/generate/{connectionId}")
    public ResponseEntity<byte[]> generateScripts(
            @PathVariable Long connectionId,
            @RequestParam("selectedObjects") List<String> selectedObjects) {

        try {
            byte[] zipFile = sqlScriptService.generateSqlScripts(connectionId, selectedObjects);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "sql-scripts.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipFile);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return ResponseEntity.internalServerError().build();
        }
    }
}