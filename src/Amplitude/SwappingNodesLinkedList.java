package Amplitude;

import leetcode.linkedList.ListNode;

public class SwappingNodesLinkedList {
    public ListNode swapNodes(ListNode head, int k) {
        ListNode dummy = new ListNode(-1);
        dummy.next = head;

        ListNode first = head;
        int n = k;
        while (n > 0) {
            first = first.next;
            n--;
        }
        ListNode slow = head;
        while (first != null) {
            first = first.next;
            slow = slow.next;
        }

        n = k;
        ListNode second = dummy;
        while (n > 0) {
            second = second.next;
            n--;
        }
        int val = second.val;
        second.val = slow.val;
        slow.val = val;
        return dummy.next;
    }
}
