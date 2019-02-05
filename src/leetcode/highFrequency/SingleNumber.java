package leetcode.highFrequency;

public class SingleNumber {
    public int singleNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            result = result ^ nums[i];
        }
        return result;
    }

    public int singleNumber2(int[] nums) {
        int result = 0;
        if (nums == null || nums.length == 0) {
            return result;
        }
        for (int i : nums) {
            result ^= i;
        }
        return result;
    }
}
