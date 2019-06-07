package leetcode.twoPointer;

public class RemoveDuplicatesFromSortedArrayII {
    public int removeDuplicates(int[] nums) {
        if (nums == null) return 0;
        if(nums.length <= 2) {
            return nums.length;
        }
        int c = 2, p = 1;
        while (c < nums.length) {
            if (nums[p - 1] == nums[c] && nums[p] == nums[c]) {
                c++;
            } else {
                p++;
                nums[p] = nums[c++];
            }
        }
        return p + 1;
    }
}
