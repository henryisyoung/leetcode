package datastructure.twopointer;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringWithoutRepeatingCharacters {
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int j = 0, max = 0, n = s.length();
        for (int i = 0; i < n; i++) {
            while (j < n && !map.containsKey(s.charAt(j))) {
                map.put(s.charAt(j++), 1);
                max = Math.max(j - i, max);
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

    public static void main(String[] args) {
        String s = "pwwkew";
        System.out.println(lengthOfLongestSubstring(s));
    }
}
