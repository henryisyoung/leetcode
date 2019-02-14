package leetcode.twoPointer;

public class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        if (s == null || t == null || s.length() < t.length()) {
            return null;
        }
        String result = null;
        int[] target = new int[256], source = new int[256];
        for (char c : t.toCharArray()) {
            target[c] += 1;
        }
        int j = 0, len = Integer.MAX_VALUE;
        for (int i = 0;i < s.length(); i++) {
            while (j < s.length() && !isValid(target, source)) {
                char c = s.charAt(j++);
                source[c] += 1;
            }
            if (isValid(target, source)) {
                len = Math.min(j - i, len);
                result = s.substring(i, j);
            }
            source[s.charAt(i)]--;
        }
        return result;
    }

    private boolean isValid(int[] target, int[] source) {
        for (int i = 0; i < 256; i++) {
            if (target[i] > source[i]) {
                return false;
            }
        }
        return true;
    }
}
