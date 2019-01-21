package leetcode;
import java.util.*;
public class Solution133 {
    public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
    	if(node == null) return node;
        HashMap<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
        ArrayList<UndirectedGraphNode> allNodes = getAllNodes(node);
        for(UndirectedGraphNode n : allNodes){
        	map.put(n, new UndirectedGraphNode(n.label));
        }
        for(UndirectedGraphNode n : allNodes){
        	UndirectedGraphNode newNode = map.get(n);
        	for(UndirectedGraphNode neibor : n.neighbors){
        		UndirectedGraphNode newNeibor = map.get(neibor);
        		newNode.neighbors.add(newNeibor);
        	}
        }
        return map.get(node);
    }
    
    
    // BFS using one queue
	private ArrayList<UndirectedGraphNode> getAllNodes(UndirectedGraphNode node) {
		Queue<UndirectedGraphNode> queue = new LinkedList<UndirectedGraphNode>();
		HashSet<UndirectedGraphNode> set = new HashSet<>();
		queue.offer(node);
		set.add(node);
		while(!queue.isEmpty()){
			UndirectedGraphNode head = queue.poll();
			for(UndirectedGraphNode n : head.neighbors){
				if(!set.contains(n)){
					queue.offer(n);
					set.add(n);
				}
			}
		}
		return new ArrayList<UndirectedGraphNode>(set);
	}
	
	public UndirectedGraphNode cloneGraph2(UndirectedGraphNode node) {
		if(node == null) return node;
		HashMap<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
		ArrayList<UndirectedGraphNode> nodes = new ArrayList<>();
		nodes.add(node);
		map.put(node, new UndirectedGraphNode(node.label));
		int start = 0;
		while(start < nodes.size()){
			UndirectedGraphNode head = nodes.get(start++);
			for(UndirectedGraphNode neighbor : head.neighbors){
				if(!map.containsKey(neighbor)){
					map.put(neighbor, new UndirectedGraphNode(neighbor.label));
					nodes.add(neighbor);
				}
			}
		}
		for(int i = 0; i < nodes.size(); i++){
			UndirectedGraphNode newNdoe = map.get(nodes.get(i));
			UndirectedGraphNode head = nodes.get(i);
			for(UndirectedGraphNode n : head.neighbors){
				newNdoe.neighbors.add(map.get(n));
			}
		}
		return map.get(node);
	}
	
	public UndirectedGraphNode cloneGraph3(UndirectedGraphNode node) {
		if(node == null) return node;
		
		HashMap<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
		Queue<UndirectedGraphNode> queue = new LinkedList<UndirectedGraphNode>();
		UndirectedGraphNode newNode = new UndirectedGraphNode(node.label);
		map.put(node, newNode);
		queue.offer(node);
		
		while(!queue.isEmpty()){
			UndirectedGraphNode cur = queue.poll();
			List<UndirectedGraphNode> neighbors = cur.neighbors;
			
			for(UndirectedGraphNode n : neighbors){
				if(!map.containsKey(n)){
					UndirectedGraphNode copy = new UndirectedGraphNode(n.label);
					map.put(n, copy);
					map.get(cur).neighbors.add(copy);
					queue.offer(n);
				}else{
					map.get(cur).neighbors.add(map.get(n));
				}
			}
		}
		
		return map.get(node);
	}
	
}
