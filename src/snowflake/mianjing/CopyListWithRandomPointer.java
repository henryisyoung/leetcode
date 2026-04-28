package snowflake.mianjing;


import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer {

    // Definition for a Node.
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

            if (head.random != null) {
                Node random = head.random;
                Node copyRandom;
                if (map.containsKey(random)) {
                    copyRandom = map.get(random);
                } else {
                    copyRandom = new Node(random.val);
                    map.put(random, copyRandom);
                }
                copy.random = copyRandom;
            }
            head = head.next;
        }

        return dummy.next;
    }
}
