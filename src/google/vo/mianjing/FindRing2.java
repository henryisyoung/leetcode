package google.vo.mianjing;

import java.util.*;

public class FindRing2 {
    /*
    graph traversal. Graph is stored in dictionary with keys as nodes and values as neighboring nodes.
    Guaranteed to be a ring, return the path either counterclockwise or clockwise following the ring
    until you get all nodes in the ring.
     */

    static class Node{
        int val;
        int prev;
        Set<Integer> path;
        public Node(int val) {
            this.val = val;
            this.prev = -1;
            this.path = new LinkedHashSet<>();
        }
    }
    public static Set<Integer> findRing(Map<Integer, List<Integer>> map) {
        Node root = new Node(5);
        root.path.add(5);
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node cur = queue.poll();
                if (map.containsKey(cur.val)) {
                    for (int next : map.get(cur.val)) {
                        if (next != cur.prev) {
                            if (cur.path.contains(next)) {
                                return cur.path;
                            }
                            Node nextNode = new Node(next);
                            nextNode.prev = cur.val;
                            nextNode.path = new LinkedHashSet<>(cur.path);
                            nextNode.path.add(next);
                            queue.add(nextNode);
                        }
                    }
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(0, Arrays.asList(1));
        map.put(1, Arrays.asList(0,4));
        map.put(2, Arrays.asList(0,3));
        map.put(3, Arrays.asList(2,4));
        map.put(4, Arrays.asList(1,5,6,3));
        map.put(5, Arrays.asList(4));
        map.put(6, Arrays.asList(4));

        System.out.println(findRing(map));
    }
}
