package google.vo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LongestStringChain {
    //https://leetcode.com/problems/longest-string-chain/discuss/814001/O(N)-Java-Solution-bucket-sort bucket
    public static int longestStrChain(String[] words) {
        Arrays.sort(words, (a, b) -> (a.length() - b.length()));
        int n = words.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        int max = 1;
        dp[0] = 1;

        for (int i = 1; i < n; i++) {
            String cur = words[i];
            for (int j = 0; j < i; j++) {
                String prev = words[j];
                if (isChain(cur, prev)) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            max = Math.max(max, dp[i]);
        }

        return max;
    }

    private static  boolean isChain(String cur, String prev) {
        if (prev.length() + 1 != cur.length()) {
            return false;
        }
        int i = 0, j = 0, count = 0;
        while (i < cur.length() && j < prev.length()) {
            if (cur.charAt(i) == prev.charAt(j)) {
                i++;
                j++;
            } else {
                if (count > 0) {
                    return false;
                }
                count++;
                i++;
            }
        }
        return true;
    }

    public int longestStrChain2(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        int max = 1;
        Arrays.sort(words, (a, b) -> a.length() - b.length());
        for(String word : words) {
            if(word.length() == 0) {
                map.put(word, 1);
            } else {
                int cur = 1;
                for(int i = 0; i < word.length(); i++) {
                    StringBuilder copy = new StringBuilder(word);

                    copy.deleteCharAt(i);
                    String tmp = copy.toString();
                    int prev = map.getOrDefault(tmp, 0);
                    cur = Math.max(cur, prev + 1);
                }
                map.put(word, cur);
                max = Math.max(max, cur);
            }

        }
        return max;
    }

    public static void main(String[] args) {
        String[] words = {"ksqvsyq","ks","kss","czvh","zczpzvdhx","zczpzvh","zczpzvhx","zcpzvh","zczvh","gr","grukmj","ksqvsq","gruj","kssq","ksqsq","grukkmj","grukj","zczpzfvdhx","gru"};
        System.out.println(longestStrChain(words));
    }
}
