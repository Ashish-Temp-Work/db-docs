package com.dbanalyzer.controller;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseObject;
import com.dbanalyzer.service.DatabaseConnectionService;
import com.dbanalyzer.service.DatabaseMetadataService;
import com.dbanalyzer.service.DataDictionaryService;
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
@RequestMapping("/data-dictionary")
public class DataDictionaryController {

    private static final Logger logger = LoggerFactory.getLogger(DataDictionaryController.class);

    @Autowired
    private DatabaseConnectionService connectionService;

    @Autowired
    private DatabaseMetadataService metadataService;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @GetMapping
    public String selectConnection(Model model) {
        model.addAttribute("connections", connectionService.findAll());
        return "data-dictionary/select-connection";
    }

    @GetMapping("/objects/{connectionId}")
    public String selectObjects(@PathVariable Long connectionId, Model model) {
        try {
            //logger.info("\n\n\n\t\tThis should print : selectObjects\n\n\n");
            DatabaseConnection connection = connectionService.findById(connectionId);
            List<DatabaseObject> objects = metadataService.getDatabaseObjects(connectionId);
            model.addAttribute("connection", connection);
            model.addAttribute("objects", objects);
            model.addAttribute("connectionId", connectionId);
            return "data-dictionary/select-objects";
        } catch (Exception e) {
            logger.error("Error retrieving database objects for connectionId {}: {}", connectionId, e.getMessage(), e); // Modified line: Log full exception `e`
            model.addAttribute("error", "Error retrieving database objects: " + e.getMessage());
            return "data-dictionary/select-connection";
        }
    }

    @PostMapping("/generate/{connectionId}")
    public ResponseEntity<byte[]> generateDictionary(
            @PathVariable Long connectionId,
            @RequestParam("format") String format,
            @RequestParam("selectedObjects") List<String> selectedObjects) {

        try {
            //logger.info("\n\n\n\t\tThis should print : generateDictionary\n\n\n");
            if ("markdown".equals(format)) {
                String markdown = dataDictionaryService.generateMarkdownDictionary(connectionId, selectedObjects);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                headers.setContentDispositionFormData("attachment", "data-dictionary.md");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(markdown.getBytes());
            } else if ("pdf".equals(format)) {
                byte[] pdf = dataDictionaryService.generatePdfDictionary(connectionId, selectedObjects);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "data-dictionary.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(pdf);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return ResponseEntity.internalServerError().build();
        }
    }
}