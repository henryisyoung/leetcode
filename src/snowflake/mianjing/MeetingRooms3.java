package snowflake.mianjing;
/*
LeetCode 2402. Meeting Rooms III

You are given an integer n. There are n rooms numbered from 0 to n - 1.

You are given a 2D integer array meetings where meetings[i] = [start_i, end_i]
means that a meeting will be held during the half-closed time interval
[start_i, end_i). All the values of start_i are unique.

Meetings are allocated to rooms by these rules:
  1. Each meeting will take place in the unused room with the lowest number.
  2. If there are no available rooms, the meeting will be delayed until a room
     becomes free. The delayed meeting keeps its original duration.
  3. When a room becomes free, the meeting that has waited the longest is
     assigned to it.

Return the number of the room that held the most meetings. If there are
multiple rooms, return the room with the lowest number.

Example 1:
  n = 2, meetings = [[0,10],[1,5],[2,7],[3,4]]
  -> Room 0 holds meetings (0,10) and (10,11). Room 1 holds (1,5) and (5,10).
  Room 0 held 2 meetings, room 1 held 2. Return 0 (smaller id).

Example 2:
  n = 3, meetings = [[1,20],[2,10],[3,5],[4,9],[6,8]]
  -> Return 1.
 */

import java.util.Arrays;
import java.util.PriorityQueue;

public class MeetingRooms3 {

    /*
    Two min-heaps:
      - free : rooms currently available, ordered by room id (smaller first).
      - busy : rooms currently in use, ordered by (endTime, roomId).

    Sort meetings by start time. For each meeting [s, e]:
      1. Move every busy room whose endTime <= s back to `free` (it has finished).
      2. If `free` is non-empty, take the smallest room id, schedule [s, e].
      3. Otherwise the meeting is delayed: pop the busy room that frees the
         earliest. The delayed meeting starts at that room's endTime and keeps
         its original duration (e - s), so the new endTime is endTime + (e - s).
      4. Increment count[roomId] and push the room back into `busy` with its
         new endTime.

    Finally, scan count[] for the max; tie -> smaller id.

    Time:  O((m + n) log n) where m = meetings.length.
    Space: O(n).
     */
    public int mostBooked(int n, int[][] meetings) {
        int[] count = new int[n];
        PriorityQueue<Integer> free = new PriorityQueue<>();
        // entries are [endTime, roomId]; order by endTime, then roomId
        PriorityQueue<long[]> busy = new PriorityQueue<>((a, b) -> {
            if (a[0] == b[0]) {
                return Long.compare(a[1], b[1]);
            }
            return Long.compare(a[0], b[0]);
        });

        Arrays.sort(meetings, (a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 0; i < n; i++) free.add(i);

        for (int[] meeting : meetings) {
            int s = meeting[0], e = meeting[1];
            // Release rooms that have finished by the time this meeting starts.
            // Intervals are half-closed [s, e), so endTime == s is free.
            while (!busy.isEmpty() && busy.peek()[0] <= s) {
                long[] room = busy.poll();
                free.add((int) room[1]);
            }

            int roomId;
            long endTime;
            if (!free.isEmpty()) {
                roomId = free.poll();
                endTime = e;
            } else {
                long[] top = busy.poll();
                roomId = (int) top[1];
                endTime = top[0] + (e - s);
            }
            busy.add(new long[]{ endTime, roomId });
            count[roomId]++;
        }

        int best = 0, max = count[0];
        for(int i = 1; i < n; i++) {
            if (count[i] > max) {
                max = count[i];
                best = i;
            }
        }

        return best;
    }

    public static void main(String[] args) {
        MeetingRooms3 s = new MeetingRooms3();

        System.out.println(s.mostBooked(2, new int[][]{{0,10},{1,5},{2,7},{3,4}})); // 0
        System.out.println(s.mostBooked(3, new int[][]{{1,20},{2,10},{3,5},{4,9},{6,8}})); // 1
        System.out.println(s.mostBooked(4, new int[][]{{18,19},{3,12},{17,19},{2,13},{7,10}})); // 0
        System.out.println(s.mostBooked(2, new int[][]{{7,10},{2,3},{4,5},{6,8}})); // 0
    }
}
