package leetcode.solution;

public class Question81_Binary_Search {
    public boolean search(int[] nums, int target) {
        if(nums == null || nums.length == 0){
            return false;
        }
        int start = 0;
        int end = nums.length - 1;
        while(start + 1 < end){
            int mid = start + (end -start)/2;
            if(nums[mid] == target){
                return true;
            }else if(nums[mid] < nums[end]){
                if(nums[end] >= target && nums[mid] < target){
                    start = mid;
                }else{
                    end = mid;
                }
            }else if(nums[mid] > nums[end]){
                if(nums[start] <= target && nums[mid] > target){
                    end = mid;
                }else {
                    start = mid;
                }
            }else{
                end--;
            }
        }
        return (nums[start] == target) || (nums[end] == target);
    }
}
