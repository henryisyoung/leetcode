package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning2 {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        if (s == null || s.length() == 0) {
            return result;
        }
        boolean[][] isPalindrome = isPalindrome(s);
        dfsSearchAll(s, 0, result, new ArrayList<>(), isPalindrome);
        return result;
    }

    private void dfsSearchAll(String s, int pos, List<List<String>> result, ArrayList<String> list, boolean[][] isPalindrome) {
        if (pos == s.length()) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos + 1; i < s.length(); i++) {
            String sub = s.substring(pos, i);
            if (isPalindrome[pos][i - 1]) {
                list.add(sub);
                dfsSearchAll(s, i, result, list, isPalindrome);
                list.remove(list.size() - 1);
            }
        }
    }

    private boolean[][] isPalindrome(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
            }
        }
        for (int len = 2; len < n; len++) {
            for (int start = 0; start + len < n; start++) {
                if (dp[start + 1][start + len - 1] && s.charAt(start) == s.charAt(start + len)) {
                    dp[start][start + len] = true;
                }
            }
        }
        return dp;
    }
}
