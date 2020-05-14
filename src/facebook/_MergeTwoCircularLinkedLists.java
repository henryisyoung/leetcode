package facebook;
//https://www.geeksforgeeks.org/sorted-merge-of-two-sorted-doubly-circular-linked-lists/
public class _MergeTwoCircularLinkedLists {
    static class Node {
        int data;
        Node next, prev;
    };

    // function for Sorted merge of two
    // sorted doubly linked list
    static Node merge(Node first, Node second) {
        // If first list is empty
        if (first == null)
            return second;

        // If second list is empty
        if (second == null)
            return first;

        // Pick the smaller value and adjust
        // the links
        if (first.data < second.data) {
            first.next = merge(first.next, second);
            first.next.prev = first;
            first.prev = null;
            return first;
        } else {
            second.next = merge(first, second.next);
            second.next.prev = second;
            second.prev = null;
            return second;
        }
    }

    // function for Sorted merge of two sorted
    // doubly circular linked list
    static Node mergeUtil(Node head1, Node head2) {
        // if 1st list is empty
        if (head1 == null)
            return head2;

        // if 2nd list is empty
        if (head2 == null)
            return head1;

        // get pointer to the node which will be the
        // last node of the final list
        Node last_node;
        if (head1.prev.data < head2.prev.data)
            last_node = head2.prev;
        else
            last_node = head1.prev;

        // store null to the 'next' link of the last nodes
        // of the two lists
        head1.prev.next = head2.prev.next = null;

        // sorted merge of head1 and head2
        Node finalHead = merge(head1, head2);

        // 'prev' of 1st node pointing the last node
        // 'next' of last node pointing to 1st node
        finalHead.prev = last_node;
        last_node.next = finalHead;

        return finalHead;
    }
}
