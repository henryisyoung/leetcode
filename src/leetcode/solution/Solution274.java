package leetcode.solution;

import java.util.Arrays;

public class Solution274 {
    public int hIndex(int[] citations) {
        if(citations == null || citations.length == 0) return 0;
        int max = 0, n = citations.length;
        Arrays.sort(citations);
        for(int i = 0; i < citations.length; i++){
        	max = Math.max(max, (n - i) * i);
        }
        return max;
    }
    
    public int hIndex2(int[] citations) {
    	if(citations == null || citations.length == 0){
    		return 0;
    	}
    	   Arrays.sort(citations);
    	   
    	    int result = 0;    
    	    for(int i=0; i<citations.length; i++){
    	        int smaller = Math.min(citations[i], citations.length-i);
    	        result = Math.max(result, smaller);
    	    }
    	 
    	    return result;
    }
}
