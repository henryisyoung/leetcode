package recovery;

import leetcode.linkedList.ListNode;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MergeKSortedLists {

    public ListNode mergeKLists(ListNode[] lists) {
        ListNode dummy = new ListNode(-1);
        ListNode head = dummy;
        PriorityQueue<ListNode> minPQ = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));

        for (ListNode node : lists) {
            minPQ.add(node);
        }

        while (!minPQ.isEmpty()) {
            ListNode cur = minPQ.poll();
            head.next = cur;
            head = head.next;
            if (cur.next != null) {
                minPQ.add(cur.next);
            }
        }

        return dummy.next;
    }

    public ListNode mergeKLists2(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        return mergeHelper(lists, 0, lists.length - 1);
    }

    private ListNode mergeHelper(ListNode[] lists, int l, int r) {
        if (l >= r) return lists[l];
        if (l + 1 == r) return mergeTwoList(lists[l], lists[r]);
        int mid = (l + r) / 2;
        ListNode leftPart = mergeHelper(lists, l, mid);
        ListNode rightPart = mergeHelper(lists, mid + 1, r);
        return mergeTwoList(leftPart, rightPart);
    }

    private ListNode mergeTwoList(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                cur.next = list1;
                list1 = list1.next;
            } else {
                cur.next = list2;
                list2 = list2.next;
            }
            cur = cur.next;
        }
        if (list1 != null) cur.next = list1;
        if (list2 != null) cur.next = list2;
        return dummy.next;
    }
}