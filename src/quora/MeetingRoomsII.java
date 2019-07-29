package quora;

import leetcode.dataStructrue.segmentTree.Interval;

import java.util.*;

public class MeetingRoomsII {
    class Node{
        int time;
        boolean isStart;
        public Node(int time, boolean isStart) {
            this.isStart = isStart;
            this.time = time;
        }
    }
    public int minMeetingRooms(List<Interval> intervals) {
        if (intervals == null || intervals.size() == 0) return 0;
        List<Node> nodes = new ArrayList<>();
        for (Interval interval : intervals) {
            nodes.add(new Node(interval.start, true));
            nodes.add(new Node(interval.end, false));
        }
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.time == o2.time) {
                    if (o1.isStart) return -1;
                    return 1;
                }
                return o1.time - o2.time;
            }
        });
        int count = 0, max = 0;
        for (Node n : nodes) {
            if (n.isStart) {
                count++;
                max = Math.max(count, max);
            } else {
                count--;
            }
        }
        return max;
    }
}
