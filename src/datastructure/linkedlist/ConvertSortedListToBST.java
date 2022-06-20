package datastructure.linkedlist;

import Bloomberg.TreeNode;
import leetcode.linkedList.ListNode;

public class ConvertSortedListToBST {
    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) return null;

        if (head.next == null) {
            return new TreeNode(head.val);
        }

        ListNode mid = findMid(head);
        TreeNode left = sortedListToBST(head);
        TreeNode right = sortedListToBST(mid.next);
        TreeNode cur = new TreeNode(mid.val);
        cur.left = left;
        cur.right = right;

        return cur;
    }

    private ListNode findMid(ListNode head) {
        ListNode prevPtr = null;
        ListNode slowPtr = head;
        ListNode fastPtr = head;

        // Iterate until fastPr doesn't reach the end of the linked list.
        while (fastPtr != null && fastPtr.next != null) {
            prevPtr = slowPtr;
            slowPtr = slowPtr.next;
            fastPtr = fastPtr.next.next;
        }

        // Handling the case when slowPtr was equal to head.
        if (prevPtr != null) {
            prevPtr.next = null;
        }

        return slowPtr;
    }
}
