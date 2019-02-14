package leetcode.twoPointer;

public class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        if (s == null || t == null || s.length() < t.length()) {
            return "";
        }
        String result = "";
        int[] target = new int[256], source = new int[256];
        for (char c : t.toCharArray()) {
            target[c]++;
        }
        int j = 0, len = Integer.MAX_VALUE, n = s.length();
        for (int i = 0; i < n; i++) {
            while (j < n && !isValid(target, source)) {
                char c = s.charAt(j++);
                source[c]++;
            }
            if (isValid(target, source) && len > j - i) {
                len = j - i;
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
