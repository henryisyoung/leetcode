package leetcode;
import java.util.*;
public class LRUCache {
	
	private class Node{
		int key,val;
		Node prev, next;
		public Node(int key, int val){
			this.key = key;
			this.val = val;
			this.prev = null;
			this.next = null;
		}
	}
	
	int capacity;
	Node head = new Node(-1,-1), tail = new Node(-1,-1);
	HashMap<Integer,Node> map = new HashMap<Integer,Node>();
	
    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if(!map.containsKey(key)) return -1;
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

	public void set(int key, int value) {
        if(get(key) != -1){
        	map.get(key).val = value;
        }else{
            if(map.size() == capacity){
            	map.remove(head.next.key);
            	head.next = head.next.next;
            	head.next.prev = head;
            }
            
            Node cur = new Node(key, value);
            map.put(key, cur);
            moveTail(cur);
        }
    }
}
