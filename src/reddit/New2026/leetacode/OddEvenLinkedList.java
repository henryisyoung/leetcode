package reddit.New2026.leetacode;

import leetcode.linkedList.ListNode;

import java.util.List;

public class OddEvenLinkedList {
    public ListNode oddEvenList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode a = head, b = head.next, c = head.next;

        while (a != null && b != null && b.next != null) {
            a.next = b.next;
            a = a.next;

            b.next = a.next;
            b = b.next;
        }

        a.next = c;
        return head;
    }
}
