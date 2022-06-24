package apple;

public class LongestPalindromicSubstring {
    public String longestPalindrome(String s) {
        if(s == null || s.length() <= 1) return s;
        int len = s.length();
        int max = 0, start = 0;

        for(int i = 0; i < len - 1; i++) {
            int[] result1 = helper(s, i , i);
            int max1 = result1[0];
            int s1 = result1[1];
            if(max1 > max) {
                max = max1;
                start = s1;
            }
            int[] result2 = helper(s, i , i + 1);
            int max2 = result2[0];
            int s2 = result2[1];
            if(max2 > max) {
                max = max2;
                start = s2;
            }
        }

        return s.substring(start, start + max);
    }

    private int[] helper(String s, int l, int r) {
        while(l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            l--;
            r++;
        }
        int count = r - l - 1;
        int start = l + 1;
        return new int[]{count, start};
    }
}
