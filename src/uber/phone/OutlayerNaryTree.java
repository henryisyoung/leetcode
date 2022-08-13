package uber.phone;

import java.util.*;

public class OutlayerNaryTree {
    static class Node{
        int val;
        List<Node> children;
        public Node(int val, List<Node> children) {
            this.val = val;
            this.children = children;
        }
    }
    public static List<Integer> findOut(Node root) {
        List<Integer> result = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        List<Integer> leftSide = new ArrayList<>();
        List<Integer> rightSide = new ArrayList<>();
        while (!queue.isEmpty()) {
            int size = queue.size();
            Node last = null;
            Node first = null;
            for (int i = 0; i < size; i++) {
                Node cur = queue.poll();
                for (Node child : cur.children) {
                    queue.add(child);
                    last = child;
                    if (first == null) {
                        first = child;
                    }
                }
            }
            if (first != null) {
                leftSide.add(first.val);
                rightSide.add(last.val);
            }

        }
        Collections.reverse(leftSide);
        result.addAll(leftSide);
        result.add(root.val);
        result.addAll(rightSide);
        return result;
    }


    public static void main(String[] args) {
        Node e = new Node(12, new ArrayList<>());
        Node c = new Node(7, Arrays.asList());
        Node t = new Node(1, Arrays.asList());
        Node f = new Node(8, Arrays.asList());
        Node b = new Node(5, Arrays.asList(e,t));
        Node d = new Node(8, Arrays.asList(f));

        Node root = new Node(4, Arrays.asList(b , c,d));
        System.out.println(findOut(root));
    }

}
