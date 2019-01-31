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
    public int removeDuplicates2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            if(nums[i] != nums[j]) {
                nums[++j] = nums[i];
            }
        }
        return j + 1;
    }
}
