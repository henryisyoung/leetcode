package intuit;

public class PopulatingNextRightPointersinEachNode {
    class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    };

    public Node connect(Node root) {
        if (root == null) {
            return root;
        }
        if (root.left == null && root.right == null) {
            return root;
        }
        root.left.next = root.right;
        if (root.next != null) {
            root.right.next = root.next.left;
        }
        connect(root.left);
        connect(root.right);
        return root;
    }

    public Node connect2(Node root) {
        if (root == null) {
            return root;
        }
        Node next = root.next;
        while (next != null) {
            if (next.left != null) {
                next = next.left;
                break;
            } else if (next.right != null) {
                next = next.right;
                break;
            }
            next = next.next;
        }
        if (root.right != null) {
            root.right.next = next;
        }
        if (root.left != null) {
            root.left.next = root.right == null ? next : root.right;
        }
        connect(root.right);
        connect(root.left);
        return root;
    }
}
