package apple;

import java.util.List;

public class MergekSortedLists {
    public class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        int left = 0, right = lists.length - 1;
        return mergeKListsHelper(left, right, lists);
    }

    private ListNode mergeKListsHelper(int left, int right, ListNode[] lists) {
        if (left >= right) {
            return lists[left];
        }
        int mid = left + (right - left) /2;
        ListNode first = mergeKListsHelper(left, mid, lists);
        ListNode second = mergeKListsHelper(mid + 1, right, lists);
        return mergeTwoLists(first, second);
    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        if (l1 != null) cur.next = l1;
        if (l2 != null) cur.next = l2;

        return dummy.next;
    }
}
