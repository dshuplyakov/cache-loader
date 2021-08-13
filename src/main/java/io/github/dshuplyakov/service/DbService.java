package io.github.dshuplyakov.service;


import io.github.dshuplyakov.dto.CacheNode;

import java.util.List;

public interface DbService {
     List<CacheNode> loadAll();

     CacheNode loadAllById(String id);

     void loadNodesFromDB();

     void save(List<CacheNode> cacheNodes);

}
