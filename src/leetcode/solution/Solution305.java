package leetcode.solution;
import java.util.*;
public class Solution305 {

	public static void main(String[] args) {
		int m = 3, n = 3;
		int[][] positions = {{0,0}, {0,1}, {1,2}, {2,1} };
		Solution305 t = new Solution305();
		System.out.println(t.numIslands2(m, n, positions));

	}
	class Union{
		int[] id, size;
		
		public Union(int cap){
			id = new int[cap];
			size = new int[cap];
			for(int i = 0; i < cap; i++){
				id[i] = i;
				size[i] = 1;
			}
		}
		
		public int find(int index){
			int parent = id[index];
			while(parent != id[parent]){
				parent = id[parent];
			}
			return parent;
		}
		
		public void union(int i, int j){
			int fi = find(i);
			int fj = find(j);
			if(fi == fj){
				return;
			}
			if(size[fi] < size[fj]){
				id[fi] = fj;
				size[fj] += size[fi];
			}else{
				id[fj] = fi;
				size[fi] += size[fj];
			}
		}
	}
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	if(m*n == 0 || positions == null || positions.length == 0){
    		return rlt;
    	}
    	Union uf = new Union(m*n);
    	int[] dr = {-1, 1, 0, 0}, dc = {0,0,-1,1};
    	int[][] table = new int[m][n];
    	int count = 0;
    	for(int[] pos : positions){
    		int r = pos[0], c = pos[1];
    		table[r][c] = 1;
    		count++;
    		for(int i = 0; i < 4; i++){
    			int nr = r + dr[i], nc = c + dc[i];
    			if(nr >= 0 && nc >= 0 && nr < m && nc < n && table[nr][nc] == 1){
    				int oldF = uf.find(r*n + c);
    				int newF = uf.find(nr*n + nc);
    				if(oldF != newF){
    					count--;
    					uf.union(oldF, newF);
    				}
    			}
    		}
    		rlt.add(count);
    	}
    	return rlt;
    }
}
