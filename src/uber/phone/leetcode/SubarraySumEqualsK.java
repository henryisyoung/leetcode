package uber.phone.leetcode;

import java.util.HashMap;
import java.util.Map;

public class SubarraySumEqualsK {
    //https://leetcode.com/problems/subarray-sum-equals-k/submissions/
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int count = 0, sum = 0;
        for (int i : nums) {
            sum += i;
            count += map.getOrDefault(sum - k, 0);
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return count;
    }
}
