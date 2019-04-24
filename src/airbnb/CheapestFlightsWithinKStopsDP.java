package airbnb;

import java.util.Arrays;

public class CheapestFlightsWithinKStopsDP {
    public int findCheapestPriceDP(int n, int[][] flights, int src, int dst, int K) {
        int[][] dist = new int[2][n];
        int INF = Integer.MAX_VALUE / 2;
        Arrays.fill(dist[0], INF);
        Arrays.fill(dist[1], INF);
        dist[0][src] = dist[1][src] = 0;

        for (int i = 1; i <= K + 1; ++i) {
            for (int[] edge : flights) {
                dist[i % 2][edge[1]] = Math.min(dist[i % 2][edge[1]], dist[(i - 1) % 2][edge[0]] + edge[2]);
            }
        }
        return dist[(K + 1) % 2][dst] < INF ? dist[(K + 1) % 2][dst] : -1;
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
