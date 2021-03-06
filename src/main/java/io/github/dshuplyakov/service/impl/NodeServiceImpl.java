package io.github.dshuplyakov.service.impl;

import io.github.dshuplyakov.dto.CacheNode;
import io.github.dshuplyakov.dto.NodeStatus;
import io.github.dshuplyakov.service.NodeDAO;
import io.github.dshuplyakov.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeDAO nodeDAO;
    private final Map<String, CacheNode> storage = new HashMap<>();
    private final Set<String> cacheStorage = new HashSet<>();

    @PostConstruct
    public void reset() {
        storage.clear();
        cacheStorage.clear();
        for (CacheNode cacheNode : nodeDAO.loadAllNodes()) {
            storage.put(cacheNode.getId(), cacheNode);
        }
    }

    @Override
    public List<CacheNode> loadAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public CacheNode loadById(String id) {
        if (storage.containsKey(id)) {
            CacheNode cacheNode = new CacheNode(storage.get(id));
            cacheNode.setAncestors(calculateAncestors(cacheNode));
            cacheNode.setNodesEnrichAncestor(findNodesForAddingAncestors(cacheNode));
            cacheStorage.add(id);
            return cacheNode;
        }

        return null;
    }

    /**
     * Find nodes which we should add ancestor
     *
     * Algorithm:
     * find all child of these node
     * if child contains in cacheStorage -> return this child
     * @param cacheNode
     * @return
     */
    private List<String> findNodesForAddingAncestors(CacheNode cacheNode) {
        Set<String> childrenNodes = findChildrenNodes(List.of(cacheNode.getId()));
        childrenNodes.retainAll(cacheStorage);
        return new ArrayList<>(childrenNodes);
    }

    private List<String> calculateAncestors(CacheNode cacheNode) {
        List<String> result = new ArrayList<>();
        while(cacheNode.getParentId() != null) {
            result.add(cacheNode.getParentId());
            cacheNode = storage.get(cacheNode.getParentId());
        }

        result.retainAll(cacheStorage);
        return result;
    }

    @Override
    public void save(@NotNull List<CacheNode> cacheNodes) {
        addNewNodes(cacheNodes);
        renameNodes(cacheNodes);
        removeNodesWithChildren(cacheNodes);
    }

    private void addNewNodes(@NotNull List<CacheNode> cacheNodes) {
        cacheNodes.forEach(node -> {
            if (NodeStatus.REMOVED != node.getStatus()) {
                if (!storage.containsKey(node.getId())) {
                    storage.put(node.getId(), node);
                }
            }
        });
    }

    private void renameNodes(@NotNull List<CacheNode> cacheNodes) {
        cacheNodes.forEach(node -> {
            if (NodeStatus.CHANGED == node.getStatus()) {
                storage.get(node.getId()).setValue(node.getValue());
            }
        });
    }

    private void removeNodesWithChildren(@NotNull List<CacheNode> cacheNodes) {
        List<String> removedNodeIds = cacheNodes.stream()
                .filter(n -> NodeStatus.REMOVED == n.getStatus())
                .map(CacheNode::getId)
                .collect(Collectors.toList());

        Set<String> nodeIdsForRemove = findChildrenNodes(removedNodeIds);
        nodeIdsForRemove.addAll(removedNodeIds);

        nodeIdsForRemove
            .stream()
            .filter(storage::containsKey)
            .forEach(nodeId -> storage.get(nodeId).setStatus(NodeStatus.REMOVED));
    }

    private Set<String> findChildrenNodes(@NotNull List<String> nodeIds) {
        Map<String, Set<String>> mapIdToChildren = buildMapIdToChildren();
        return nodeIds.stream()
                .map(nodeId -> findChildrenOfNode(nodeId, mapIdToChildren))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Find all children nodes of passed node
     * @param nodeId
     * @param mapIdToChildren
     * @return
     */
    private List<String> findChildrenOfNode(@NotNull String nodeId,
                                            @NotNull Map<String, Set<String>> mapIdToChildren) {
        Queue<String> queue = new LinkedList<>();
        if (mapIdToChildren.containsKey(nodeId)) {
            queue.addAll(mapIdToChildren.get(nodeId));
        }
        List<String> result = new ArrayList<>();
        buildPathFromHeadToLeafs(queue, mapIdToChildren, result);
        return result;
    }

    /**
     * Build map:  node id => array of children
     * @return
     */
    private Map<String, Set<String>> buildMapIdToChildren() {
        Map<String, Set<String>> result = new HashMap<>();
        for (CacheNode node : storage.values()) {
            if (node.getParentId() != null) {
                if (result.containsKey(node.getParentId())) {
                    result.get(node.getParentId()).add(node.getId());
                } else {
                    Set<String> childrenIds = new HashSet<>();
                    childrenIds.add(node.getId());
                    result.putIfAbsent(node.getParentId(), childrenIds);
                }
            }
        }
        return result;
    }

    /**
     * Build path from passed node to its leafs
     * @param queue
     * @param mapIdToChildren
     * @param result
     */
    private void buildPathFromHeadToLeafs(@NotNull Queue<String> queue, @NotNull Map<String, Set<String>> mapIdToChildren,
                                          @NotNull List<String> result) {
        String nodeId = queue.poll();
        if (nodeId == null) {
            return;
        }
        result.add(nodeId);
        if (mapIdToChildren.containsKey(nodeId)) {
            Set<String> nodeIds = mapIdToChildren.get(nodeId);
            queue.addAll(nodeIds);
        }
        buildPathFromHeadToLeafs(queue, mapIdToChildren, result);
    }

}
