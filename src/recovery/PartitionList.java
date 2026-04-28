package recovery;

import leetcode.linkedList.ListNode;

public class PartitionList {
    public ListNode partition(ListNode head, int x) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode aHead = new ListNode(-1), bHead = new ListNode(-1);
        ListNode aNode = aHead, bNode = bHead;
        while (head != null) {
            if (head.val < x) {
                aNode.next = new ListNode(head.val);
                aNode = aNode.next;
            } else {
                bNode.next = new ListNode(head.val);
                bNode = bNode.next;
            }
            head = head.next;
        }
        aNode.next = bHead.next;
        return aHead.next;
    }
}
