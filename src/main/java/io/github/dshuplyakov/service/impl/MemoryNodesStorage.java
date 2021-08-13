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
        result.add(new CacheNode("208",null,1, "Node208"));
        result.add(new CacheNode("0", null, 0, "Node0"));
        result.add(new CacheNode("7","0",1, "Node7"));
        result.add(new CacheNode("3","7",2, "Node3"));
        result.add(new CacheNode("17","3",3, "Node17"));
        result.add(new CacheNode("12","7",4, "Node12"));
        result.add(new CacheNode("31","0",1, "Node31"));
        result.add(new CacheNode("32","31",2, "Node32"));
        result.add(new CacheNode("90","32",3, "Node90"));
        result.add(new CacheNode("18","90",4, "Node18"));
        result.add(new CacheNode("4","90",4, "Node4"));
        result.add(new CacheNode("50",null,4, "Node50"));
        result.add(new CacheNode("52","50",5, "Node52"));
        result.add(new CacheNode("150",null,4, "Node150"));
        result.add(new CacheNode("201",null,1, "Node201"));
        result.add(new CacheNode("152","50",5, "Node152"));

        return result;
    }
}
