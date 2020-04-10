package facebook;

import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer {
    class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    public Node copyRandomList(Node head) {
        Node dummy = new Node(-1);
        Node cur = dummy;

        Map<Node, Node> map = new HashMap<>();

        while (head != null) {
            Node copy;
            if (map.containsKey(head)) {
                copy = map.get(head);
            } else {
                copy = new Node(head.val);
                map.put(head, copy);
            }
            cur.next = copy;
            cur = cur.next;

            if(head.random != null) {
                if (map.containsKey(head.random)) {
                    copy.random = map.get(head.random);
                } else {
                    Node random = new Node(head.random.val);
                    copy.random = random;
                    map.put(head.random, random);
                }
            }
            head = head.next;
        }

        return dummy.next;
    }
}
