package io.github.dshuplyakov.service;

import io.github.dshuplyakov.dto.CacheNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NodeDAO {
    @NotNull List<CacheNode> loadAllNodes();
}
