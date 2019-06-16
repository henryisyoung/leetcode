package leetcode.solution;

public class Question26_Sorted_Array {
    public int removeDuplicates(int[] nums) {
        if(nums == null || nums.length == 0) return 0;
        int lens = nums.length;
        int j = 0;
        for(int i = 0; i < lens - 1; i++){
            if(nums[i + 1] != nums[i]){
                j++;
                nums[j] = nums[i + 1];
            }
        }
        return j + 1;
    }
}
