package leetcode.scanLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingRoomsII {
    public class Interval {
        int start;
        int end;
        public Interval() { start = 0; end = 0; }
        public Interval(int s, int e) { start = s; end = e; }
    }

    private class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.pos = pos;
            this.isStart = isStart;
        }
    }

    public int minMeetingRooms(Interval[] intervals) {
        int count = 0, max = 0;
        if (intervals == null || intervals.length == 0) {
            return count;
        }
        List<Node> list = new ArrayList<>();
        for (Interval interval : intervals) {
            int start = interval.start, end = interval.end;
            Node s = new Node(start, true), e = new Node(end, false);
            list.add(s);
            list.add(e);
        }
        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos != o2.pos) {
                    return o1.pos - o2.pos;
                }
                if (!o1.isStart) {
                    return -1;
                }
                return 1;
            }
        });
        for (Node n : list) {
            if (n.isStart) {
                count++;
                max = Math.max(max, count);
            } else {
                count--;
            }
        }
        return max;
    }
}
