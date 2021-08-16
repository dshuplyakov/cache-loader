package io.github.dshuplyakov.rest.controller;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storage")

@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    @GetMapping(path = "/nodes")
    public List<CacheNode> load() {
        return nodeService.loadAll();
    }

    @GetMapping(path = "/node/{id}")
    public CacheNode loadById(@PathVariable String id) {
        return nodeService.loadById(id);
    }

    @PostMapping(path = "/nodes")
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
