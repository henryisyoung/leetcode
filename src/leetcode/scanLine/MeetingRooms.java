package leetcode.scanLine;

import java.util.*;

public class MeetingRooms {
    public class Interval {
      int start;
      int end;
      public Interval() { start = 0; end = 0; }
      public Interval(int s, int e) { start = s; end = e; }
    }


    public boolean canAttendMeetings(Interval[] intervals) {
        if (intervals == null || intervals.length == 0) {
            return true;
        }
        Arrays.sort(intervals, new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                return o1.start - o2.start;
            }
        });
        int end = intervals[0].end;
        for (int i = 1; i < intervals.length; i++) {
            Interval interval = intervals[i];
            if (interval.start < end) {
                return false;
            }
            end = Math.max(end, interval.end);
        }
        return true;
    }
}
