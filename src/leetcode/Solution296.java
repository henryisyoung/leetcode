package leetcode;
import java.util.*;
public class Solution296 {
    public int minTotalDistance(int[][] grid) {
        if(grid == null || grid.length == 0){
        	return 0;
        }
        List<Integer> ipos = new ArrayList<Integer>();
        List<Integer> jpos = new ArrayList<Integer>();
        int m = grid.length, n = grid[0].length;
        for(int i = 0; i < m; i++){
        	for(int j = 0; j < n; j++){
        		if(grid[i][j] == 1){
        			ipos.add(i);
        			jpos.add(j);
        		}
        	}
        }
        int sum = 0;
        for(int i : ipos){
        	sum += Math.abs(i - ipos.get(ipos.size()/2)); 
        }
        Collections.sort(jpos);
        for(int j : jpos){
        	sum += Math.abs(j - jpos.get(jpos.size()/2)); 
        }
        return sum;
    }
}
