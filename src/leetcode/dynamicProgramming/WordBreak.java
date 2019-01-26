package leetcode.dynamicProgramming;

import java.util.List;

public class WordBreak {
    public boolean wordBreak(String s, List<String> wordDict) {
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        int maxLen = maxLenHelper(wordDict);
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= maxLen && j <= i; j++) {
                int start = i - j;
                String str = s.substring(start, i);
                if(dp[start] && wordDict.contains(str)) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    private int maxLenHelper(List<String> wordDict) {
        int maxLen = 0;
        for(String word : wordDict) {
            maxLen = Math.max(maxLen, word.length());
        }
        return maxLen;
    }
}
