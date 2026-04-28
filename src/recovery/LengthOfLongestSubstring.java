package recovery;

import java.util.HashMap;
import java.util.Map;

public class LengthOfLongestSubstring {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> counter = new HashMap<>();
        int left = 0, right = 0, maxLen = 0;
        for (;left < s.length() && right < s.length(); left++) {
            while (right < s.length() && !counter.containsKey(s.charAt(right))) {
                counter.put(s.charAt(right++), 1);
                maxLen = Math.max(maxLen, right - left);
            }
            int count = counter.get(s.charAt(left));
            if (count == 1) {
                counter.remove(s.charAt(left));
            } else {
                counter.put(s.charAt(left), count - 1);
            }
        }
        return maxLen;
    }
}
