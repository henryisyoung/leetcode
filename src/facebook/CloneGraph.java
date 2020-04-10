package facebook;

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
        if(node == null) return null;
        Map<Node, Node> map = new HashMap<>();
        map.put(node, new Node(node.val, new ArrayList<>()));

        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while(!queue.isEmpty()) {
            Node n = queue.poll();
            Node cur = map.get(n);
            for (Node child : n.neighbors) {
                if (!map.containsKey(child)) {
                    queue.add(child);
                    Node newChild = new Node(child.val, new ArrayList<>());
                    cur.neighbors.add(newChild);
                    map.put(child, newChild);
                } else {
                    cur.neighbors.add(map.get(child));
                }
            }
        }
        return map.get(node);
    }
}
