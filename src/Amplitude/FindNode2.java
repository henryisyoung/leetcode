package Amplitude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FindNode2 {
    static class Node{
        int val;
        List<Node> children;
        Node parent;
        public Node(int val, List<Node> children) {
            this.val = val;
            this.children = children;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + val +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return val == node.val && Objects.equals(children, node.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, children);
        }
    }

    public static Node findNode(Node root1, Node root2, Node a) {
        if (a.equals(root2)) {
            return root2;
        }
        List<Integer> posList = new ArrayList<>();

        while (a != null && a != root1) {
            if (a.parent == null) {
                throw new IllegalStateException("no valid parent node");
            }
            int pos = findPos(a.parent, a);
            if (pos == -1) {
                throw new IllegalStateException("not valid node");
            }
            posList.add(0, pos);
            a = a.parent;
        }
        Node cur = root2;
        for (int i = 0; i < posList.size(); i++) {
            int pos = posList.get(i);
            cur = cur.children.get(pos);
        }
        return cur;
    }

    private static int findPos(Node parent, Node a) {
        int pos = parent.children.indexOf(a);
        return pos;
    }

    public static void test() {
        Node node6 = new Node(6, Arrays.asList());
        Node node5 = new Node(5, Arrays.asList());
        Node node4 = new Node(4, Arrays.asList());
        Node node3 = new Node(3, Arrays.asList());
        Node node2 = new Node(2, Arrays.asList(node4, node5, node6));
        Node root1 = new Node(1, Arrays.asList(node2, node3));
        node2.parent = root1;
        node3.parent = root1;
        node4.parent = node2;
        node5.parent = node2;
        node6.parent = node2;

        Node node26 = new Node(6, Arrays.asList());
        Node node25 = new Node(5, Arrays.asList());
        Node node24 = new Node(4, Arrays.asList());
        Node node23 = new Node(3, Arrays.asList());
        Node node22 = new Node(2, Arrays.asList(node24, node25, node26));
        Node root2 = new Node(1, Arrays.asList(node22, node23));
        node22.parent = root2;
        node23.parent = root2;
        node24.parent = node22;
        node25.parent = node22;
        node26.parent = node22;

        Node bad = new Node(12, new ArrayList<>());

        try {
            System.out.println(findNode(root1, root2, node5).equals(node25));
            System.out.println(findNode(root1, root2, node4).equals(node24));
            System.out.println(findNode(root1, root2, root1).equals(root2));
            System.out.println(findNode(root1, root2, node5).equals(node23));
            System.out.println(findNode(root1, root2, bad).equals(node23));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        test();
    }
}
