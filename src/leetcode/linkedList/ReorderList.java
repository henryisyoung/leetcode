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

    public void reorderList2(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }
        ListNode mid = findMid2(head);
        ListNode right = reverse2(mid.next);
        mid.next = null;
        merge2(head, right);
    }

    private void merge2(ListNode left, ListNode right) {
        ListNode dummy = new ListNode(0);
        int pos = 0;
        while (left != null && right != null) {
            if (pos % 2 == 0) {
                dummy.next = left;
                left = left.next;
            } else {
                dummy.next = right;
                right = right.next;
            }
            pos++;
            dummy = dummy.next;
        }
        if (left != null) {
            dummy.next = left;
        }
        if (right != null) {
            dummy.next = right;
        }
    }

    private ListNode reverse2(ListNode list) {
        ListNode prev = null;
        while (list != null) {
            ListNode tmp = list.next;
            list.next = prev;
            prev = list;
            list = tmp;
        }
        return prev;
    }

    private ListNode findMid2(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast. next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }
}
