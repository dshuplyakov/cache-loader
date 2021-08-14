package io.github.dshuplyakov.rest.controller;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodes")

@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    @GetMapping(path = "/load")
    public List<CacheNode> load() {
        return nodeService.loadAll();
    }

    @GetMapping(path = "/get/{id}")
    public CacheNode loadById(@PathVariable String id) {
        return nodeService.loadAllById(id);
    }

    @PostMapping(path = "/save")
    public String save(@RequestBody List<CacheNode> cacheNodes) {
        nodeService.save(cacheNodes);
        return "OK";
    }

    @GetMapping(path = "/reset")
    public String reset() {
        nodeService.loadNodesFromDB();
        return "OK";
    }


}
