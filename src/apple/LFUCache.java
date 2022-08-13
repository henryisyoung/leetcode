package apple;

import java.util.HashMap;
import java.util.Map;

public class LFUCache {
    class Node{
        Node prev, next;
        int key, val, count;
        public Node(int key, int val, int count) {
            this.count = count;
            this.key = key;
            this.val = val;
        }
    }

    class ListNode{
        Node head, tail;
        int size;
        public ListNode() {
            this.head = new Node(-1,-1,-1);
            this.tail = new Node(-1,-1,-1);
            head.next = tail;
            tail.prev = head;
            this.size = 0;
        }

        public void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        public void add(Node node) {
            head.next.prev = node;
            node.next = head.next;
            head.next = node;
            node.prev = head;
            size++;
        }

        public Node removeLeast() {
            Node remove = tail.prev;
            remove(remove);
            return remove;
        }
    }

    Map<Integer, Node> map;
    Map<Integer, ListNode> countMap;
    int leastCount, capacity;

    public LFUCache(int capacity) {
        this.map = new HashMap<>();
        this.countMap = new HashMap<>();
        this.leastCount = 0;
        this.capacity = capacity;
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            return -1;
        }
        Node node = map.get(key);
        promote(node);
        return node.val;
    }

    private void promote(Node node) {
        int count = node.count;
        ListNode list = countMap.get(count);
        list.remove(node);
        if (count == leastCount && list.size == 0) {
            leastCount++;
        }
        node.count++;
        countMap.putIfAbsent(node.count, new ListNode());
        countMap.get(node.count).add(node);
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.val = value;
            promote(node);
            return;
        }
        if (map.size() == capacity) {
            Node remove = countMap.get(leastCount).removeLeast();
            map.remove(remove.key);
        }
        Node node = new Node(key, value, 1);
        map.put(key, node);
        countMap.putIfAbsent(node.count, new ListNode());
        countMap.get(1).add(node);
        leastCount = 1;
    }
}
