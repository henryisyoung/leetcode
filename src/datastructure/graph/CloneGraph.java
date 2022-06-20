package datastructure.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CloneGraph {
    public Node cloneGraph(Node node) {
        if (node == null) return node;
        Map<Node, Node> map = new HashMap<>();
        Node newNode = new Node(node.val);
        map.put(node, newNode);

        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            Node copy = map.get(cur);
            for (Node next : cur.neighbors) {
                if (map.containsKey(next)) {
                    Node nextCopy = map.get(next);
                    copy.neighbors.add(nextCopy);
                } else {
                    Node nextCopy = new Node(next.val);
                    copy.neighbors.add(nextCopy);
                    map.put(next, nextCopy);
                    queue.add(next);
                }
            }
        }
        return map.get(node);
    }
}
