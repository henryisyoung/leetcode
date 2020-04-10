package facebook;

import Bloomberg.TreeNode;
import leetcode.linkedList.ListNode;

public class ConvertSortedListToBinarySearchTree {
    ListNode node;
    public TreeNode sortedListToBST(ListNode head) {
        int size = findSize(head);
        this.node = head;
        return helper(size);
    }

    private TreeNode helper(int size) {
        if (size == 0) return null;
        TreeNode left = helper(size / 2);
        TreeNode root= new TreeNode(node.val);
        node = node.next;
        TreeNode right = helper(size - size / 2 -1);
        root.left = left;
        root.right = right;
        return root;
    }

    private int findSize(ListNode head) {
        int size = 0;
        while (head != null) {
            size++;
            head = head.next;
        }
        return size;
    }
}