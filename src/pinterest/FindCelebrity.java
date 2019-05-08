package pinterest;

public class FindCelebrity {
    public int findCelebrity(int n) {
        int l = 0, r = n - 1;
        while (l < r) {
            if (knows(l, r)) {
                l++;
            } else {
                r--;
            }
        }
        for (int i = 0; i < n; i++) {
            if (i != r) {
                if (knows(r, i) | !knows(i, r)) {
                    return -1;
                }
            }
        }
        return r;
    }

    private boolean knows(int l, int r) {
        return true;
    }
}
