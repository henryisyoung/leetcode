package leetcode.linkedList;

public class ReverseLinkedListII {
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if(m >= n || head == null || head.next == null) {
            return head;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        head = dummy;
        for (int i = 0; i < (m - 1); i++){
            if(head == null){
                return head;
            }
            head = head.next;
        }
        ListNode preM = head, mNode = head.next, nNode = head.next, postNode = nNode.next;
        for(int i = m; i < n; i++){
            ListNode tmp = postNode.next;
            postNode.next = nNode;
            nNode = postNode;
            postNode = tmp;
        }
        preM.next = nNode;
        mNode.next = postNode;

        return dummy.next;
    }
    public ListNode reverseBetween2(ListNode head, int m, int n) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        int count = m;
        head = dummy;
        while (count > 0) {
            count--;
            if (head == null) {
                return  head;
            }
            head = head.next;
        }
        ListNode prevM = head, mNode = head.next, nNode = head.next, postN = nNode.next;
        for (int i = m; i < n; i++) {
            ListNode tmp = postN.next;
            postN.next = nNode;
            nNode = postN;
            postN = tmp;
        }
        prevM.next = nNode;
        mNode.next = postN;
        return dummy.next;
    }
}
