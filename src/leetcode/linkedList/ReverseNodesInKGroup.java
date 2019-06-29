package leetcode.linkedList;

public class ReverseNodesInKGroup {
    public ListNode reverseKGroup(ListNode head, int k) {
        if(head==null || k==1) return head;
        if(! isValidPhase(head, k)) return head;
        int i = k;
        ListNode cur = head.next, prev = head;
        while(i > 1){
            ListNode tmp = cur.next;
            cur.next = prev;
            prev = cur;
            cur = tmp;
            i--;
        }
        head.next = reverseKGroup(cur,k);
        return prev;
    }

    private boolean isValidPhase(ListNode head, int k) {
        if(head==null) return false;
        ListNode cur = head;
        while(cur!=null){
            k--;
            cur=cur.next;
        }
        return k<=0;
    }
}
