package leetcode;

public class Question148_Linked_List {
    public ListNode sortList(ListNode head) {
        if(head == null || head.next == null){
            return head;
        }
        ListNode mid = find_mid(head);
        ListNode right = sortList(mid.next);
        mid.next = null;
        ListNode left = sortList(head);
        return merge(left, right);
    }

    private ListNode merge(ListNode left, ListNode right) {
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

    private ListNode find_mid(ListNode head) {
        // comment: be sure that fast and slow cannot be the same node example 2 -> 4 cannot divided if they are the same
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    public ListNode quicksortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode leftDM = new ListNode(0), lc = leftDM;
        ListNode rightDM = new ListNode(0), rc = rightDM;
        ListNode midDM = new ListNode(0), midc = midDM;
        ListNode p = find_mid(head);
        while(head != null){
            if(head.val < p.val){
                lc.next = new ListNode(head.val);
                lc = lc.next;
            }else if(head.val > p.val){
                rc.next = new ListNode(head.val);
                rc = rc.next;
            }else {
                midc.next = new ListNode(head.val);
                midc = midc.next;
            }
            head = head.next;
        }
        lc.next = null;
        rc.next = null;
        midc.next = null;
        ListNode left = quicksortList(leftDM.next);
        ListNode right = quicksortList(rightDM.next);
        return concate(left, midDM.next, right);
    }

    private ListNode concate(ListNode left, ListNode mid, ListNode right) {
        ListNode dummy = new ListNode(0), tail = dummy;

        tail.next = left; tail = getTail(tail);
        tail.next = mid; tail = getTail(tail);
        tail.next = right;
        return dummy.next;
    }

    private ListNode getTail(ListNode head) {
        if(head == null || head.next == null) return head;
        while(head.next != null) head = head.next;
        return head;
    }
}
