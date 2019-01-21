package leetcode;
import java.util.*;
public class Solution310 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution310 t = new Solution310();
		int[][] prerequisites = {{1,0},{2,0},{3,1}};
		
		System.out.println(t.findMinHeightTrees(4, prerequisites));

	}
	
        public List<Integer> findMinHeightTrees(int n, int[][] edges) {  
            Queue<Integer> leaf = new LinkedList<Integer>();  
            if(n<=1) {  
                leaf.add(0);  
                return (List<Integer>) leaf;  
            }  
            Map<Integer, ArrayList<Integer>> graph = new HashMap<Integer, ArrayList<Integer>>();  
            for(int i=0;i<n;i++) {
            	graph.put(i, new ArrayList<Integer>());  
            }
            int[] neighbors = new int[n];  
            for(int[] edge : edges) {  
                neighbors[edge[0]]++;  
                neighbors[edge[1]]++;  
                graph.get(edge[0]).add(edge[1]);  
                graph.get(edge[1]).add(edge[0]);  
            }  
              
            for(int i=0;i<n;i++) {  
                if(graph.get(i).size() ==1 ) leaf.offer(i);  
            }  
            while(n>2) {  
                int size = leaf.size(); 
                for(int i = 0; i < size; i++) {  
                    n--; 
                    int cur = leaf.poll();
                    for(int nb : graph.get(cur)) {  
                        if(--neighbors[nb] == 1) leaf.add(nb);  
                    }  
                }  
                
            }  
            return (List<Integer>) leaf;
        }  
}
