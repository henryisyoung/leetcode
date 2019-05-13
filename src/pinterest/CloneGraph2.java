package pinterest;

import java.util.*;

public class CloneGraph2 {
    class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {}

        public Node(int _val,List<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

    public Node cloneGraph(Node node) {
        if (node == null) {
            return node;
        }
        Map<Node, Node> map = new HashMap<>();
        Node copy = new Node(node.val, new ArrayList<>());
        map.put(node, copy);
        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            Node curCopy = map.get(cur);
            for (Node next : cur.neighbors) {
                if (!map.containsKey(next)) {
                    Node nextCopy = new Node(next.val, new ArrayList<>());
                    curCopy.neighbors.add(nextCopy);
                    queue.add(next);
                    map.put(next, nextCopy);
                } else {
                    Node nextCopy = map.get(next);
                    curCopy.neighbors.add(nextCopy);
                }
            }
        }

        return map.get(node);
    }
}
