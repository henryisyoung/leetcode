package leetcode.dataStructrue;

import java.util.HashSet;
import java.util.Set;

public class LongestConsecutiveSequence {
    public int longestConsecutive(int[] nums) {
        if(nums == null || nums.length == 0) {
            return 0;
        }
        int result = 1;
        Set<Integer> set = new HashSet<>();
        for (int i : nums) {
            set.add(i);
        }
        for (int i : nums) {
            set.remove(i);
            int count = 1;
            int smaller = i - 1;
            int greater = i + 1;
            while (set.contains(greater)) {
                count++;
                set.remove(greater);
                greater++;
            }
            while (set.contains(smaller)) {
                count++;
                set.remove(smaller);
                smaller--;
            }
            result = Math.max(result, count);
        }
        return result;
    }

    public int longestConsecutive2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int result = 1;
        Set<Integer> set = new HashSet<Integer>();
        for (int i : nums) {
            set.add(i);
        }
        for (int i : nums) {
            int count = 1;
            set.remove(i);
            int greater = i + 1, smaller = i - 1;
            while (set.contains(greater)) {
                set.remove(greater++);
                count++;
            }
            while (set.contains(smaller)) {
                set.remove(smaller--);
                count++;
            }
            result = Math.max(count, result);
        }
        return result;
    }
}
