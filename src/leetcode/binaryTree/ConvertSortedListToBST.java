package leetcode.binaryTree;

import leetcode.linkedList.ListNode;

import java.util.ArrayList;
import java.util.List;

public class ConvertSortedListToBST {
    ListNode cur;
    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) {
            return null;
        }
        cur = head;
        int size = 0;
        while (head != null) {
            size++;
            head = head.next;
        }
        return sortedListToBSTHelper(size);
    }

    private TreeNode sortedListToBSTHelper(int size) {
        if (size <= 0) {
            return null;
        }
        TreeNode left = sortedListToBSTHelper(size/2);
        TreeNode root = new TreeNode(cur.val);
        cur = cur.next;
        TreeNode right = sortedListToBSTHelper(size - 1 - size/2);
        root.left = left;
        root.right = right;
        return root;
    }
}
