package uber;

import java.util.Arrays;
import java.util.Comparator;

public class MaximumLengthOfPairChain {
    public static int findLongestChain(int[][] pairs) {
        if (pairs == null || pairs.length == 0 || pairs[0] == null || pairs[0].length == 0) {
            return 0;
        }
        int n = pairs.length;
        int[] dp = new int[n];
        dp[0] = 1;
        Arrays.sort(pairs, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] == o2[0]) return o1[1] - o2[1];
                return o1[0] - o2[0];
            }
        });
        for (int i = 1; i < n; i++) {
            dp[i] = dp[i - 1];
            for (int j = i - 1; j >= 0; j--) {
                if (pairs[j][1] < pairs[i][0]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
        int[][] pairs = {{1,2}, {2,3}, {3,4}};
        System.out.println(findLongestChain(pairs));
    }

}
