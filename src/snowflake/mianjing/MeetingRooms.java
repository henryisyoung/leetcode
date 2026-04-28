package snowflake.mianjing;

import java.util.*;

public class MeetingRooms {
    public boolean canAttendMeetings(int[][] intervals) {
        if(intervals == null || intervals.length == 0) {
            return true;
        }
        int n = intervals.length;
        Arrays.sort(intervals, (a, b) -> (a[0] - b[0]));

        int[] prev = intervals[0];
        for (int i = 1; i < n; i++) {
            if (prev[1] > intervals[i][0]) return false;
            prev = intervals[i];
        }
        return true;
    }

    static class Node {
        int time;
        boolean isStart;
        public Node(int time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }
    }

    public int minMeetingRooms(int[][] intervals) {
        if(intervals == null || intervals.length == 0) {
            return 0;
        }
        List<Node> list = new ArrayList<>();
        for (int[] meeting : intervals) {
            int st = meeting[0], et = meeting[1];
            list.add(new Node(st, true));
            list.add(new Node(et, false));
        }
        Collections.sort(list, (a, b) -> {
            if (a.time != b.time) return a.time - b.time;
            if (a.isStart != b.isStart) {
                if (a.isStart) return 1;
                return -1;
            }
            return 0;
        });

        int max = 0;
        int count  = 0;
        for (Node n : list) {
            if (n.isStart) count++;
            else count--;
            max = Math.max(max, count);
        }
        return max;
    }
}
