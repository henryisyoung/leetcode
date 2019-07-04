package leetcode.linkedList;

public class SortList {
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode mid = findMid(head);
        ListNode second = sortList(mid.next);
        mid.next = null;
        ListNode first = sortList(head);
        return mergeList(first, second);
    }

    private ListNode mergeList(ListNode first, ListNode second) {
        ListNode dm = new ListNode(0);
        ListNode cur = dm;
        while (first != null && second != null) {
            if (first.val < second.val) {
                cur.next = new ListNode(first.val);
                first = first.next;
            } else {
                cur.next = new ListNode(second.val);
                second = second.next;
            }
            cur = cur.next;
        }
        if (first != null) cur.next = first;
        if (second != null) cur.next = second;
        return dm.next;
    }

    private ListNode findMid(ListNode head) {
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
}
