package facebook;

public class LongestSubstringWithoutRepeatingCharacters {
    public static int lengthOfLongestSubstring(String s) {
        int result = 0;
        int j = 0, n = s.length();
        int[] table = new int[26];
        for (int i = 0; i < n; i++) {
            while (j < n && table[s.charAt(j) - 'a'] == 0) {
                table[s.charAt(j++) - 'a']++;
                result = Math.max(j - i, result);
            }
            table[s.charAt(i) - 'a']--;
        }
        return result;
    }

    public static void main(String[] args) {
        String s = "abcabcbb";
        System.out.println(lengthOfLongestSubstring(s));
    }
}
