package leetcode.graphAndSearch;

import java.util.*;

public class CloneGraph {
    class UndirectedGraphNode {
        int label;
        List<UndirectedGraphNode> neighbors;
        UndirectedGraphNode(int x) {
            label = x;
            neighbors = new ArrayList<UndirectedGraphNode>();
        }
    }

    public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
        if(node == null){
            return null;
        }
        Map<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
        Queue<UndirectedGraphNode> queue = new LinkedList<>();
        queue.offer(node);
        map.put(node, new UndirectedGraphNode(node.label));

        while (! queue.isEmpty()) {
            UndirectedGraphNode cur = queue.poll();
            UndirectedGraphNode copy = map.get(cur);
            for(UndirectedGraphNode ns : cur.neighbors) {
                if(! map.containsKey(ns)) {
                    UndirectedGraphNode copyNS = new UndirectedGraphNode(ns.label);
                    map.put(ns, copyNS);
                    queue.add(ns);
                    copy.neighbors.add(copyNS);
                } else {
                    copy.neighbors.add(map.get(ns));
                }
            }
        }

        return map.get(node);
    }

    public UndirectedGraphNode cloneGraph2(UndirectedGraphNode node) {
        if(node == null) {
            return node;
        }
        Map<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
        Queue<UndirectedGraphNode> queue = new LinkedList<>();
        queue.add(node);
        UndirectedGraphNode copy = new UndirectedGraphNode(node.label);
        map.put(node, copy);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                UndirectedGraphNode cur = queue.poll();
                UndirectedGraphNode copyCur = map.get(node);
                for (UndirectedGraphNode neighbor : cur.neighbors) {
                    if (!map.containsKey(neighbor)) {
                        queue.add(neighbor);
                        UndirectedGraphNode copyNeightbor = new UndirectedGraphNode(neighbor.label);
                        map.put(neighbor, copyNeightbor);
                        copyCur.neighbors.add(copyNeightbor);
                    } else {
                        copyCur.neighbors.add(map.get(neighbor));
                    }
                }
            }
        }
        return copy;
    }
}
