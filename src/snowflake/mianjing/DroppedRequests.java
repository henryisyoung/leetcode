package snowflake.mianjing;

import java.util.ArrayList;
import java.util.List;

/*
Problem Overview
Imagine your website is getting too many visitors at once. To handle the load, you turn on a rate limiter. Now, you need to identify exactly which requests were blocked.

You are given an array called requestTimes. This array lists the arrival time (in seconds) for every request. The times are sorted in non-decreasing order (earliest to latest).

Your goal is to return a new array containing the timestamps of the requests that were blocked.

A request is blocked if it breaks either of these two rules:

Rule 1: There are more than 3 requests happening in the same second.
Rule 2: There are more than 20 requests happening in any 10-second rolling window.
If a request breaks both rules at the same time, simply list it once in your output.

Illustrative Examples
Example 1:

Input: requestTimes = [1,1,1,1,2,2,2,2,3]

Output: [1,2]

Breakdown: The system allows 3 requests at second 1. The 4th request arrives at second 1, so it is blocked. Later, the 4th request at second 2 is also blocked.

Example 2:

Input: requestTimes = [1,1,1,2,2,2,3,3,3,4,4,4,5,5,5,6,6,6,7,7,7,8]

Output: [7,8]

Breakdown: The request at index 20 (which arrives at second 7) is the 21st request within a 10-second period. Because the limit is 20, this request is blocked. The next request at second 8 is also blocked for the same reason.

Input Limits
1 <= requestTimes.length <= 2 * 10^5
1 <= requestTimes[i] <= 10^9
requestTimes is sorted in non-decreasing order.
 */
public class DroppedRequests {

    /**
     * Returns the timestamps of dropped requests.
     *
     * Two rules:
     *   Rule 1 (per-second): more than 3 requests in the same second → drop from the 4th on.
     *   Rule 2 (rolling):    more than 20 requests within any 10-second window → drop from the 21st on.
     *
     * Because the input is sorted, both rules collapse to O(1) checks per request:
     *   Rule 1 fires at index i  ⇔  i >= 3  AND  requests[i]  == requests[i - 3]
     *                              (i.e. there are already 3 requests with the same timestamp).
     *   Rule 2 fires at index i  ⇔  i >= 20 AND  requests[i] - requests[i - 20] < 10
     *                              (the 20 prior requests all fall inside a 10-second window with this one).
     *
     * Both rules count ALL incoming requests (blocked or not) — matches the
     * "the 21st request within a 10-second period" wording in the prompt.
     *
     * If both rules fire on the same request, we list it once (short-circuit OR).
     *
     * Time:  O(n)
     * Space: O(output)
     */
    public List<Integer> droppedReq(int[] requests) {
        List<Integer> result = new ArrayList<>();
        if (requests == null || requests.length == 0) {
            return result;
        }

        for (int i = 0; i < requests.length; i++) {
            boolean dropped =
                    (i >= 3  && requests[i]      == requests[i - 3]) ||
                    (i >= 20 && requests[i] - requests[i - 20] < 10);
            if (dropped) {
                result.add(requests[i]);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        DroppedRequests s = new DroppedRequests();

        // Example 1: 4 requests at second 1 → drop the 4th; same at second 2.
        System.out.println(s.droppedReq(
                new int[]{1, 1, 1, 1, 2, 2, 2, 2, 3})); // [1, 2]

        // Example 2: 21st request (index 20) at t=7 trips the 10-second window;
        // index 21 at t=8 also trips it.
        System.out.println(s.droppedReq(
                new int[]{1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4,
                          5, 5, 5, 6, 6, 6, 7, 7, 7, 8})); // [7, 8]

        // Empty / null
        System.out.println(s.droppedReq(new int[0])); // []
        System.out.println(s.droppedReq(null));       // []

        // Both rules fire at once: 4 same-second requests AND 21+ in 10s.
        // Build 21 requests all at second 5: indices 0..20.
        // index 3 trips Rule 1, index 20 trips both — should still be reported once.
        int[] big = new int[22];
        for (int i = 0; i < 21; i++) big[i] = 5;
        big[21] = 6;
        System.out.println(s.droppedReq(big)); // [5, 5, 5, ... , 5] x18 (indices 3..20)

        // Spread out enough that nothing is dropped.
        System.out.println(s.droppedReq(
                new int[]{1, 11, 21, 31})); // []
    }
}
