package leetcode;
import java.util.*;
public class Solution215 {
	public static void main(String[] args) {
		Solution215 t = new Solution215();
		int[] nums = {3,1,5,2,6,4};
		System.out.println(t.kthLargestElement(2, nums));
	}
	
    public int findKthLargest(int[] nums, int k) {
    	PriorityQueue<Integer> pq = new  PriorityQueue<Integer>(nums.length, new mycomp());
    	for(int i : nums){
    		pq.offer(i);
    	}
    	int rlt = 0;
    	
    	while(k > 0){
    		rlt = pq.poll();
    		
    		k--;
    	}
    	return rlt;
    }
    
    private class mycomp implements Comparator<Integer>{
    	public int compare(Integer a, Integer b){
    		return b - a;
    	}
    }
    
    public int kthLargestElement(int k, int[] nums) {
        // write your code here
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (k <= 0) {
            return 0;
        }
        return helper(nums, 0, nums.length - 1, nums.length - k);
        
    }
    public int helper(int[] nums, int l, int r, int k) {
        if (l == r) {
            return nums[l];
        }
        int position = partition(nums, l, r);
        
        if (position == k) {
            return nums[position];
        } else if (position < k) {
            return helper(nums, position + 1, r, k);
        }  else {
            return helper(nums, l, position - 1, k);
        }
    }
    public int partition(int[] nums, int l, int r) {
        // 初始化左右指针和pivot
        int left = l, right = r;
        int pivot = nums[left];
        
        // 进行partition
        while (left < right) {
            while (left < right && nums[right] >= pivot) {
                right--;
            }
            nums[left] = nums[right];
            while (left < right && nums[left] <= pivot) {
                left++;
            }
            nums[right] = nums[left];
        }
        
        // 返还pivot点到数组里面
        nums[left] = pivot;
        return left;         
    }
}
