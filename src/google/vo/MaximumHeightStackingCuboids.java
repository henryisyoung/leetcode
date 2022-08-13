package google.vo;

import java.util.Arrays;

public class MaximumHeightStackingCuboids {
    public int maxHeight(int[][] cuboids) {
        for (int[] arr : cuboids) {
            Arrays.sort(arr);
        }

        Arrays.sort(cuboids, (a, b) -> {
            if (a[0] != b[0]) {
                return a[0] - b[0];
            }
            if (a[1] != b[1]) {
                return a[1] - b[1];
            }
            return a[2] - b[2];
        });
        int n = cuboids.length;
        int[] dp = new int[n];
        dp[0] = cuboids[0][2];
        int max = dp[0];
        for (int i = 1; i < n; i++) {
            dp[i] = cuboids[i][2];
            for (int j = 0; j < i; j++) {
                if (valid(cuboids[j], cuboids[i])) {
                    dp[i] = Math.max(dp[i], dp[j] + cuboids[i][2]);
                }
            }
            max = Math.max(max, dp[i]);
        }

        return max;
    }

    private boolean valid(int[] prev, int[] cur) {
        return prev[0] <= cur[0] && prev[1] <= cur[1] && prev[2] <= cur[2];
    }
}
