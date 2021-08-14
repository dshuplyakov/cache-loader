package io.github.dshuplyakov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheNode {
    private String id;
    private String parentId;
    private String value;
    private NodeStatus status;

    public CacheNode(String id, String parentId, String value) {
        this.id = id;
        this.parentId = parentId;
        this.value = value;
    }
}
