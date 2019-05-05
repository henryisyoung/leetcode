package leetcode.dynamicProgramming;

import java.util.Arrays;

public class ScrambleString {
    public boolean isScramble(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        }
        if (s1.length() == 0 || s1.equals(s2)) {
            return true;
        }
        if (!isValid(s1, s2)) {
            return false;
        }
        int n = s1.length();
        for (int i = 1; i < n; i++) {
            String s11 = s1.substring(0, i), s12 = s1.substring(i, n);
            String s21 = s2.substring(0, i), s22 = s2.substring(i, n);
            String s31 = s2.substring(n - i, n), s32 = s2.substring(0, n - i);
            if (isScramble(s11, s21) && isScramble(s12, s22)) {
                return true;
            }
            if (isScramble(s11, s31) && isScramble(s12, s32)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValid(String s1, String s2) {
        char[] a = s1.toCharArray();
        char[] b = s2.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        if (!(new String(a)).equals(new String(b))) {
            return false;
        }
        return true;
    }

    public boolean isScrambleDP(String s1, String s2) {
        int len = s1.length();
        int [][][] visit = new int[len][len][len + 1];
        return checkScramble(s1,0,s2,0, len, visit);
    }

    private boolean checkScramble(String s1, int s1Start, String s2, int s2Start, int k, int[][][] visit) {
        if (visit[s1Start][s2Start][k] != 0) {
            return visit[s1Start][s2Start][k] == 1;
        }
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            visit[s1Start][s2Start][k] = -1;
            return false;
        }
        if (s1.length() == 0 || s1.equals(s2)) {
            visit[s1Start][s2Start][k] = 1;
            return true;
        }
        if (!isValid(s1, s2)) {
            visit[s1Start][s2Start][k] = -1;
            return false;
        }
        for (int i = 1; i < s1.length(); i++) {
            String s11 = s1.substring(0, i);
            String s12 = s1.substring(i, s1.length());

            String s21 = s2.substring(0, i);
            String s22 = s2.substring(i, s2.length());

            String s23 = s2.substring(0, s2.length() - i);
            String s24 = s2.substring(s2.length() - i, s2.length());
            if (checkScramble(s11, s1Start, s21, s2Start, i, visit) && checkScramble(s12, s1Start + i, s22, s2Start + i, k - i, visit)) {
                visit[s1Start][s2Start][k] = 1;
                return true;
            }

            if (checkScramble(s11, s1Start, s24, s2Start + k - i, i, visit) && checkScramble(s12, s1Start + i, s23, s2Start, k - i, visit)) {
                visit[s1Start][s2Start][k] = 1;
                return true;
            }
        }
        visit[s1Start][s2Start][k] = -1;
        return false;
    }
}
