package datastructure.twopointer;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringWithAtMostKDistinctCharacters {
    public static int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (s.length() <= k) return s.length();
        Map<Character, Integer> map = new HashMap<>();
        int j = 0, max = 0, n = s.length();
        for (int i = 0; i < n; i++) {
            while (j < n && map.size() <= k) {
                char c = s.charAt(j++);
                map.put(c, map.getOrDefault(c, 0) + 1);
                if (map.size() <= k) max = Math.max(max, j - i);
            }
            char ic = s.charAt(i);
            int val = map.get(ic) - 1;
            if (val == 0) {
                map.remove(ic);
            } else {
                map.put(ic, val);
            }
        }
        return max;
    }

    public static void main(String[] args) {
        String s = "eceba";
        int k = 2;
        System.out.println(lengthOfLongestSubstringKDistinct(s, k));
    }
}
