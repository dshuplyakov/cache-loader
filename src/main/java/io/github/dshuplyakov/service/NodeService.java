package io.github.dshuplyakov.service;


import io.github.dshuplyakov.dto.CacheNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface NodeService {
     @NotNull List<CacheNode> loadAll();

     @Nullable CacheNode loadById(@NotNull String id);

     void reset();

     void save(@NotNull List<CacheNode> cacheNodes);

}
