package leetcode;

public class Question34_Binary_Search {
    public static void main(String[] args){

    }

    public int[] searchRange(int[] nums, int target) {
        int[] result = new int[2];
        if (nums == null || nums.length == 0){
            result[0] = -1;
            result[1] = -1;
            return result;
        }
        int first = find_first(nums, target);
        if(first == -1) {
            result[0] = -1;
            result[1] = -1;
            return result;
        }
        int last = find_last(nums, target);
        result[0] = first;
        result[1] = last;
        return result;
    }

    private int find_last(int[] nums, int target) {
        int start = 0, end = nums.length - 1;
        while (start + 1 < end){
            int mid = start + (end - start)/2;
            if (nums[mid] <= target){
                start = mid;
            }else{
                end = mid;
            }
        }
        if(nums[end] == target) return end;
        if(nums[start] == target) return start;
        return -1;
    }

    private int find_first(int[] nums, int target) {
        int start = 0, end = nums.length - 1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            if(nums[mid] >= target){
                end = mid;
            }else {
                start = mid;
            }
        }
        if(nums[start] == target) return start;
        if(nums[end] == target) return end;
        return -1;
    }
}
