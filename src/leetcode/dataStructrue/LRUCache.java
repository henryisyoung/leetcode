package leetcode.dataStructrue;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private class Node {
        int val, key;
        Node prev, next;
        public Node(int key, int val) {
            this.val = val;
            this.key = key;
        }
    }
    Map<Integer, Node> map;
    int cap;
    Node head, tail;
    public LRUCache(int capacity) {
        map = new HashMap<>();
        cap = capacity;
        head = new Node(Integer.MIN_VALUE, Integer.MIN_VALUE);
        tail = new Node(Integer.MIN_VALUE, Integer.MIN_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if(! map.containsKey(key)) {
            return -1;
        }
        Node cur = map.get(key);
        cur.prev.next = cur.next;
        cur.next.prev = cur.prev;
        moveTail(cur);
        return cur.val;
    }

    private void moveTail(Node cur) {
        cur.prev = tail.prev;
        tail.prev.next = cur;
        cur.next = tail;
        tail.prev = cur;
    }

    public void put(int key, int value) {
        if(get(key) == -1) {
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            moveTail(newNode);
            if(map.size() > cap) {
                map.remove(head.next.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
        } else {
            map.get(key).val = value;
        }

    }
}
