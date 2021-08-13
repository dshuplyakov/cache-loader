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
    private Integer level;
    private String value;
    private NodeStatus status;

    public CacheNode(String id, String parentId, Integer level, String value) {
        this.id = id;
        this.parentId = parentId;
        this.level = level;
        this.value = value;
    }
}
