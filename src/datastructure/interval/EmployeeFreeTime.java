package datastructure.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeFreeTime {
    class Interval {
        public int start;
        public int end;

        public Interval() {}

        public Interval(int _start, int _end) {
            start = _start;
            end = _end;
        }
    };

    class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }
    }

    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
        List<Interval> result = new ArrayList<>();
        if (schedule == null || schedule.size() == 0) return result;
        List<Node> nodes = new ArrayList<>();
        for (List<Interval> l : schedule) {
            for (Interval interval : l) {
                nodes.add(new Node(interval.start, true));
                nodes.add(new Node(interval.end, false));
            }
        }

        Collections.sort(nodes, (a, b) -> {
            if (a.pos != b.pos) return a.pos - b.pos;
            if (a.isStart) {
                if (!b.isStart) return -1;
            }
            if (!a.isStart) {
                if (b.isStart) return 1;
            }
            return 0;
        });

        Integer prev = null;
        int count = 0;
        for (Node node : nodes) {
            if (node.isStart) {
                if (count == 0 && prev != null && prev != node.pos) {
                    result.add(new Interval(prev, node.pos));
                }
                count++;
                prev = node.pos;
            } else {
                count--;
                prev = node.pos;
            }
        }
        return result;
    }

    public static void main(String[] args) {

    }
}
