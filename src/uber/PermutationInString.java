package uber;

import java.util.*;

public class PermutationInString {
    public static boolean checkInclusion(String s1, String s2) {
        if (s1 == null || s2.length() == 0) return true;
        if (s2 == null || s2.length() < s1.length()) return false;
        int n = s1.length(), m = s2.length();
        int[] table = new int[256];
        for (char c : s1.toCharArray()) {
            table[c]++;
        }
        int[] init = new int[256];
        for (int i = 0; i < n; i++) {
            char c = s2.charAt(i);
            init[c]++;
        }
        if (Arrays.equals(init, table)){
            return true;
        }
        for (int i = n; i < m; i++) {
            char remove = s2.charAt(i - n);
            char add = s2.charAt(i);
            init[remove]--;
            init[add]++;
            if (Arrays.equals(init, table)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String s1 = "ab", s2 = "eidbaooo";
        String s11 = "ab", s21 = "eidboaoo";
        System.out.println(checkInclusion(s1, s2));
        System.out.println(checkInclusion(s11, s21));
    }
}
