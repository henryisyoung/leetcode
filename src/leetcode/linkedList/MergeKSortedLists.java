package leetcode.linkedList;

public class MergeKSortedLists {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        return mergeListHelper(lists, 0, lists.length - 1);
    }

    private ListNode mergeListHelper(ListNode[] lists, int left, int right) {
        if (left == right) {
            return lists[left];
        } else if (left == right + 1) {
            return mergeList(lists[left], lists[right]);
        } else {
            int mid = left + (right - left) / 2;
            ListNode leftList = mergeListHelper(lists, left, mid);
            ListNode rightList = mergeListHelper(lists, mid + 1, right);
            return mergeList(leftList, rightList);
        }
    }

    private ListNode mergeList(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                cur.next = new ListNode(list1.val);
                list1 = list1.next;
            } else {
                cur.next = new ListNode(list2.val);
                list2 = list2.next;
            }
            cur = cur.next;
        }
        if (list1 != null) {
            cur.next = list1;
        }
        if (list2 != null) {
            cur.next = list2;
        }
        return dummy.next;
    }

    public ListNode mergeKLists2(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        return mergerHelper(lists, 0, lists.length - 1);
    }

    private ListNode mergerHelper(ListNode[] lists, int start, int end) {
        if (start == end) {
            return lists[start];
        }
        if (start + 1 == end) {
            return mergeTwoList(lists[start], lists[end]);
        }
        int mid = start + (end - start) / 2;
        ListNode left = mergerHelper(lists, start, mid);
        ListNode right = mergerHelper(lists, mid + 1, end);
        return mergeTwoList(left, right);
    }

    private ListNode mergeTwoList(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (l1 != null && l2 != null) {
            if(l1.val < l2.val) {
                cur.next = new ListNode(l1.val);
                l1 = l1.next;
            } else {
                cur.next = new ListNode(l2.val);
                l2 = l2.next;
            }
            cur = cur.next;
        }
        if (l1 != null) {
            cur.next = l1;
        }
        if (l2 != null) {
            cur.next = l2;
        }
        return dummy.next;
    }


}
