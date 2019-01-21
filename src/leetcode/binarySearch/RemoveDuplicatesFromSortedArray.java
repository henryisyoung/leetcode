package leetcode.binarySearch;

public class RemoveDuplicatesFromSortedArray {
    public int removeDuplicates(int[] nums) {
        if(nums == null || nums.length == 0){
            return 0;
        }
        int j = 0, len = nums.length;
        for(int i = 0; i < len - 1; i++){
            if(nums[i] != nums[i + 1]){
                j++;
                nums[j] = nums[i+1];
            }
        }
        return j+1;
    }
}
