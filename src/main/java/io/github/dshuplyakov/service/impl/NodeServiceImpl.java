package io.github.dshuplyakov.service.impl;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.dto.NodeStatus;
import io.github.dshuplyakov.service.NodeDAO;
import io.github.dshuplyakov.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    public static final String TEMP_ID_PREFIX = "T";
    private final NodeDAO nodeDAO;
    private final Map<String, CacheNode> storage = new HashMap<>();
    private final Map<String, String> mapTempIds = new HashMap<>();

    @PostConstruct
    public void loadNodesFromDB() {
        storage.clear();
        for (CacheNode cacheNode : nodeDAO.loadAllNodes()) {
            storage.put(cacheNode.getId(), cacheNode);
        }
    }

    private String getAutoIncrementId() {
        OptionalInt max = storage.keySet().stream().mapToInt(Integer::valueOf).max();
        int result = max.isPresent() ? max.getAsInt() + 1 : max.getAsInt();
        return String.valueOf(result);
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
                cacheNode.setId(convertTempIdToRealId(cacheNode.getId()));
                cacheNode.setParentId(convertTempIdToRealId(cacheNode.getParentId()));
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

    private String convertTempIdToRealId(String tempId) {
        if (tempId.contains(TEMP_ID_PREFIX)) {
            if (!mapTempIds.containsKey(tempId)) {
                mapTempIds.put(tempId, getAutoIncrementId());
            }
            return mapTempIds.get(tempId);
        } else {
            return tempId;
        }
    }

}
