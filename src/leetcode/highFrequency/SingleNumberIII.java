package leetcode.highFrequency;

public class SingleNumberIII {
    public int[] singleNumber(int[] nums) {
        int[] result = new int[2];
        int val = nums[0];
        for (int i = 1; i < nums.length; i++) {
            val ^= nums[i];
        }
        int a = 0, b = 0;

        int pos = val - (val & (val - 1));

        for (int i : nums) {
            if((pos & i) == 0) {
                a ^= i;
            } else {
                b ^= i;
            }
        }

        result[0] = a;
        result[1] = b;
        return result;
    }
}
