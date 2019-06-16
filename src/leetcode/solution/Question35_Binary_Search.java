package leetcode.solution;

public class Question35_Binary_Search {

    public static void main(String[] args){
        int[] nums = {1,3,5,6};
        int target = 5;
        Question35_Binary_Search q35 = new Question35_Binary_Search();
        int pos = q35.searchInsert(nums, target);
        System.out.println(pos);
    }

    public int searchInsert(int[] nums, int target) {
        if(nums == null || nums.length == 0){
            return 0;
        }
        int start = 0;
        int end = nums.length - 1;
        if(nums[start] >= target) return start;
        if(nums[end] < target) return end + 1;
        while(start + 1 < end){
            int mid = start + (end - start)/2;
            if(nums[mid] == target){
                return mid;
            }else if(nums[mid] < target){
                start = mid;
            }else{
                end = mid;
            }
        }
        return end;
    }
}
