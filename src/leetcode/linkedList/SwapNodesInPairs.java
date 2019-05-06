package leetcode.linkedList;

import java.util.List;

public class SwapNodesInPairs {
    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode pre = head;
        ListNode cur = head.next;
        while (cur != null && cur.next != null) {
            int val = cur.val;
            cur.val = pre.val;
            pre.val = val;
            pre = pre.next.next;
            cur = cur.next.next;
        }
        if (cur != null) {
            int val = cur.val;
            cur.val = pre.val;
            pre.val = val;
        }
        return head;
    }
}
