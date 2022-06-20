package datastructure.hashTable;

import java.util.HashSet;
import java.util.Set;

public class LongestConsecutiveSequence {
    public static int longestConsecutive(int[] nums) {
        int max = 0;
        if(nums == null || nums.length == 0) return max;
        Set<Integer> set = new HashSet<>();
        for (int a : nums) set.add(a);

        for(int i : nums) {
            int count = 1;
            set.remove(i);
            int greater = i + 1, smaller = i - 1;
            while(set.contains(greater)) {
                count++;
                set.remove(greater++);
            }
            while(set.contains(smaller)) {
                count++;
                set.remove(smaller--);
            }
            max = Math.max(max, count);
        }

        return max;
    }

    public static void main(String[] args) {
        int[] nums = {100,4,200,1,3,2};
        System.out.println(longestConsecutive(nums));
    }
}
