package leetcode.linkedList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FirstUnique {
    class Node{
        int key, count;
        Node prev, next;
        public Node(int key, int count) {
            this.key = key;
            this.count = count;
        }
    }

    Node head, tail;
    Map<Integer, Node> map = new HashMap<>();
    Set<Integer> unique = new HashSet<>();
    public FirstUnique(int[] nums) {
        this.head = new Node(-1, -1);
        this.map = new HashMap<>();
        for (int i : nums) {
            if (map.containsKey(i)) {
                map.get(i).count++;
            } else {
                Node node =  new Node(i, 1);
                map.put(i, node);
            }
        }
        this.tail = head;
        for (int key : map.keySet()) {
            if (map.get(key).count == 1) {
                unique.add(key);
                Node n = new Node(key, map.get(key).count);
                tail.next = n;
                n.prev = tail;
                tail = tail.next;
            }
        }
    }

    public int showFirstUnique() {
        Node cur = head;
        if (cur.next != null) return cur.next.key;
        return -1;
    }

    public void add(int value) {
        if (unique.contains(value)) {
            unique.remove(value);
            Node node = map.get(value);
            node.prev.next = node.next;
            node.next.prev = node.prev;
            map.get(value).count++;
            return;
        }
        unique.add(value);
        Node n =  new Node(value, 1);
        map.put(value, n);
        tail.next = n;
        n.prev = tail;
        tail = tail.next;

    }
}
