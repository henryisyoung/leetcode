package leetcode.linkedList;

public class RemoveDuplicatesFromSortedList {
    public ListNode deleteDuplicates(ListNode head) {
        if(head==null||head.next==null) return head;
        ListNode fast = head.next;
        ListNode prev = head;
        while(fast != null){
            while(fast != null && fast.val == prev.val){
                fast = fast.next;
            }
            if(fast == null){
                prev.next=fast;
                return head;
            }
            prev.next = fast;
            prev = fast;
        }
        return head;
    }
    public ListNode deleteDuplicates2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode dummy = new ListNode(0);
        ListNode cur= head, next = head;
        dummy.next = head;
        while (next != null) {
            while (next != null && cur.val == next.val) {
                next = next.next;
            }
            if (next == null) {
                cur.next = next;
                break;
            }
            cur.next = next;
            cur = next;
        }
        return dummy.next;
    }

}
