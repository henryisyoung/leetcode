package wework;

public class MinimumWindowSubstring {
//    Input: S = "ADOBECODEBANC", T = "ABC"
//    Output: "BANC"
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";
        int[] target = new int[256], source = new int[256];
        for (char c : t.toCharArray()) {
            target[c]++;
        }
        int i = 0, j = 0, max = Integer.MAX_VALUE;
        String result = "";
        while (i < s.length()) {
            while (j < s.length() && !isValid(source, target)) {
                char c = s.charAt(j++);
                source[c]++;
            }
            if (j - i < max && isValid(source, target)) {
                max = j - i;
                result = s.substring(i, j);
            }
            source[s.charAt(i)]--;
            i++;
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
