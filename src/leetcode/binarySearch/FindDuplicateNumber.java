package leetcode.binarySearch;

import java.util.Arrays;

public class FindDuplicateNumber {
    public int findDuplicate(int[] nums) {
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i-1]) {
                return nums[i];
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {1,3,4,2,2};
        Arrays.sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
