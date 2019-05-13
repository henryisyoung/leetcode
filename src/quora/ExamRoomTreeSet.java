package quora;

import java.util.TreeSet;

public class ExamRoomTreeSet {
    TreeSet<Integer> seats;
    int n;
    public ExamRoomTreeSet(int N) {
        this.seats = new TreeSet();
        this.n = N;
    }

    public int seat() {
       int start = 0, max = 0, pos = 0;
       for (int i : seats) {
           if (start == 0) {
               if (max < i - start) {
                   max = i - start;
                   pos = start;
               }
           } else {
               if (max < (i - start + 1) / 2) {
                   max = (i - start + 1) / 2;
                   pos = start + max - 1;
               }
           }
           start = i + 1;
       }
       if (start > 0 && max < n - start) {
           max = n - start;
           pos = n - 1;
       }
       seats.add(pos);
       return pos;
    }

    public void leave(int p) {
        seats.remove(p);
    }
}
