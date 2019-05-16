package leetcode.binaryTree;

import java.util.LinkedList;
import java.util.Queue;

public class PopulatingNextRightPointersInEachNodeII {
    class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {}

        public Node(int _val,Node _left,Node _right,Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }

    public Node connect(Node root) {
        if(root == null) {
            return root;
        }
        Node next = root.next;
        while (next != null) {
            if (next.left != null) {
                next = next.left;
                break;
            }
            if (next.right != null) {
                next = next.right;
                break;
            }
            next = next.next;
        }
        if (root.right != null) {
            root.right.next = next;
        }
        if (root.left != null) {
            root.left.next = root.right != null ? root.right : next;
        }
        connect(root.right);
        connect(root.left);
        return root;
    }
}
