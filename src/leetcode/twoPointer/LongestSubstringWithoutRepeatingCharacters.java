package leetcode.twoPointer;

public class LongestSubstringWithoutRepeatingCharacters {
    public int lengthOfLongestSubstring(String s) {
        int[] map = new int[256];

        int j = 0, n = s.length();
        int ans = 0;
        for (int i = 0; i < n; i++) {
            while (j < n && map[s.charAt(j)] == 0) {
                map[s.charAt(j++)] = 1;
                ans = Math.max(ans, j - i);
            }
            map[s.charAt(i)] = 0;
        }
        return ans;
    }
}
