package leetcode.graphAndSearch;

import java.util.Arrays;

public class MatchsticksToSquare {
    public boolean makesquare(int[] nums) {
        if (nums == null || nums.length < 4) {
            return false;
        }
        int sum = 0;
        for (int i : nums) sum += i;
        if (sum % 4 != 0) return false;
        int len = sum / 4;
        Arrays.sort(nums);
        int[] sums = new int[4];
        return dfsSearchAll(nums, sums, 0, len);
    }

    private boolean dfsSearchAll(int[] nums, int[] sums, int pos, int len) {
        if (pos == nums.length) {
            return sums[0] == len && sums[1] == len && sums[2] == len;
        }
        for (int i = 0; i < 4; ++i) {
            if (sums[i] + nums[pos] > len) continue;
            sums[i] += nums[pos];
            if (dfsSearchAll(nums, sums, pos + 1, len)) return true;
            sums[i] -= nums[pos];
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {1,1,2,2,2};
        MatchsticksToSquare sovler = new MatchsticksToSquare();
        System.out.println(sovler.makesquare(nums));
    }
}
