package leetcode.linkedList;

public class RemoveLinkedListElements {
    public ListNode removeElements(ListNode head, int val) {
        if(head == null) {
            return head;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        head = dummy;
        while (head.next != null) {
            if(head.next.val == val) {
                head.next = head.next.next;
            } else {
                head = head.next;
            }
        }
        return dummy.next;
    }
    public ListNode removeElements2(ListNode head, int val) {
        if (head == null) {
            return head;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy, cur = head;
        while (cur != null) {
            while (cur != null && cur.val == val) {
                cur = cur.next;
            }
            prev.next = cur;
            if (cur == null) {
                break;
            }
            cur = cur.next;
            prev = prev.next;
        }

        return dummy.next;
    }
}
