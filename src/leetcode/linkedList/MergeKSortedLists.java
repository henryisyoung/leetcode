package leetcode.linkedList;

public class MergeKSortedLists {
    public ListNode mergeKLists(ListNode[] lists) {
        if(lists == null || lists.length == 0){
            return null;
        }

        return mergeListHelper(lists, 0, lists.length - 1);
    }

    private ListNode mergeListHelper(ListNode[] lists, int left, int right) {
        if (left == right) {
            return lists[left];
        } else if(left == right) {
            return mergeList(lists[left], lists[right]);
        } else {
            int mid = left + (right - left)/2;
            ListNode leftList = mergeListHelper(lists, left, mid);
            ListNode rightList = mergeListHelper(lists, mid + 1, right);
            return mergeList(leftList, rightList);
        }
    }

    private ListNode mergeList(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (list1 != null && list2 != null) {
            if(list1.val < list2.val){
                cur.next = new ListNode(list1.val);
                list1 = list1.next;
            } else {
                cur.next = new ListNode(list2.val);
                list2 = list2.next;
            }
            cur = cur.next;
        }
        if(list1 != null){
            cur.next = list1;
        }
        if(list2 != null) {
            cur.next = list2;
        }
        return dummy.next;
    }
}
