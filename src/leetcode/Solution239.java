package leetcode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Solution239 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] nums = {1,3,-1,-3,5,3,6,7};
		Solution239 t = new Solution239();
		System.out.println(Arrays.toString(t.maxSlidingWindow(nums, 3)));
	}
    public int[] maxSlidingWindow(int[] nums, int k) {
    	if(nums == null || nums.length < k){
    		return new int[0];  
    	}
    	int n = nums.length, j = 0;
    	int[] rlt = new int[n - k + 1];
    	Deque<Integer> deque = new ArrayDeque<Integer>();
    	
    	for(int i = 0; i < n; i++){
    		while(! deque.isEmpty() && nums[i] > deque.peekLast()){
    			deque.pollLast();
    		}
    		deque.offer(nums[i]);
    		if(i > k - 1 && nums[i - k] == deque.peekFirst()){
    			deque.pollFirst();
    		}
    		if(i >= k - 1){
    			rlt[j++] = deque.peekFirst();
    		}
    	}
    	return rlt;
    }
}
