package com.dbanalyzer.controller;

import com.dbanalyzer.model.DatabaseConnection;
import com.dbanalyzer.model.DatabaseType;
import com.dbanalyzer.service.DatabaseConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/connections")
public class DatabaseConnectionController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionController.class);

    @Autowired
    private DatabaseConnectionService connectionService;

    @GetMapping("/new")
    public String newConnection(Model model) {
        model.addAttribute("connection", new DatabaseConnection());
        model.addAttribute("databaseTypes", DatabaseType.values());
        return "connections/new";
    }

    @GetMapping("/edit")
    public String listConnections(Model model) {
        model.addAttribute("connections", connectionService.findAll());
        return "connections/list";
    }

    @GetMapping("/edit/{id}")
    public String editConnection(@PathVariable Long id, Model model) {
        DatabaseConnection connection = connectionService.findById(id);
        if (connection == null) {
            return "redirect:/connections/edit";
        }
        model.addAttribute("connection", connection);
        model.addAttribute("databaseTypes", DatabaseType.values());
        return "connections/edit";
    }

    @PostMapping("/save")
    public String saveConnection(@ModelAttribute DatabaseConnection connection) {
        connectionService.save(connection);
        return "redirect:/";
    }

    @PostMapping("/update/{id}")
    public String updateConnection(@PathVariable Long id, @ModelAttribute DatabaseConnection connection) {
        connection.setId(id);
        connectionService.save(connection);
        return "redirect:/connections/edit";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
        connectionService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testConnection(@RequestBody DatabaseConnection connection) {
        Map<String, Object> response = new HashMap<>();
        boolean isValid = connectionService.testConnection(connection);
        response.put("success", isValid);
        response.put("message", isValid ? "Connection successful!" : "Connection failed!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/default-port/{databaseType}")
    @ResponseBody
    public ResponseEntity<Integer> getDefaultPort(@PathVariable DatabaseType databaseType) {
        return ResponseEntity.ok(databaseType.getDefaultPort());
    }
}
