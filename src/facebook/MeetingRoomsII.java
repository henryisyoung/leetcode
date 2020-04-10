package facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingRoomsII {
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0 || intervals[0] == null || intervals[0].length == 0) return 0;
        int count  = 0;
        List<Node> list = new ArrayList<>();

        for (int[] ints : intervals) {
            list.add(new Node(ints[0], true));
            list.add(new Node(ints[1], false));
        }
        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.time == o2.time) {
                    if (o1.isStart) return 1;
                    return -1;
                }
                return o1.time - o2.time;
            }
        });
        int max = 0;
        for (Node node : list) {
            if (node.isStart) {
                count++;
                max = Math.max(count, max);
            } else {
                count--;
            }
        }
        return max;
    }

    class Node{
        int time;
        boolean isStart;
        public Node(int time, boolean isStart) {
            this.isStart = isStart;
            this.time = time;
        }
    }
}
