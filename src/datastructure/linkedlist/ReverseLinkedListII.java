package datastructure.linkedlist;

import leetcode.linkedList.ListNode;

public class ReverseLinkedListII {
    public ListNode reverseBetween(ListNode head, int m, int n) {
        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        head = dummy;
        for(int i = 0; i < m - 1; i++) {
            head = head.next;
        }
        ListNode pm = head, mNode = head.next, nNode = head.next, pn = nNode.next;
        for(int i = m; i < n; i++) {
            ListNode tmp = pn.next;
            pn.next = mNode;
            mNode = pn;
            pn = tmp;
        }
        pm.next = mNode;
        nNode.next = pn;
        return dummy.next;
    }
}
