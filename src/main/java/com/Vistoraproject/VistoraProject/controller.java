package com.Vistoraproject.VistoraProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meta")
public class controller {

    @Autowired
    private MetaDataService service;

    @PostMapping("/init")
    public String init(@RequestBody Configfile config) {
        service.initConnection(config);
        return "✅ Connection initialized.";
    }

    @GetMapping("/tables")
    public List<String> getTables() {
        return service.getAllTables();
    }

    @GetMapping("/columns/{table}")
    public List<Map<String, Object>> getColumns(@PathVariable String table) {
        return service.getColumnMetadata(table);
    }

    @GetMapping("/primary-keys/{table}")
    public List<Map<String, Object>> getPrimaryKeys(@PathVariable String table) {
        return service.getPrimaryKeys(table);
    }

    @GetMapping("/foreign-keys/{table}")
    public List<Map<String, Object>> getForeignKeys(@PathVariable String table) {
        return service.getForeignKeys(table);
    }

    @GetMapping("/indexes/{table}")
    public List<Map<String, Object>> getIndexes(@PathVariable String table) {
        return service.getIndexes(table);
    }

    @GetMapping("/generate-model/{table}")
    public String previewModel(@PathVariable String table) {
        modelgenerator generator = new modelgenerator();
        return generator.generateModelAsString(table, service);
    }

}
