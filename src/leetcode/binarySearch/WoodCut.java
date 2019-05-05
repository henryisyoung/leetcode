package leetcode.binarySearch;

public class WoodCut {
    public int woodCut(int[] L, int k) {

        int l = 1, res = 0;
        int r = 0;
        for (int item : L) {
            r = Math.max(r, item);
        }

        while (l <= r) {
            int mid = l + (r - l) / 2; // (l + r) / 2 may overflow
            if (count(L, mid) >= k) {
                res = mid;
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }

        return res;
    }

    private int count(int[] L, int len) {
        int sum = 0;
        for (int item : L) {
            sum += item / len;
        }
        return sum;
    }
}
