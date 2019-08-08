package leetcode.dataStructrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LongestConsecutiveSequence {
    public static int longestConsecutive(int[] nums) {
        if(nums == null || nums.length == 0) return 0;
        Set<Integer> set = new HashSet<>();
        for (int i : nums) set.add(i);
        int max = 1;
        for (int i : nums) {
            set.remove(i);
            int greater = i + 1;
            int smaller = i - 1;
            int count = 1;
            while (set.contains(greater)) {
                set.remove(greater++);
                count++;
            }
            while (set.contains(smaller)) {
                set.remove(smaller--);
                count++;
            }
            max = Math.max(count, max);
        }
        return max;
    }

    public static void main(String[] args) {
        int[] nums = {1,2,3,4,5,876,1};
        System.out.println(longestConsecutive(nums));
    }
}
