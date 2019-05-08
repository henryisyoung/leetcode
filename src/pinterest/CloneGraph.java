package pinterest;

import java.util.*;

public class CloneGraph {
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
        Node copyNode = new Node(node.val, new ArrayList<>());
        map.put(node, copyNode);

        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            Node copy = map.get(cur);
            for (Node next : cur.neighbors) {
                if (!map.containsKey(next)){
                    queue.add(next);
                    Node nextCopy = new Node(next.val, new ArrayList<>());
                    copy.neighbors.add(nextCopy);
                    map.put(next, nextCopy);
                } else {
                    Node nextCopy = map.get(next);
                    copy.neighbors.add(nextCopy);
                }
            }
        }

        return map.get(node);
    }
}
