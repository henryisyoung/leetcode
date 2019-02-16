package leetcode.twoPointer;

import java.util.*;

public class LongestSubstringWithAtMostKDistinctCharacters {
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        int max = 0;
        if (s == null || s.length() == 0) {
            return max;
        }
        Map<Character, Integer> map = new HashMap<>();
        int n = s.length(), start = 0;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (map.containsKey(c)) {
                map.put(c, 1 + map.get(c));
            } else {
                map.put(c, 1);
            }
            while (map.size() > k) {
                char startC = s.charAt(start++);
                if (map.get(startC) == 1) {
                    map.remove(startC);
                } else {
                    map.put(startC, map.get(startC) - 1);
                }
            }
            max = Math.max(max, i - start + 1);
        }
        max = Math.max(max, n - start);
        return max;
    }
}
