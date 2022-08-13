package uber.phone.leetcode;

public class ExamRoom {
    int[] seats;
    int n;
    int empty;
    public ExamRoom(int n) {
        this.seats = new int[n];
        this.n = n;
        this.empty = n;
    }

    public int seat() {
        if (empty == 0) {
            return -1;
        }
        int max = 0;
        empty--;
        int pos = -1;

        int left = -1, right = 0;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                left = i;
            } else {
                while (right < n && (seats[right] == 0 || right <= left)) {
                    right++;
                }
                int ld = left == -1 ? Integer.MAX_VALUE : i - left;
                int rd = right == n ? Integer.MAX_VALUE : right - i;
                if (Math.min(ld, rd) > max) {
                    pos = i;
                    max = Math.min(ld, rd);
                }
            }
        }
        seats[pos] = 1;
        return pos;
    }

    public void leave(int p) {
        if (p < 0 || p >= n) {
            return;
        }
        seats[p] = 0;
    }
}
