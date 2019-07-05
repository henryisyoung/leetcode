package leetcode.linkedList;

public class RotateList {
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) {
            return head;
        }
        int len = 1;
        ListNode cur = head;
        while (cur.next != null) {
            len++;
            cur = cur.next;
        }
        if (k % len == 0) return head;
        k %= len;
        k = len - k;
        ListNode slow = head;
        while (k > 0) {
            k--;
            cur.next = new ListNode(slow.val);
            slow = slow.next;
            cur = cur.next;
        }
        return slow;
    }
}
