package leetcode;
import java.util.*;
public class Solution84 {
	
	//O(n*n)
    public int largestRectangleArea(int[] heights) {
    	if(heights == null || heights.length == 0) return 0;
        int n = heights.length, max = 0;
        for(int i = 0; i < n; i++){
        	if(i + 1 < n && heights[i + 1] >= heights[i]){
        		continue;
        	}
        	int min = heights[i];
        	for(int j = i; j >= 0;j--){
        		min = Math.min(heights[j],min);
        		int area = min*(i - j + 1);
        		if(area > max) max = area;
        	}
        }
        return max;
    }
    
	//O(n)
    public int largestRectangleAreaON(int[] heights) {
    	if(heights == null || heights.length == 0) return 0;
    	Stack<Integer> stack = new Stack<Integer>();
    	int max = 0, n = heights.length;
    	for(int i = 0; i <= n; i++){
    		int cur = (i == n)? -1 : heights[i];
    		while(!stack.isEmpty() && cur <= heights[stack.peek()]){
    			int h = heights[stack.pop()]; 
    			int w = (stack.isEmpty())? i : i - stack.peek() - 1;
    			max = Math.max(h*w, max);
    		}
    		stack.push(i);
    	}
    	return max;
    }
}
