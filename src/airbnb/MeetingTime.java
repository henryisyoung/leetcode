package airbnb;

import leetcode.dataStructrue.segmentTree.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingTime {
    class Point implements Comparable<Point> {
        int time;
        boolean isStart;

        Point(int time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }

        @Override
        public int compareTo(Point that) {
            if (this.time != that.time || this.isStart == that.isStart) {
                return this.time - that.time;
            }
                else {
                return this.isStart ? -1 : 1;
            }
        }
    }

    public List<Interval> getAvailableIntervals(List<List<Interval>> intervals, int k) {
        List<Interval> res = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        for (List<Interval> intervalList : intervals) {
            for (Interval interval : intervalList) {
                points.add(new Point(interval.start, true));
                points.add(new Point(interval.end, false));
            }
        }
        Collections.sort(points);
        int count = 0;
        Integer availableStart = null;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (point.isStart) {
                count++;
                if (availableStart == null && i == 0 && count <= intervals.size() - k) {
                    availableStart = point.time;
                } else if (availableStart != null && count == intervals.size() - k + 1) {
                    res.add(new Interval(availableStart, point.time));
                    availableStart = null;
                }
            } else {
                count--;
                if (count == intervals.size() - k && i < points.size() - 1) {
                    availableStart = point.time;
                } else if (availableStart != null && i == points.size() - 1 && count <= intervals.size() - k) {
                    res.add(new Interval(availableStart, point.time));
                    availableStart = null;
                }
            }
        }
        return res;
    }

    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
        if (schedule == null || schedule.size() == 0) {
            return null;
        }
        List<Node> nodes = new ArrayList<>();
        List<Interval> result = new ArrayList<>();
        for (List<Interval> list : schedule) {
            for (Interval interval : list) {
                int start = interval.start, end = interval.end;
                Node s = new Node(start, true);
                Node e = new Node(end, false);
                nodes.add(s);
                nodes.add(e);
            }
        }
        Collections.sort(nodes, new Comparator<Node>() {
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

        int count = 0;
        boolean isEmpty = false;
        int[] interval = new int[2];
        for (Node node : nodes) {
            if (node.isStart) {
                if (isEmpty && count == 0 && node.pos != interval[0]) {
                    interval[1] = node.pos;
                    result.add(new Interval(interval[0], interval[1]));
                    interval = new int[2];
                }
                count++;
                isEmpty = false;
            } else {
                count--;
                if (count == 0) {
                    isEmpty = true;
                    interval[0] = node.pos;
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

        @Override
        public String toString() {
            String is = isStart ? "Start" : "End";
            return "pos : " + pos + ", " + is;
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
