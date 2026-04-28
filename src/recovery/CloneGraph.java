package recovery;

import datastructure.graph.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CloneGraph {
    public Node cloneGraph(Node node) {
        if (node == null) {
            return null;
        }

        Map<Node, Node> map = new HashMap<>();
        Node copy = new Node(node.val);
        map.put(node, copy);
        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node old = queue.poll();
            Node oldCopy = map.get(old);
            for (Node child : old.neighbors) {
                if (map.containsKey(child)) {
                    Node childCopy = map.get(child);
                    oldCopy.neighbors.add(childCopy);
                } else {
                    Node childCopy = new Node(child.val);
                    oldCopy.neighbors.add(childCopy);
                    map.put(child, childCopy);
                    queue.add(child);
                }
            }
        }

        return copy;
    }
}
