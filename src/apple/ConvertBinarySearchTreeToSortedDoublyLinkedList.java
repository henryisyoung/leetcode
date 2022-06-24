package apple;

public class ConvertBinarySearchTreeToSortedDoublyLinkedList {
    class Node {
        public int val;
        public Node left;
        public Node right;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val,Node _left,Node _right) {
            val = _val;
            left = _left;
            right = _right;
        }
    };

    Node first, last;
    public Node treeToDoublyList(Node root) {
        if (root == null) return root;
        treeToDoublyListHelper(root);
        first.left = last;
        last.right = first;
        return first;
    }

    private void treeToDoublyListHelper(Node root) {
        if (root == null) return;
        treeToDoublyList(root.left);
        if (last == null) {
            first = root;
        } else {
            last.right = root;
            root.left = last;
        }
        last = root;
        treeToDoublyList(root.right);
    }
}
