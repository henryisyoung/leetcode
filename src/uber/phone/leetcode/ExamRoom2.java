package uber.phone.leetcode;

import java.util.TreeSet;

public class ExamRoom2 {
    TreeSet<Integer> seats;
    int n ;
    public ExamRoom2(int n) {
        this.seats = new TreeSet<>();
        this.n = n;
    }

    public int seat() {
        int left = 0, max = 0, pos = 0;
        for (int seat : seats) {
            if (left == 0) {
                max = seat;
                pos = left;
            } else {
                int mid = left + (seat - left) / 2;
                int dist = Math.min(mid - left, seat - mid);
                if (dist > max) {
                    max = dist;
                    pos = mid;
                }
            }
            left = seat;
        }
        if (left > 0 && max < n - 1 - left) {
            pos = n - 1;
        }
        seats.add(pos);
        return pos;
    }

    public void leave(int p) {
        seats.remove(p);
    }
}
