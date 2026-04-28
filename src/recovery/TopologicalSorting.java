package recovery;

import datastructure.graph.Node;

import java.util.*;


public class TopologicalSorting {
    class DirectedGraphNode {
        int label;
        List<DirectedGraphNode> neighbors;

        DirectedGraphNode(int x) {
            label = x;
            neighbors = new ArrayList<DirectedGraphNode>();
        }
    }
    public ArrayList<DirectedGraphNode> topSort(ArrayList<DirectedGraphNode> graph) {
        // write your code here
        if (graph == null || graph.isEmpty()) {
            return graph;
        }

        ArrayList<DirectedGraphNode> result = new ArrayList<>();
        Map<DirectedGraphNode, Integer> inDegree = new HashMap<>();

        for (DirectedGraphNode node : graph) {
            for (DirectedGraphNode nb : node.neighbors) {
                inDegree.put(nb, inDegree.getOrDefault(nb, 0) + 1);
            }
        }
        Queue<DirectedGraphNode> queue = new LinkedList<>();
        for (DirectedGraphNode node : graph) {
            int count = inDegree.getOrDefault(node, 0);
            if (count == 0) {
                queue.add(node);
                result.add(node);
            }
        }

        while (!queue.isEmpty()) {
            DirectedGraphNode cur = queue.poll();
            for (DirectedGraphNode next : cur.neighbors) {
                int nextInDegree = inDegree.get(next) - 1;
                inDegree.put(next, nextInDegree);
                if (nextInDegree == 0) {
                    queue.add(next);
                    result.add(next);
                }
            }
        }
        return result;
    }
}
