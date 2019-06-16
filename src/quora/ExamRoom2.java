package quora;

import java.util.TreeSet;

public class ExamRoom2 {
    TreeSet<Integer> seats;
    int n;
    public ExamRoom2(int N) {
        this.seats = new TreeSet<>();
        this.n = N;
    }

    public int seat() {
        int pos = 0, max = 0;
        int left = 0;
        for (int i : seats) {
            if (left == 0) {
                if (max < i - left) {
                    max = i - left;
                    pos = 0;
                }
            } else {
                if (max < (i - left + 1) / 2) {
                    max = (i - left + 1) / 2;
                    pos = left + max - 1;
                }
            }
            left = i + 1;
        }
        if (left > 0 && max < n - left) {
            pos = n - 1;
        }
        seats.add(pos);
        return pos;
    }

    public void leave(int p) {
        seats.remove(p);
    }

    public static void main(String[] args) {
        ExamRoom2 examRoom = new ExamRoom2(10);
        for (int i = 0; i < 4; i++) {
            System.out.println(examRoom.seat());
        }
        examRoom.leave(4);
        System.out.println(examRoom.seat());
    }
}
