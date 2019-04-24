package airbnb;

import leetcode.dataStructrue.segmentTree.Interval;

import java.util.*;

public class MeetingTimeArray {

    public int[][] employeeFreeTime(int[][][] schedule) {
        List<Interval> result = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        for (int[][] list : schedule) {
            for (int[] interval : list) {
                Node n1 = new Node(interval[0], true), n2 = new Node(interval[1], false);
                nodes.add(n1);
                nodes.add(n2);
            }
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

        boolean available = false;
        int[] spot = new int[2];
        int count = 0;

        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n.isStart) {
                if (available && count == 0 && spot[0] != n.pos) {
                    result.add(new Interval(spot[0], n.pos));
                    spot = new int[2];
                }
                available = false;
                count++;
            } else {
                count--;
                if (count == 0) {
                    available = true;
                    spot[0] = n.pos;
                }
            }
        }
        int n = result.size();
        int[][] arr = new int[n][2];
        int t = 0;
        for (Interval interval : result) {
            arr[t][0] = interval.start;
            arr[t][1] = interval.end;
            t++;
        }
        return arr;
    }

    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
        List<Interval> result = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        for (List<Interval> list : schedule) {
            for (Interval interval : list) {
                Node n1 = new Node(interval.start, true), n2 = new Node(interval.end, false);
                nodes.add(n1);
                nodes.add(n2);
            }
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

        boolean available = false;
        int[] spot = new int[2];
        int count = 0;

        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n.isStart) {
                if (available && count == 0 && spot[0] != n.pos) {
                    result.add(new Interval(spot[0], n.pos));
                    spot = new int[2];
                }
                available = false;
                count++;
            } else {
                count--;
                if (count == 0) {
                    available = true;
                    spot[0] = n.pos;
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
        Interval i6 = new Interval(7, 9);
        List<Interval> l1 = new ArrayList<>(), l2 = new ArrayList<>(), l3 = new ArrayList<>();
        l1.add(i1);
        l1.add(i2);
        l2.add(i3);
        l2.add(i4);
        l3.add(i5);
        l3.add(i6);
        List<List<Interval>> intervals = new ArrayList<>();
        intervals.add(l1);
        intervals.add(l2);
        intervals.add(l3);

        MeetingTimeArray solver = new MeetingTimeArray();
        List<Interval> result = solver.employeeFreeTime(intervals);
        System.out.println(result.toString());
    }
}
