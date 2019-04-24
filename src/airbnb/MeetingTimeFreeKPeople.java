package airbnb;

import leetcode.dataStructrue.segmentTree.Interval;
import org.omg.CORBA.INTERNAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingTimeFreeKPeople {
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
        int count = 0, n = intervals.size();
        Collections.sort(points);
        Integer availableStart = null;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (point.isStart) {
                count++;
                if (availableStart == null && i == 0 && n - count >= k) {
                    availableStart = point.time;
                } else if (availableStart != null && k == n - count + 1) {
                    res.add(new Interval(availableStart, point.time));
                    availableStart = null;
                }
            } else {
                count--;
                if (k == n - count && i < points.size() - 1) {
                    availableStart = point.time;
                } else if (availableStart != null && i == points.size() - 1 && k <= n - count && availableStart != point.time) {
                    res.add(new Interval(availableStart, point.time));
                    availableStart = null;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        // [[1, 3], [6, 7]], [[2, 4]], [[2, 3], [8, 12]]
        Interval i1 = new Interval(1, 3);
        Interval i2 = new Interval(6, 7);
        Interval i3 = new Interval(2, 4);
        Interval i4 = new Interval(2, 3);
        Interval i5 = new Interval(8, 12);
        Interval i6 = new Interval(9, 12);
        Interval i7 = new Interval(10, 13);
        List<Interval> l1 = new ArrayList<>(), l2 = new ArrayList<>(), l3 = new ArrayList<>();
        l1.add(i1);
        l1.add(i2);
        l1.add(i6);
        l2.add(i3);
        l2.add(i4);
        l2.add(i7);
        l3.add(i5);
        List<List<Interval>> intervals = new ArrayList<>();
        intervals.add(l1);
        intervals.add(l2);
        intervals.add(l3);
        MeetingTimeFreeKPeople solver = new MeetingTimeFreeKPeople();
        List<Interval> result = solver.getAvailableIntervals(intervals, 2);
        System.out.println(result.toString());
    }
}
