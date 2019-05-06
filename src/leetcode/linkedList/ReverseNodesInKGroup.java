package leetcode.linkedList;

public class ReverseNodesInKGroup {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || head.next == null || k == 1) {
            return head;
        }
        return reverseHelper(head, k);
    }

    private ListNode reverseHelper(ListNode head, int k) {
        if (!isValid(head, k)) {
            return head;
        }
        ListNode cur = head.next, pre = head;
        int count = k;
        while (count > 1) {
            ListNode tem = cur.next;
            cur.next = pre;
            pre = cur;
            cur = tem;
            count--;
        }
        head.next = reverseHelper(cur, k);
        return pre;
    }

    private boolean isValid(ListNode head, int k) {
        int count = 0;
        ListNode cur = head;
        while (cur != null) {
            count++;
            cur = cur.next;
        }
        return count <= k;
    }
}
