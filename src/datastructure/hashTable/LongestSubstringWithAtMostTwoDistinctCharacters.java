package datastructure.hashTable;
import java.util.*;

public class LongestSubstringWithAtMostTwoDistinctCharacters {
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        if (s == null || s.length() == 0) return 0;
        int j = 0, n = s.length(), min = 0;
        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < n; i++) {
            while (j < n && map.size() <= 2) {
                char c = s.charAt(j++);
                map.put(c, map.getOrDefault(c, 0) + 1);
                if (map.size() < 3) {
                    min = Math.max(min, j - i);
                }
            }

            char cur = s.charAt(i);
            int val = map.get(cur) - 1;
            if (val == 0) {
                map.remove(cur);
            } else {
                map.put(cur, val);
            }
        }

        return min;
    }
}
