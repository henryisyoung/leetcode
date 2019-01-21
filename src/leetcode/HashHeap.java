package leetcode;
import java.util.*;
class HashHeap {
	private class Node {
		public int cnt;
		public int id;
		
		public Node(int c, int i) {
			cnt = c;
			id = i;
		}
	}
	
	public ArrayList<Integer> list;
	public HashMap<Integer, Node> map;
	public Comparator<Integer> comp;
	private int size;
	
	public void printList() {
		String s = "";
		for(Integer i : list) {
			s += i + ": "+ map.get(i).cnt+", ";
		}
		System.out.println(s);
	}
	
	public HashHeap(Comparator<Integer> cmp) {
		list = new ArrayList<>();
		map = new HashMap<>();
		comp = cmp;
	}
	
	public int peek() {
		return list.get(0);
	}

	public int poll() {
		int head = list.get(0);
		remove(head);
		return head;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public int size() {
	    return size;
	}

	public boolean contains(int val) {
	    return map.containsKey(val);
	}
	
	public void add(int val) {
		size++;
		if(!map.containsKey(val)) {
			list.add(val);
			map.put(val, new Node(1, list.size()-1));
			siftUp(list.size()-1);
		}
		else map.get(val).cnt += 1;
	}

	public void remove(int val) {
		size--;
		Node node = map.get(val);
		node.cnt -= 1;
		if(node.cnt > 0) return ;
		else {
			int id = node.id;
			swap(id, list.size()-1);
			map.remove(list.get(list.size()-1));
			list.remove(list.size()-1);
			if(id >= list.size()) return ;
//			todo: need siftup in remove
			siftUp(id);
			siftDown(id);
		}
	}
	
	private void siftUp(int id) {
		int curr = id;
//		todo: caution with parent id
		int parent_id = curr == 0 ? -1 : (curr-1)/2;
		while(parent_id > -1) {
			Integer parent_val = list.get(parent_id);
			Integer curr_val = list.get(curr);
			if(comp.compare(parent_val, curr_val) > 0) {
				swap(parent_id, curr);
				curr = parent_id;
				parent_id = curr == 0 ? -1 : (curr-1)/2;
			} else break;
		}
	}
	
	private void siftDown(int id) {
		int curr = id;
		int left_kid = 2*curr + 1;
		while(left_kid < list.size()) {
			Integer curr_val = list.get(curr);
			int right_kid = left_kid + 1;
			int child_id = left_kid;
			if(right_kid < list.size() && comp.compare(list.get(left_kid),list.get(right_kid)) > 0) child_id = right_kid;
			Integer child_val = list.get(child_id);
			if(comp.compare(curr_val, child_val) > 0) {
				swap(curr, child_id);
				curr = child_id;
				left_kid = 2 * curr + 1;
			} else break;
		}
	}
	
	private void swap(int i, int j) {
		int i_val = list.get(i);
		int j_val = list.get(j);
		list.set(i, j_val);
		map.get(j_val).id = i;
		list.set(j, i_val);
		map.get(i_val).id = j;
	}
}