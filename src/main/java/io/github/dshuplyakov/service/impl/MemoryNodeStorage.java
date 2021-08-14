package io.github.dshuplyakov.service.impl;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.service.NodeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryNodeStorage implements NodeDAO {

    public List<CacheNode> loadAllNodes() {
        ArrayList<CacheNode> result = new ArrayList<>();
        result.add(new CacheNode("0", null, "Node0"));
        result.add(new CacheNode("7","0", "Node7"));
        result.add(new CacheNode("3","7", "Node3"));
        result.add(new CacheNode("17","3", "Node17"));
        result.add(new CacheNode("12","7", "Node12"));
        result.add(new CacheNode("31","0", "Node31"));
        result.add(new CacheNode("32","31", "Node32"));
        result.add(new CacheNode("90","32", "Node90"));
        result.add(new CacheNode("18","90", "Node18"));
        result.add(new CacheNode("4","90", "Node4"));
        result.add(new CacheNode("50","12", "Node50"));
        result.add(new CacheNode("52","50", "Node52"));
        result.add(new CacheNode("150","12", "Node150"));
        result.add(new CacheNode("201","12", "Node201"));
        result.add(new CacheNode("152","50", "Node152"));
        return result;
    }
}
