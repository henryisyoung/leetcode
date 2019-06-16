package leetcode.solution;

import java.util.Stack;

public class Solution85 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution85 t = new Solution85();
		char[][] matrix = {{'0','1'}};
		System.out.println(Integer.parseInt("66")-1);
	}
    public int maximalRectangle(char[][] matrix) {
        if(matrix == null || matrix.length == 0) return 0;
        if(matrix[0] == null || matrix[0].length == 0) return 0;
        int[][] table = new int[matrix.length][matrix[0].length];
        int max = 0;
        
        for(int i = 0;i < matrix.length;i++){
        	for(int j = 0;j < matrix[0].length;j++){
        		if(i == 0){
        			table[i][j] = matrix[i][j] - '0';
        		}else{
        			if( matrix[i][j] == '0'){
        				table[i][j] = 0;
        			}else{
        				table[i][j] = 1 + table[i - 1][j];
        			}
        		}
        	}
        	max = Math.max(max, findMax(table[i]));
        }
        

        return max;
    }

	private int findMax(int[] arr) {
		int max = 0;
		Stack<Integer> stack = new Stack<Integer>();
		for(int i = 0;i <= arr.length;i++){
			int cur = (i == arr.length)?-1:arr[i];
			while(!stack.isEmpty() && cur <= arr[stack.peek()]){
				int h = arr[stack.pop()];
				int w = (stack.isEmpty())?i : i - stack.peek() - 1;
				max = Math.max(max, h*w);
			}
			stack.push(i);
		}
		System.out.println(max);
		return max;
	}
}
