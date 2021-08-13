package io.github.dshuplyakov.service.impl;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.service.NodePersistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryNodesStorage implements NodePersistence {

    public List<CacheNode> loadAllNodes() {
        ArrayList<CacheNode> result = new ArrayList<>();
        result.add(new CacheNode("14",0, "Node1"));
        result.add(new CacheNode("7",1, "Node3"));
        result.add(new CacheNode("3",1, "Node4"));
        result.add(new CacheNode("17",2, "Node12"));
        result.add(new CacheNode("12",3, "Node19"));
        result.add(new CacheNode("31",2, "AANode21"));
        result.add(new CacheNode("32",2, "ZZNode1"));
        result.add(new CacheNode("90",4, "BBNode"));
        result.add(new CacheNode("18",5, "Node21"));
        result.add(new CacheNode("4",4, "Node21"));
        return result;
    }
}
