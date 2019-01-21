package leetcode;

public class Solution280 {
    public void wiggleSort(int[] nums) {
        // Write your code here
        for(int i=1; i<nums.length; i++) {
            if((i%2==1 && (nums[i] < nums[i-1]) || 
              (i%2==0) && (nums[i] > nums[i-1]))) {
                swap(nums, i-1, i);
            }
        }
    }
    
    public void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
