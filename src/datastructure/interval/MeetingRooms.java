package datastructure.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingRooms {
    class Node{
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.pos = pos;
            this.isStart = isStart;
        }
    }

    public boolean canAttendMeetings(int[][] intervals) {
        List<Node> nodes = new ArrayList<>();
        for(int[] interval : intervals) {
            nodes.add(new Node(interval[0], true));
            nodes.add(new Node(interval[1], false));
        }
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                if(a.pos != b.pos) {
                    return a.pos -b.pos;
                }
                if(a.isStart) {
                    if(!b.isStart) return 1;
                }
                return -1;
            }
        });

        int count = 0;
        for(Node n : nodes) {
            if(n.isStart) {
                count++;
                if(count > 1) return false;
            } else {
                count--;
            }
        }
        return true;
    }
}
