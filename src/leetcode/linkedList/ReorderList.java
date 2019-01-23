package leetcode.linkedList;

public class ReorderList {
    public void reorderList(ListNode head) {
        if(head == null || head.next == null){
            return;
        }
        ListNode mid = findMid(head);
        ListNode list2 = reverse(mid.next);
        mid.next = null;
        ListNode list1 = head;
        merge(list1, list2);
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
