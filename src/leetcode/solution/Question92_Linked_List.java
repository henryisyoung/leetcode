package leetcode.solution;

public class Question92_Linked_List {
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if(m >= n || head == null || head.next == null) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        head = dummy;
        for (int i = 0; i < m - 1; i++){
            if(head == null) return head;
            head = head.next;
        }
        ListNode preM = head, nodeM = head.next, nodeN = head.next, pastN = nodeN.next;
        for(int i = m; i < n; i++){
            ListNode tmp = pastN.next;
            pastN.next = nodeN;
            nodeN = pastN;
            pastN = tmp;
        }
        preM.next = nodeN;
        nodeM.next = pastN;
        return dummy.next;
    }
}
