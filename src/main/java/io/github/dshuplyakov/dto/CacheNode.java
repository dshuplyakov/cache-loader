package io.github.dshuplyakov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.EnumMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheNode {
    private String id;
    private Integer level;
    private String value;
    private NodeStatus status;

    public CacheNode(String id, Integer level, String value) {
        this.id = id;
        this.level = level;
        this.value = value;
    }
}
