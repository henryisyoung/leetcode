package google;

import java.util.*;

/**
 * One obvious problem is a race between testing for cache.get(key).isExpired() and cache.remove(key)
 * on the next line. It is very well possible for another thread to preempt right in between just to
 * get() that object.
 * A thread performing get() would believe that it updated useTime, yet the entry would have been removed.
 * Depending on the use case, the consequences could be catastrophic, or harmless.
 */
public class MapWithExpiringEntries {
    class Node {
        int key, val, expire, timestamp;
        Node next, prev;
        public Node(int key, int val, int expire, int timestamp) {
            this.key = key;
            this.val = val;
            this.expire = expire;
            this.timestamp = timestamp;
            this.next = null;
            this.prev = null;
        }
    }

    Map<Integer, Node> map;
    Node head, tail;
    int capacity;

    public MapWithExpiringEntries(int n) {
        this.head = new Node(-1,-1, -1, -1);
        this.tail = new Node(-1, -1, -1, -1);
        this.head.next = tail;
        this.tail.prev = head;
        this.map= new HashMap<>();
        this.capacity = n;
    }

    public void put(int key, int val, int expire, int timestamp) {
        if (!map.containsKey(key)) {
            Node n = new Node(key, val, expire, timestamp);
            moveTail(n);
            map.put(key, n);
        } else {
            Node n = map.get(key);
            n.expire = expire;
            n.val = val;
            n.timestamp = timestamp;
            n.prev.next = n.next;
            n.next.prev = n.prev;
            moveTail(n);
        }

        while (map.size() > capacity ||
                (head.next.timestamp + head.next.expire < timestamp && head.next.expire != -1)) {
            map.remove(head.next.key);
            head.next = head.next.next;
            head.next.prev = head;
        }
    }

    private void moveTail(Node n) {
        tail.prev.next = n;
        n.prev = tail.prev;
        tail.prev = n;
        n.next = tail;
    }

    public Integer get(int key, int timestamp) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node n = map.get(key);
        if (n.expire + n.timestamp < timestamp) {
            deleteNode(n);
            return null;
        }
        return n.val;
    }

    private void deleteNode(Node n) {
        map.remove(n.key);
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    public static void main(String[] args) {
        MapWithExpiringEntries sovler = new MapWithExpiringEntries(5);
        sovler.put(10, 25, 50, 100000000);
        sovler.put(11, 125, 50, 100000100);
//        sovler.put(12, 225, 50, 100000200);
//        sovler.put(13, 325, 50, 100000300);
        for (int key : sovler.map.keySet()) {
            System.out.println("key " + key);
        }
        Integer result = sovler.get(10, 10000010);
        System.out.println(result);
    }
}
