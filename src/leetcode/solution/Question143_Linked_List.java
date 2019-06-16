package leetcode.solution;

public class Question143_Linked_List {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode mid = find_mid(head);
        ListNode newHead = reverse(mid.next);
        mid.next = null;
        merge(head, newHead);
    }

    private void merge(ListNode l1, ListNode l2) {
        ListNode dm = new ListNode(0);
        int count = 0;
        while(l1 != null && l2 != null){
            if(count % 2 == 0){
                dm.next = l1;
                l1 = l1.next;
            }else{
                dm.next = l2;
                l2 = l2.next;
            }
            count++;
            dm = dm.next;
        }
        if(l1 != null){
            dm.next = l1;
        }else{
            dm.next = l2;
        }
    }

    private ListNode reverse(ListNode node) {
        ListNode prev = null;
        while (node != null){
            ListNode tmp = node.next;
            node.next = prev;
            prev = node;
            node = tmp;
        }
        return prev;
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
}
