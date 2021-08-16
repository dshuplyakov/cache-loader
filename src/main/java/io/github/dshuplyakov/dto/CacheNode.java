package io.github.dshuplyakov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheNode {
    private String id;
    private String parentId;
    private String value;
    private NodeStatus status;
    private List<String> ancestors;

    public CacheNode(String id, String parentId, String value) {
        this.id = id;
        this.parentId = parentId;
        this.value = value;
    }

    public CacheNode(CacheNode cacheNode) {
        this.id = cacheNode.id;
        this.parentId = cacheNode.parentId;
        this.value = cacheNode.value;
        this.status = cacheNode.status;
        this.ancestors = cacheNode.ancestors;
    }
}
