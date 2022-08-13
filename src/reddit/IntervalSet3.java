package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntervalSet3 {
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
    public IntervalSet3() {
        this.list = new ArrayList<>();
    }

    public void insert(Interval interval) {
        if (list.size() == 0) {
            list.add(interval);
            return;
        }
        int l = 0, r = list.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            Interval midInterval = list.get(mid);
            if (midInterval.start < interval.start) {
                l = mid;
            } else if (midInterval.start > interval.start) {
                r = mid;
            } else {
                if (midInterval.end < interval.end) {
                    l = mid;
                } else if (midInterval.end > interval.end) {
                    r = mid;
                } else {
                    list.add(mid, interval);
                    return;
                }
            }
        }
        Interval right = list.get(r);
        Interval left = list.get(l);
        if (right.start < interval.start) {
            list.add(r + 1, interval);
            return;
        }
        if (left.start < interval.start) {
            list.add(l + 1, interval);
            return;
        }
        if (left.start == interval.start) {
            if (left.end < interval.end) {
                list.add(l + 1, interval);
                return;
            }
        }
        list.add(l, interval);
    }

    public boolean find(int val) {
        if (list.size() == 0) {
            return false;
        }
        int l = 0, r = list.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            Interval midInterval = list.get(mid);
            if (midInterval.start > val) {
                r = mid;
            } else if (midInterval.start < val) {
                l = mid;
            } else {
                if (midInterval.end >= val) {
                    return true;
                }
                l = mid;
            }
        }
        Interval left = list.get(l);
        Interval right = list.get(r);

        if (left.start <= val && left.end >= val) {
            return true;
        }
        if (right.start <= val && right.end >= val) {
            return true;
        }
        return false;
    }

    public void merge() {
        if (list.size() == 0) {
            return;
        }
        Interval prev = list.get(0);
        List<Interval> copy = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            Interval cur = list.get(i);
            if (cur.start > prev.end) {
                copy.add(prev);
                prev = cur;
            } else {
                prev.end = Math.max(cur.end, prev.end);
            }
        }
        copy.add(prev);
        list = new ArrayList<>(copy);
    }

    public static void main(String[] args) {
        IntervalSet3 set = new IntervalSet3();
        List<Interval> intervals = Arrays.asList(
          new Interval(3,3),
          new Interval(2,2),
          new Interval(1,14),
          new Interval(1,7),
          new Interval(11,17),
          new Interval(2,4)
        );
        for (Interval interval : intervals) {
            set.insert(interval);
            System.out.println(set.list);
        }
//        System.out.println(set.find(20));
        for (int i = 1; i <= 20; i++) {
            System.out.println("i : " + i + " is " + set.find(i));
        }
        set.merge();
        System.out.println(set.list);
    }
}
