package leetcode.highFrequency;

import java.util.ArrayList;
import java.util.List;

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
            if ((pos & i) == 0) {
                a ^= i;
            } else {
                b ^= i;
            }
        }

        result[0] = a;
        result[1] = b;
        return result;
    }

    public int[] singleNumber2(int[] nums) {
        int[] result = new int[2];
        if (nums == null || nums.length == 0) {
            return result;
        }
        int val = nums[0];
        for (int i  =1; i < nums.length; i++) {
            val ^= nums[i];
        }
        int n = val & (-val);
        int a = 0, b = 0;
        for (int i : nums) {
            if ((i & n) == n) {
                a ^= i;
            } else {
                b ^= i;
            }
        }
        result[0] = a;
        result[1] = b;
        return result;
    }

    private int findSingle(List<Integer> nums) {
        int val = 0;
        for (int i : nums) {
            val ^= i;
        }
        return val;
    }
}
