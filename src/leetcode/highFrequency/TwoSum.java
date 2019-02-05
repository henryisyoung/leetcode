package leetcode.highFrequency;

import java.util.HashMap;
import java.util.Map;

public class TwoSum {
    //Given an array of integers, return indices of the two numbers such that they add up to a specific target.
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        int[] result = new int[2];
        for (int i = 0; i < nums.length; i++) {
            int offset = target - nums[i];
            if(map.containsKey(offset)) {
                int pos = map.get(offset);
                result[0] = pos;
                result[1] = i;
                break;
            } else {
                map.put(nums[i], i);
            }
        }
        return result;
    }

    public int[] twoSum2(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return nums;
        }
        int n = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int diff = target - nums[i];
            if (map.containsKey(diff)) {
                int[] result = {map.get(diff), i};
                return result;
            } else {
                map.put(nums[i], i);
            }
        }
        return null;
    }
}
