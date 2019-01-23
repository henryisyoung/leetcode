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
}
