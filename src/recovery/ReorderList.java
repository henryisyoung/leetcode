package recovery;

import leetcode.linkedList.ListNode;

public class ReorderList {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }
        ListNode mid = findMid(head);
        ListNode secondList = reverse(mid.next);
        mid.next = null;
        merge(head, secondList);
    }

    private void merge(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(-1);
        int count = 0;
        while (list1 != null && list2 != null) {
            if (count % 2 == 0) {
                dummy.next = list1;
                list1 = list1.next;
            } else {
                dummy.next = list2;
                list2 = list2.next;
            }
            count++;
            dummy = dummy.next;
        }
        if (list1 != null) dummy.next = list1;
        if (list2 != null) dummy.next = list2;
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode tmp = head.next;
            head.next = prev;
            prev = head;
            head = tmp;
        }
        return prev;
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
