package leetcode.linkedList;

public class ReverseLinkedList {
    public ListNode reverseList(ListNode head) {
        if(head == null || head.next == null) {
            return head;
        }
        ListNode prev = null;
        while (head != null) {
            ListNode tmp = head.next;
            head.next = prev;
            prev = head;
            head = tmp;
        }
        return prev;
    }

    public ListNode reverseList2(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode tmp = head.next;
            head.next = prev;
            prev = head;
            head = tmp;
        }
        return prev;
    }
}
