package leetcode.solution;
import java.util.*;
public class Solution261 {
	
	public static void main(String[] args) {
		Solution261 t = new Solution261();
		int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {1, 4}};
		System.out.println(t.validTree(5, edges));
	}
	
	class Union{
		HashMap<Integer, Integer> father = new HashMap<Integer, Integer>();
		public Union(int n){
			for(int i = 0; i < n; i++){
				father.put(i, i);
			}
		}
		
		public int find(int x){
			int parent = father.get(x);
			while(parent != father.get(parent)){
				parent = father.get(parent);
			}
			int fa = father.get(x);
			while(fa != father.get(fa)){
				int tmp = father.get(fa);
				father.put(fa, parent);
				fa = tmp;
			}
			return parent;
		}
		
		public void union(int x, int y){
			int faX = find(x);
			int faY = find(y);
			if(faX != faY){
				father.put(faX, faY);
			}
		}
	}
    public boolean validTree(int n, int[][] edges) {
        Union u = new Union(n);
        for(int i = 0; i < edges.length; i++){
        	int x = edges[i][0], y = edges[i][1];
        	if(u.find(x) == u.find(y)){
        		return false;
        	}else{
        		u.union(x, y);
        	}
        }
        int x = edges[0][0];
        for(int i = 1; i < edges.length; i++){
        	int y = edges[i][0];
        	if(u.find(x) != u.find(y)){
        		return false;
        	}
        }
        //System.out.println(u.father.size());
        return true;
    }
}
