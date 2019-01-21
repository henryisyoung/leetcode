package leetcode;

import java.util.Arrays;
import java.util.LinkedList;

public class Solution210 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution210 t = new Solution210();
		int[][] prerequisites = {{1,0},{2,0},{3,1},{3,2}};
		int[][] prerequisites2 = {{1,3},{2,1},{0,1}};
		System.out.println(Arrays.toString(t.findOrder(4, prerequisites2)));

	}
	
    public int[] findOrder(int numCourses, int[][] prerequisites) {
	    if(prerequisites == null){
	        throw new IllegalArgumentException("illegal prerequisites array");
	    }
	 
	    int len = prerequisites.length;
	     if(len == 0){
	        int[] res = new int[numCourses];
	        for(int m=0; m<numCourses; m++){
	             res[m]=m;
	        }
	        return res;
	     }
	    if(numCourses == 0){
	        return new int[numCourses];
	    }
	 
	    // counter for number of prerequisites
	    int[] pCounter = new int[numCourses];
	    for(int i=0; i<len; i++){
	        pCounter[prerequisites[i][0]]++;
	    }
	 
	    //store courses that have no prerequisites
	    LinkedList<Integer> queue = new LinkedList<Integer>();
	    int index = 0;
	    int[] rlt = new int[numCourses];
	    for(int i=0; i<numCourses; i++){
	        if(pCounter[i]==0){
	            queue.add(i);
	            rlt[index++] = i;
	        }
	    }
	    
	    while(!queue.isEmpty()){
	    	int c = queue.poll();
	    	for(int[] arr : prerequisites){
	    		if(c == arr[1]){
	    			pCounter[arr[0]]--;
	    			if(pCounter[arr[0]] == 0){
	    	            queue.add(arr[0]);
	    	            rlt[index++] = arr[0];
	    			}
	    		}
	    	}
	    }
        if(index == numCourses){
        	return rlt;
        }
        return new int[0];
	    
    }
}
