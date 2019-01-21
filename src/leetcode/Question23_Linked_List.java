package leetcode;

public class Question23_Linked_List {
    public ListNode mergeKLists(ListNode[] lists) {
        if(lists == null || lists.length == 0){
            return null;
        }
        return merger(lists, 0, lists.length - 1);
    }

    private ListNode merger(ListNode[] lists, int l, int r) {
        if(l == r){
            return lists[l];
        }else if (l + 1 == r){
            return mergeHelper(lists[l], lists[r]);
        }else {
            int mid = l + (r - l)/2;
            ListNode left = merger(lists,l, mid);
            ListNode right = merger(lists,mid + 1, r);
            return mergeHelper(left, right);
        }
    }

    private ListNode mergeHelper(ListNode left, ListNode right) {
        ListNode dm = new ListNode(0);
        ListNode cur = dm;
        while(left != null && right != null){
            if(left.val < right.val){
                cur.next = new ListNode(left.val);
                left = left.next;
            }else {
                cur.next = new ListNode(right.val);
                right = right.next;
            }
            cur = cur.next;
        }
        if(left != null) cur.next = left;
        if(right != null) cur.next = right;
        return dm.next;
    }
}
