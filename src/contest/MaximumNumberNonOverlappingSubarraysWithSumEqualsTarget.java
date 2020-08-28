package contest;

import java.util.HashMap;
import java.util.Map;

public class MaximumNumberNonOverlappingSubarraysWithSumEqualsTarget {
    public int maxNonOverlapping(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 0);
        int sum = 0, max = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (map.containsKey(sum - target)) {
                max = Math.max(max, map.get(sum - target) + 1);
            }
            map.put(sum, max);
        }

        return max;
    }


}
