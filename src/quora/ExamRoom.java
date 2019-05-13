package quora;

public class ExamRoom {
    int[] seats;
    public ExamRoom(int N) {
        this.seats = new int[N];
    }

    public int seat() {
        int pos = 0, max = 0, n = seats.length;
        int prev = -1, next = 1;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                prev = i;
            }else {
                while (next < n && (seats[next] == 0 || next < i)) {
                    next++;
                }
                int left = prev == -1 ? Integer.MAX_VALUE : i - prev;
                int right = next == n ? Integer.MAX_VALUE : next - i;
                if (max < Math.min(left, right)) {
                    max = Math.min(left, right);
                    pos = i;
                }
            }
        }
        seats[pos] = 1;
        return pos;
    }

    public void leave(int p) {
        seats[p] = 0;
    }
}
