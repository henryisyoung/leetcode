package snowflake.mianjing.selfreview;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MaximumNumberEventsCanAttended {
    public int maxEvents(int[][] events) {
        Arrays.sort(events, Comparator.comparingInt(a -> a[0]));
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        int maxDay = 0, minDay = events[0][0];
        for (int[] event : events) {
            maxDay = Math.max(event[1], maxDay);
        }
        int i = 0, n = events.length, count = 0;
        for (int d = minDay; d <= maxDay; d++) {
            while (i < n && d >= events[i][0]) {
                pq.add(events[i][1]);
                i++;
            }

            while (!pq.isEmpty() && pq.peek() < d) {
                pq.poll();
            }

            if (!pq.isEmpty()) {
                pq.poll();
                count++;
            }

            if (i == n && pq.isEmpty()) break;
        }
        return count;
    }

    public int maxValue(int[][] events, int k) {
        Arrays.sort(events, Comparator.comparingInt(a -> a[1]));
        int n = events.length;
        int[] endTime = new int[n];
        for (int i = 0; i < n; i++) {
            endTime[i] = events[i][1];
        }
        int[][] dp = new int[n + 1][k + 1];

        for (int i = 1 ; i <= n; i++) {
            for (int j = 1; j <= k; j++) {
                int curS = events[i - 1][0], curV = events[i - 1][2];
                int lastIndex = binaryFind(endTime, curS);
                int prev = lastIndex == -1 ? 0 : lastIndex + 1;

                int skipCur = dp[i - 1][j];
                int useCur = dp[prev][j - 1] + curV;
                dp[i][j] = Math.max(skipCur, useCur);
            }
        }
        return dp[n][k];
    }

    private int binaryFind(int[] times, int time) {
        if (times.length == 0) return -1;
        int left = 0, right = times.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (times[mid] >= time) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (times[right] < time) return right;
        if (times[left] < time) return left;
        return -1;
    }
}