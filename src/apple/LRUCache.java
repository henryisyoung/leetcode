package apple;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    Node head, tail;
    Map<Integer, Node> map;
    int cap;
    public LRUCache(int capacity) {
        this.cap = capacity;
        this.map = new HashMap<>();
        this.head = new Node(-1,-1);
        this.tail = new Node(-1,-1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if(!map.containsKey(key)) return -1;
        Node n = map.get(key);
        n.prev.next = n.next;
        n.next.prev = n.prev;
        moveToTail(n);
        return n.val;
    }

    private void moveToTail(Node n) {
        n.prev = tail.prev;
        n.next = tail;
        tail.prev.next = n;
        tail.prev = n;
    }

    public void put(int key, int value) {
        if (get(key) != -1) {
            map.get(key).val = value;
        } else {
            Node n = new Node(key, value);
            map.put(key, n);
            moveToTail(n);
            if (map.size() > cap) {
                map.remove(head.next.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
        }
    }

    public class Node{
        int key, val;
        Node prev, next;
        public Node(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }
}


