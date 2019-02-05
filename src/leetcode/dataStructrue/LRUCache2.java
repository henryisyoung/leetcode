package leetcode.dataStructrue;

import java.util.HashMap;
import java.util.Map;

public class LRUCache2 {
    private class Node {
        int val, key;
        Node prev, next;
        public Node(int key, int val) {
            this.val = val;
            this.key = key;
        }
    }

    Node head, tail;
    Map<Integer, Node> map;
    Integer capacity;
    public LRUCache2(int capacity){
        this.tail = new Node(-1, -1);
        this.head = new Node(-1, -1);
        tail.prev = head;
        head.next = tail;
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        } {
            Node cur = map.get(key);
            cur.prev.next = cur.next;
            cur.next.prev = cur.prev;
            moveTail(cur);
            return cur.val;
        }
    }

    private void moveTail(Node cur) {
        tail.prev.next = cur;
        cur.prev = tail.prev;
        tail.prev = cur;
        cur.next = tail;
    }

    public void put(int key, int value) {
        if (get(key) != -1) {
            map.put(key, new Node(key, value));
        } else {
            if (map.size() == capacity) {
                map.remove(head.next.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
            Node cur = new Node(key, value);
            moveTail(cur);
        }
    }
}
