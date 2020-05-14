package facebook;

import java.util.HashSet;
import java.util.Set;

public class LongestConsecutiveSequence {
    public int longestConsecutive(int[] nums) {
        int max = 0;
        Set<Integer> set = new HashSet<>();
        for (int a : nums) set.add(a);

        for (int a : nums) {
            int greater = a + 1, smaller = a - 1;
            int local = 1;
            set.remove(a);
            while (set.contains(greater)) {
                set.remove(greater);
                local++;
                greater++;
            }

            while (set.contains(smaller)) {
                set.remove(smaller);
                local++;
                smaller--;
            }
            max = Math.max(max, local);
        }
        return max;
    }
}
