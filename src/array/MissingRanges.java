package array;

import java.util.*;

public class MissingRanges {
    public List<String> findMissingRanges(int[] nums, int lower, int upper) {
        List<String> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            result.add(createRange(lower, upper));
            return result;
        }
        int prev = lower - 1, cur = 0, n = nums.length;
        for (int i = 0; i <= n; i++){
            cur = i == n ? upper + 1 : nums[i];
            if (cur - prev >= 2) {
                result.add(createRange(prev + 1, cur - 1));
            }
            prev = cur;
        }
        return result;
    }

    private String createRange(int lower, int upper) {
        if (lower == upper) {
            return Integer.toString(lower);
        }
        return lower + "->" + upper;
    }
}
