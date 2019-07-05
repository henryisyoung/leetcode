package leetcode.dataStructrue.treeMapSet;

import java.util.*;

public class ContainsDuplicateIII {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < nums.length; i++) {
            Integer floor = set.floor(nums[i]);
            Integer ceil = set.ceiling(nums[i]);
            if (floor != null && floor + t >= nums[i] || ceil != null && nums[i] + t >= ceil) {
                return true;
            }
            set.add(nums[i]);
            if (set.size() > k) {
                set.remove(nums[i - k]);
            }
        }
        return false;
    }
}
