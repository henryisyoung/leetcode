package leetcode.graphAndSearch;

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
       if(node == null) {
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
                   Node copyNext = new Node(next.val, new ArrayList<>());
                   queue.add(next);
                   map.put(next, copyNext);
                   curCopy.neighbors.add(copyNext);
               } else {
                   Node copyNext = map.get(next);
                   curCopy.neighbors.add(copyNext);
               }
           }
       }
       return copy;
    }
}
