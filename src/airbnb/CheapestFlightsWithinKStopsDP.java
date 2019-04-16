package airbnb;

import java.util.Arrays;

public class CheapestFlightsWithinKStopsDP {
    public int findCheapestPriceDP(int n, int[][] flights, int src, int dst, int K) {
        final int kInfCost = 1<<30;
        int[] cost = new int[n];
        Arrays.fill(cost, kInfCost);
        cost[src] = 0;

        for (int i = 0; i <= K; ++i) {
            int[] tmp = cost.clone();
            for(int[] p: flights)
                tmp[p[1]] = Math.min(tmp[p[1]], cost[p[0]] + p[2]);
            cost = tmp;
        }

        return cost[dst] >= kInfCost ? -1 : cost[dst];
    }

    public int findCheapestPriceDP2(int n, int[][] flights, int src, int dst, int K) {
        int[][] dp = new int[K + 2][n];
        for (int i = 0; i <= K + 1; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = 1<<30;
            }
        }
        dp[0][src] = 0;
        for (int i = 1; i <= K + 1; i++) {
            dp[i][src] = 0;
            for (int[] flight : flights) {
                dp[i][flight[1]] = Math.min(dp[i][flight[1]], dp[i - 1][flight[0]] + flight[2]);
            }
        }
        return dp[K + 1][dst] >= 1<<30 ? -1 : dp[K + 1][dst];
    }

    public static void main(String[] args) {
        int n = 3, src = 0, dst = 2, K = 0;
        int[][] flights ={{0,1,100},{1,2,100},{0,2,500}};
        CheapestFlightsWithinKStopsDP solver = new CheapestFlightsWithinKStopsDP();
        int num = solver.findCheapestPriceDP2(n, flights, src, dst, K);
        System.out.println("num " + num);
    }
}
