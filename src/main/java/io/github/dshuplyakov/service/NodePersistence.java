package io.github.dshuplyakov.service;

import io.github.dshuplyakov.dto.CacheNode;

import java.util.List;

public interface NodePersistence {
    List<CacheNode> loadAllNodes();
}
