package wework;

public class MinimumWindowSubstring {
//    Input: S = "ADOBECODEBANC", T = "ABC"
//    Output: "BANC"
    public String minWindow(String s, String t) {
        if (t.length() > s.length()) {
            return "";
        }
        int m = s.length(), n = t.length();
        int[] source = new int[256], target =  new int[256];

        for (char c : t.toCharArray()) {
            target[c]++;
        }
        String result = "";
        int j = 0, len = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            while (j < m && !isValid(source, target)) {
                char c = s.charAt(j++);
                source[c]++;
            }
            if (isValid(source, target) && len > j - i) {
                len = j - i;
                result = s.substring(i, j);
            }
            source[s.charAt(i)]--;
        }
        return result;
    }

    private boolean isValid(int[] source, int[] target) {
        for (int i = 0; i < 256; i++) {
            if (source[i] < target[i]) {
                return false;
            }
        }
        return true;
    }
}
