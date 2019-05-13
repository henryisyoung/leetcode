package quora;

import java.util.*;

public class MeetingRoomsII {
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0 || intervals[0] == null || intervals[0].length == 0) {
            return 0;
        }
        int m = intervals.length, n = intervals[0].length;
        List<Node> nodes = new ArrayList<>();
        for (int[] ints : intervals) {
            nodes.add(new Node(ints[0], true));
            nodes.add(new Node(ints[1], false));
        }
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos == o2.pos) {
                    if (!o1.isStart) {
                        return -1;
                    }
                    return 1;
                } else {
                    return o1.pos - o2.pos;
                }
            }
        });
        int count = 0, max = 0;
        for (Node node : nodes) {
            if (node.isStart) {
                count++;
                max = Math.max(count, max);
            } else {
                count--;
            }
        }
        return max;
    }

    class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }
    }
}
