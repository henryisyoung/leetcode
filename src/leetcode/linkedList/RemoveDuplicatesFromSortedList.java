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
}
