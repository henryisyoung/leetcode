package recovery;

import leetcode.linkedList.ListNode;

public class RemoveDuplicatesFromSortedList2 {
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode dummy = new ListNode(-1);
        ListNode cur = head;
        ListNode next = head.next;
        dummy.next = head;

        ListNode prev = dummy;

        while (next != null) {
            if (cur.val != next.val) {
                prev = cur;
                cur = next;
                next = next.next;
            } else {
                while (next != null && cur.val == next.val) {
                    next = next.next;
                }
                prev.next = next;
                cur = next;

                if (next != null) {
                    next = next.next;
                }
            }
        }

        return dummy.next;
    }
}
