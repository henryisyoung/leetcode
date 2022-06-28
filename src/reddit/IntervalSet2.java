package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntervalSet2 {
    static class Interval {
        int start, end;
        public Interval(int start, int end) {
            this.end = end;
            this.start = start;
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    List<Interval> list;
    public IntervalSet2() {
        this.list = new ArrayList<>();
    }

    public void insert(Interval interval) {
        List<Interval> tmp = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            Interval cur = list.get(i);
            if (cur.end < interval.start) {
                index++;
                tmp.add(cur);
            } else if (cur.start > interval.end) {
                tmp.add(cur);
            } else {
                interval.start = Math.min(interval.start, cur.start);
                interval.end = Math.max(interval.end, cur.end);
            }
        }
        tmp.add(index, interval);
        list = tmp;
    }

    public boolean find(int val) {
        int l = 0, r = list.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            Interval interval = list.get(mid);
            if (interval.start <= val && interval.end >= val) {
                return true;
            } else if (interval.end <= val) {
                l = mid;
            } else {
                r = mid;
            }
        }
        Interval left = list.get(l), right = list.get(r);
        return left.start <= val && left.end >= val || right.start <= val && right.end >= val;
    }

    public void merge() {
        Interval prev = list.get(0);
        List<Interval> tmp = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            Interval cur = list.get(i);
            if (cur.start > prev.end) {
                tmp.add(prev);
                prev = cur;
            } else {
                prev.end = Math.max(prev.end, cur.end);
            }
        }
        tmp.add(prev);
        list = tmp;
    }

    public static void main(String[] args) {
        IntervalSet2 set = new IntervalSet2();
        List<Interval> intervals = Arrays.asList(
          new Interval(3,3),
          new Interval(4,7),
          new Interval(11,17),
          new Interval(21,117),
          new Interval(2,4)
        );
        for (Interval interval : intervals) {
            set.insert(interval);
        }
        System.out.println(set.list);
        for (int i = 1; i <= 20; i++) {
            System.out.println("i : " + i + " is " + set.find(i));
        }

    }
}
