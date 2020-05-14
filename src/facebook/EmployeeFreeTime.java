package facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        @Override
        public String toString() {
            return start + "-" + end;
        }
    };

    class Node{
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }
    }

    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
        List<Interval> result = new ArrayList<>();
        List<Node> list = new ArrayList<>();
        for (List<Interval> sc : schedule) {
            for (Interval interval : sc) {
                list.add(new Node(interval.start, true));
                list.add(new Node(interval.end, false));
            }
        }

        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos != o2.pos) return o1.pos - o2.pos;
                if (o1.isStart) return 1;
                return -1;
            }
        });

        int count = 0;
        Integer last = null;

        for (Node n : list) {
            if (n.isStart) {
                if (count == 0 && last != null && last != n.pos) {
                    result.add(new Interval(last, n.pos));
                }
                last = n.pos;
                count++;
            } else {
                count--;
                last = n.pos;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        EmployeeFreeTime solution = new EmployeeFreeTime();

    }
}
