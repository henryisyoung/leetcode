package facebook;

public class SortCircularDoublyLinkedList {
    static class ListNode {

        int data;
        ListNode next, prev;

        // Constructor to create a new node
        ListNode(int d) {
            data = d;
            next = prev = null;
        }
    }

    public static ListNode sortCortCircularDoublyLinkedList(ListNode head) {
        if(head == null || head.next == null) return head;

        ListNode slow = head, fast = head;

        while (fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow){
                break;
            }

        }

        if(fast == null || fast.next == null) return null;

        fast = head;
        ListNode prev = slow.prev;
        while (fast != slow) {
            slow = slow.next;
            prev = prev.next;
            fast = fast.next;
        }

        prev.next = null;

        return mergeSort(head);
    }

    private static ListNode mergeSortHelper(ListNode first, ListNode second) {
        ListNode dm = new ListNode(0);
        ListNode cur = dm;
        while (first != null && second != null) {
            if (first.data < second.data) {
                cur.next = new ListNode(first.data);
                first = first.next;
            } else {
                cur.next = new ListNode(second.data);
                second = second.next;
            }
            cur = cur.next;
        }
        if (first != null) cur.next = first;
        if (second != null) cur.next = second;
        return dm.next;
    }

    private static ListNode mergeSort(ListNode node) {
        if (node == null || node.next == null) return node;
        ListNode mid = findMid(node);
        ListNode second = mergeSort(mid.next);

        mid.next.prev = null;
        mid.next = null;
        ListNode first = mergeSort(node);

        return mergeSortHelper(first, second);
    }

    private static ListNode findMid(ListNode head) {
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    public static void main(String[] args) {
        ListNode a = new ListNode(7);
        ListNode b = new ListNode(16);
        ListNode c = new ListNode(5);
        ListNode d = new ListNode(3);
        ListNode e = new ListNode(4);
        ListNode f = new ListNode(22);
        a.next = b;
        b.prev = a;
        b.next = c;
        c.prev = b;
        c.next = d;
        d.prev = c;
        d.next = e;
        e.prev = d;
        e.next = f;
        f.prev = e;
        f.next = b;
        ListNode result = sortCortCircularDoublyLinkedList(a);
        while (result != null) {
            System.out.println(result.data);
            result = result.next;
        }
    }
}
