package leetcode.graphAndSearch;

import java.util.*;

public class TopologicalSort {
    class DirectedGraphNode {
        int label;
        ArrayList<DirectedGraphNode> neighbors;
        DirectedGraphNode(int x) {
            label = x;
            neighbors = new ArrayList<DirectedGraphNode>();
        }
    }
    public ArrayList<DirectedGraphNode> topSort(ArrayList<DirectedGraphNode> graph) {
        ArrayList<DirectedGraphNode> result = new ArrayList<>();
        if(graph == null) {
            return result;
        }
        Map<DirectedGraphNode, Integer> map = new HashMap<>();
        for (DirectedGraphNode node : graph) {
            for (DirectedGraphNode neibs : node.neighbors) {
                if (map.containsKey(neibs)) {
                    map.put(neibs, map.get(neibs) + 1);
                } else {
                    map.put(neibs, 1);
                }
            }
        }
        Queue<DirectedGraphNode> queue = new LinkedList<>();
        for (DirectedGraphNode node : graph) {
            if(! map.containsKey(node)) {
                queue.add(node);
                result.add(node);
            }
        }

        while (! queue.isEmpty()) {
            DirectedGraphNode cur = queue.poll();
            for (DirectedGraphNode nb : cur.neighbors) {
                int inDegree = map.get(nb) - 1;
                map.put(nb, inDegree);
                if(inDegree == 0) {
                    queue.add(nb);
                    result.add(nb);
                }
            }
        }

        return result;
    }
}
