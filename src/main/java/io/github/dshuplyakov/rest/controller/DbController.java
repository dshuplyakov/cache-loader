package io.github.dshuplyakov.rest.controller;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.service.DbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/nodes")

@RequiredArgsConstructor
public class DbController {

    private final DbService dbService;

    @GetMapping(path = "/load")
    public List<CacheNode> load() {
        return dbService.loadAll();
    }

    @GetMapping(path = "/get/{id}")
    public CacheNode loadById(@PathVariable String id) {
        return dbService.loadAllById(id);
    }

    @PostMapping(path = "/save")
    public String save(@RequestBody List<CacheNode> cacheNodes) {
        dbService.save(cacheNodes);
        return "OK";
    }

    @GetMapping(path = "/reset")
    public String reset() {
        dbService.loadNodesFromDB();
        return "OK";
    }


}
