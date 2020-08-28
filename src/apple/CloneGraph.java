package apple;

import java.util.*;

public class CloneGraph {
    class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        Map<Node, Node> map = new HashMap<>();
        Node newNode = new Node(node.val);
        map.put(node, newNode);

        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node n = queue.poll();
            Node copy = map.get(n);

            for (Node child : n.neighbors) {
                if (map.containsKey(child)) {
                    copy.neighbors.add(map.get(child));
                } else {
                    Node childCopy = new Node(child.val);
                    copy.neighbors.add(childCopy);
                    map.put(child, childCopy);
                    queue.add(child);
                }
            }
        }

        return map.get(node);
    }
}
