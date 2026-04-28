package recovery;

import leetcode.linkedList.ListNode;

public class RemoveDuplicatesFromSortedList {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = head;
        dummy.next = head;

        ListNode prev = head;

        while (cur != null) {
            while (cur != null && cur.val == prev.val) {
                cur = cur.next;
            }
            prev.next = cur;
            prev = cur;
            if (cur != null) {
                cur = cur.next;
            }
        }

        return dummy.next;
    }
}
