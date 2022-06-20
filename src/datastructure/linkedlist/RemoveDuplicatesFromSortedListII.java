package datastructure.linkedlist;

import leetcode.linkedList.ListNode;

public class RemoveDuplicatesFromSortedListII {
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode prev = dummy;
        ListNode cur = head, next = head.next;

        while (next != null) {
            if (cur.val != next.val) {
                prev = cur;
                cur = next;
                next = next.next;
            } else {
                while (next != null && cur.val == next.val) {
                    next = next.next;
                }
                if (next == null) {
                    prev.next = null;
                } else {
                    prev.next = next;
                    cur = next;
                    next = next.next;
                }
            }
        }
        return dummy.next;
    }
}
