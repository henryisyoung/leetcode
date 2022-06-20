package datastructure.linkedlist;

import leetcode.linkedList.ListNode;

public class PartitionList {
    public ListNode partition(ListNode head, int x) {
        if (head == null || head.next == null) return head;
        ListNode a = new ListNode(-1), b = new ListNode(-1);
        ListNode aHead = a, bHead = b;

        while (head != null) {
            if (head.val < x) {
                aHead.next = head;
                aHead = aHead.next;
            } else {
                bHead.next = head;
                bHead = bHead.next;
            }
            head = head.next;
        }
        aHead.next = b.next;
        return a.next;
    }
}
