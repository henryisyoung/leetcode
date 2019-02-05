package leetcode.highFrequency;

public class SingleNumberII {
    public int singleNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        int result = 0;
        int[] bit = new int[32];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < nums.length; j++) {
                bit[i] += (nums[j] >> i) & 1;
                bit[i] %= 3;
            }
            result |= bit[i] << i;
        }
        return result;
    }

    public int singleNumber2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int result = 0;
        int[] bit = new int[32];
        for (int j = 0; j < 32; j++) {
            for (int i : nums) {
                int digit = (i >> j) & 1;
                bit[j] += digit;
                bit[j] %= 3;
            }
            result |= bit[j] << j;
        }
        return result;
    }
}
