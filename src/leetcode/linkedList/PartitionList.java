package leetcode.linkedList;

public class PartitionList {
    public ListNode partition(ListNode head, int x) {
        if(head == null || head.next == null){
            return head;
        }
        ListNode dummy = new ListNode(0);
        ListNode dummy2 = new ListNode(0);
        ListNode small = dummy, great = dummy2;
        while (head != null) {
            int val = head.val;
            if(val < x) {
                small.next = new ListNode(val);
                small = small.next;
            } else {
                great.next = new ListNode(val);
                great = great.next;
            }
            head = head.next;
        }
        small.next = dummy2.next;
        great.next = null;

        return dummy.next;
    }

    public ListNode partition2(ListNode head, int x) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode dummy1 = new ListNode(0);
        ListNode dummy2 = new ListNode(0);
        ListNode smaller = dummy1;
        ListNode greater = dummy2;
        while (head != null) {
            if (head.val < x) {
                smaller.next = new ListNode(head.val);
                smaller = smaller.next;
            } else {
                greater.next = new ListNode(head.val);
                greater = greater.next;
            }
            head = head.next;
        }
        smaller.next = dummy2.next;
        greater.next = null;
        return dummy1.next;
    }
}
