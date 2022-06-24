package apple;

import leetcode.linkedList.ListNode;

public class RemoveNthNodeFromEndList {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode prev = dummy;
        ListNode first = head;
        while (n-- > 0) {
            first = first.next;
        }
        while (first != null) {
            first = first.next;
            prev = prev.next;
        }
        prev.next = prev.next.next;
        return dummy.next;
    }
}
