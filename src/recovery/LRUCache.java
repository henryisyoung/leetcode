package recovery;

import java.util.HashMap;
import java.util.Map;

/***
 * 题号	题目	重点
 * 146	LRU Cache	设计 + 数据结构
 * 981	Time Based Key-Value Store	KV store（非常高频）
 * 295	Find Median from Data Stream	heap 设计
 * 347	Top K Frequent Elements	hashmap + heap
 * 3	Longest Substring Without Repeating Characters	sliding window
 * 20	Valid Parentheses	stack
 * 1	Two Sum	hashmap
 * 215	Kth Largest Element in Array	quickselect / heap
 * 48	Rotate Image	matrix manipulation
 * 23	Merge k Sorted Lists	heap
 * 460	LFU Cache	设计题
 * 208	Implement Trie	系统组件抽象
 */
public class LRUCache {
    private class Node {
        Node prev, next;
        int key, val;

        Node(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    private Node headNode, tailNode;
    private int capacity;
    private Map<Integer, Node> nodeMap;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        headNode = new Node(-1, -1);
        tailNode = new Node(-1, -1);
        headNode.next = tailNode;
        tailNode.prev = headNode;
        nodeMap = new HashMap<>();
    }

    public int get(int key) {
        if (!nodeMap.containsKey(key)) {
            return -1;
        }
        Node node = nodeMap.get(key);

        node.next.prev = node.prev;
        node.prev.next = node.next;
        moveTail(node);
        return node.val;
    }

    private void moveTail(Node node) {
        node.next = tailNode;
        node.prev = tailNode.prev;
        tailNode.prev.next = node;
        tailNode.prev = node;
    }

    public void put(int key, int value) {
        if (get(key) == -1) {
            Node node = new Node(key, value);
            nodeMap.put(key, node);
            moveTail(node);
            if (nodeMap.size() > capacity) {
                Node removeNode = headNode.next;
                headNode.next = removeNode.next;
                removeNode.next.prev = headNode;
                nodeMap.remove(removeNode.key);
            }
        } else {
            Node node = nodeMap.get(key);
            node.val = value;
        }
    }
}
