package leetcode.twoPointer;

import java.util.*;

public class LongestSubstringWithAtMostTwoDistinctCharacters {
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        int max = 0;
        if (s == null || s.length() == 0) {
            return max;
        }
        int start = 0, n = s.length();
        Map<Character, Integer> map = new HashMap<>();
        for (int end = 0; end < n; end++){
            char c = s.charAt(end);
            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                map.put(c, map.get(c) + 1);
            }
            max = Math.max(max, end - start + 1);
            while (map.size() > 2) {
                int val = map.get(s.charAt(start)) - 1;
                if (val > 0) {
                    map.put(s.charAt(start), val);
                } else {
                    map.remove(s.charAt(start));
                }
                start++;
            }
        }
        max = Math.max(max, n - start + 1);
        return max;
    }

    public int lengthOfLongestSubstringTwoDistinct2(String s) {
        int max = 0;
        if (s == null || s.length() == 0) {
            return max;
        }
        int j = 0, n = s.length();
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            while (j < n && map.size() <= 2) {
                char c = s.charAt(j++);
                if (map.containsKey(c)) {
                    map.put(c, map.get(c) + 1);
                } else {
                    map.put(c, 1);
                }
                if (map.size() <= 2) {
                    max = Math.max(j - i, max);
                }
            }
            int val = map.get(s.charAt(i)) - 1;
            if (val == 0) {
                map.remove(s.charAt(i));
            } else {
                map.put(s.charAt(i), val);
            }
        }
        return max;
    }
}
