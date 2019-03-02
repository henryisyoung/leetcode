package airbnb;

import leetcode.dataStructrue.segmentTree.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingTime {
    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
        List<Interval> result = new ArrayList<>();
        boolean isAvailable = false;
        List<Node> nodes = new ArrayList<>();
        for (List<Interval> list : schedule) {
            for (Interval interval : list) {
                Node s = new Node(interval.start, true), e = new Node(interval.end, false);
                nodes.add(s);
                nodes.add(e);
            }
        }
        int[] arr = new int[2];
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos != o2.pos) {
                    return o1.pos - o2.pos;
                } else {
                    if (!o1.isStart) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
        int count = 0;
        for (Node n : nodes) {
            if (n.isStart) {
                if (count == 0 && isAvailable == true && arr[0] != n.pos) {
                    arr[1] = n.pos;
                    result.add(new Interval(arr[0], arr[1]));
                    arr = new int[2];
                }
                isAvailable = false;
                count++;
            } else {
                count--;
                if (count == 0) {
                    isAvailable = true;
                    arr[0] = n.pos;
                }
            }
        }
        return result;
    }

    private static class Node {
        int pos;
        boolean isStart;

        public Node(int pos, boolean isStart) {
            this.pos = pos;
            this.isStart = isStart;
        }
    }

    public static void main(String[] args) {
        // [[1, 3], [6, 7]], [[2, 4]], [[2, 3], [9, 12]]
        Interval i1 = new Interval(1, 3);
        Interval i2 = new Interval(6, 7);
        Interval i3 = new Interval(2, 4);
        Interval i4 = new Interval(2, 3);
        Interval i5 = new Interval(9, 12);
        List<Interval> l1 = new ArrayList<>(), l2 = new ArrayList<>(), l3 = new ArrayList<>();
        l1.add(i1);
        l1.add(i2);
        l2.add(i3);
        l2.add(i4);
        l3.add(i5);
        List<List<Interval>> intervals = new ArrayList<>();
        intervals.add(l1);
        intervals.add(l2);
        intervals.add(l3);
    }
}
