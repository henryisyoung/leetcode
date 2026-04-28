package snowflake.mianjing;

import java.util.Arrays;
import java.util.PriorityQueue;

/*
Maximum Number of Events That Can Be Attended
Problem Overview
You are given a list of events. Each event has a start day and an end day, represented as [startDay, endDay].

Here are the rules:

You can choose to attend an event on any day between its start and end dates (inclusive).
You can only attend one event per day.
Your goal is to find the maximum number of events you can attend.

Sample Cases
Example 1:

Input: events = [[1,2],[2,3],[3,4]]
Output: 3
Example 2:

Input: events = [[1,2],[2,3],[3,4],[1,2]]
Output: 4
Input Limits
1 <= events.length <= 10^5
events[i].length == 2
1 <= startDay_i <= endDay_i <= 10^5
How to Solve It
To solve this problem efficiently, we need a smart plan. The best approach is to be "greedy." This means making the best possible choice at each specific moment.

The best choice is to always attend the event that ends the soonest.

Why? Because an event that ends soon expires quickly. If we don't attend it now, we might lose the chance forever. Events that end later can wait for future days.

Here is the step-by-step logic:

Sort the Data: First, sort all events based on their startDay. This allows us to process days in order and know exactly which events become available on which day.
Use a Min-Heap: We need a way to track the endDay of all currently available events. A Min-Heap (or Priority Queue) is perfect for this. It keeps the event with the smallest endDay at the top.
Check Each Day: Loop through the days starting from day 1.
Add New Events: If any events start on the current day, add their endDay to the Min-Heap.
Remove Expired Events: Check the top of the heap. If an event ended before the current day, remove it. We can no longer attend it.
Attend an Event: If the Min-Heap is not empty, pick the top item (the one ending soonest). This counts as attending one event. Remove it from the heap and move to the next day.

part2:
You are provided with a list of events. Each event is stored as [startDay, endDay, value].

The event begins on startDay.
The event finishes on endDay.
If you choose to attend this event, you gain a score of value.
You must follow these rules:

You can attend a maximum of k events.
You cannot attend two events at the same time.
Timing Rule: If you attend an event that ends on day d, the next event you choose must start strictly after day d (meaning day d + 1 or later).

Your goal is to find the highest total value you can earn by picking up to k non-overlapping events.

Sample Cases
Case 1:

Input: events = [[1,2,4],[3,4,3],[2,3,1]], k = 2

Output: 7

Case 2:

Input: events = [[1,2,4],[3,4,3],[2,3,10]], k = 2

Output: 10

Input Limits
k is at least 1 and does not exceed the total number of events.
The number of events is between 1 and 10^5.
startDay and endDay can be as large as 10^9.
value can be up to 10^6.
 */
public class MaximumNumberEvents {
    public int maxEvents(int[][] events) {
        Arrays.sort(events, (a, b) -> Integer.compare(a[0], b[0]));
        int maxDay = 0;
        for (int[] e : events) maxDay = Math.max(maxDay, e[1]);
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        int count = 0, i = 0, n = events.length;
        for (int day = 1; day <= maxDay; day++) {
            while (i < n && events[i][0] <= day) {
                pq.offer(events[i++][1]);
            }
            while (!pq.isEmpty() && pq.peek() < day) {
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

    // part2
    public int maxEventsVal(int[][] events, int k) {
        if (events == null || events.length == 0 || k == 0) {
            return 0;
        }
        int n = events.length;
        Arrays.sort(events, (a, b) -> Integer.compare(a[1], b[1]));

        int[] endTime = new int[n];
        for (int i = 0; i < n; i++) {
            endTime[i] = events[i][1];
        }

        int[][] dp = new int[n + 1][k + 1];
        for (int i = 1; i <= n; i++) {
            int curStart = events[i - 1][0];
            int curVal   = events[i - 1][2];
            // Largest 0-based index p with endTime[p] < curStart, or -1.
            // Independent of j, so hoist out of the inner loop.
            int p = binaryFind(curStart, endTime);
            int prevBestBase = (p == -1) ? 0 : p + 1;   // 1-based DP row to look up
            for (int j = 1; j <= k; j++) {
                int skip = dp[i - 1][j];
                int take = curVal + dp[prevBestBase][j - 1];
                dp[i][j] = Math.max(skip, take);
            }
        }
        return dp[n][k];
    }

    // Returns the largest 0-based index p with times[p] < time, or -1.
    private int binaryFind(int time, int[] times) {
        if (times.length == 0) return -1;
        int left = 0, right = times.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (times[mid] < time) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (times[right] < time) return right;
        if (times[left]  < time) return left;
        return -1;
    }

    public static void main(String[] args) {
        MaximumNumberEvents s = new MaximumNumberEvents();

        // Part 2 cases
        System.out.println(s.maxEventsVal(new int[][]{{1,2,4},{3,4,3},{2,3,1}}, 2)); // 7
        System.out.println(s.maxEventsVal(new int[][]{{1,2,4},{3,4,3},{2,3,10}}, 2)); // 10
        System.out.println(s.maxEventsVal(new int[][]{{1,2,4},{3,4,3},{2,3,10}}, 1)); // 10
        System.out.println(s.maxEventsVal(new int[][]{{1,1,5}}, 1)); // 5
    }
}
