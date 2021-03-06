package leetcode.linkedList;

import leetcode.solution.ListNode;

public class AddTwoNumbers {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(0);
        ListNode cur = head;
        int val = 0;
        while (l1 != null || l2 != null){

            if(l1 != null){
                val += l1.val;
                l1 = l1.next;
            }
            if(l2 != null){
                val += l2.val;
                l2 = l2.next;
            }
            cur.next = new ListNode(val % 10);
            cur = cur.next;
            val /= 10;
        }
        if(val == 1){
            cur.next = new ListNode(1);
        }
        return head.next;
    }

    public ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        int val = 0;
        while (l1 != null || l2 != null) {
            if (l1 != null) {
                val += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                val += l2.val;
                l2 = l2.next;
            }
            cur.next = new ListNode(val % 10);
            val /= 10;
            cur = cur.next;
        }
        if (val != 0) {
            cur.next = new ListNode(val);
        }
        return dummy.next;
    }
}
