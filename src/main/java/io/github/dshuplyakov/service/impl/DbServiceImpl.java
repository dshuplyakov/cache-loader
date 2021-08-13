package io.github.dshuplyakov.service.impl;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.dto.NodeStatus;
import io.github.dshuplyakov.service.NodePersistence;
import io.github.dshuplyakov.service.DbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DbServiceImpl implements DbService {

    private final NodePersistence nodePersistence;
    private final Map<String, CacheNode> storage = new HashMap<>();

    @PostConstruct
    public void loadNodesFromDB() {
        storage.clear();
        for (CacheNode cacheNode : nodePersistence.loadAllNodes()) {
            storage.put(cacheNode.getId(), cacheNode);
        }
    }

    private String getAutoIncrementId() {
        return Collections.max(storage.keySet()) + 1;
    }

    @Override
    public List<CacheNode> loadAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public CacheNode loadAllById(String id) {
        return storage.get(id);
    }

    @Override
    public void save(List<CacheNode> cacheNodes) {
        for (CacheNode cacheNode : cacheNodes) {
            if (NodeStatus.NEW == cacheNode.getStatus()) {
                cacheNode.setId(getAutoIncrementId());
                storage.put(cacheNode.getId(), cacheNode);
            }

            if (NodeStatus.CHANGED == cacheNode.getStatus()) {
                storage.get(cacheNode.getId()).setValue(cacheNode.getValue());
            }

            if (NodeStatus.REMOVED == cacheNode.getStatus()) {
                storage.remove(cacheNode.getId());
            }
        }
    }

}
