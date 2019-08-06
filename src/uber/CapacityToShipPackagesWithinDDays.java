package uber;

public class CapacityToShipPackagesWithinDDays {
    public int shipWithinDays(int[] weights, int D) {
        if(weights == null || weights.length == 0) return 0;
        int left = 1, right = 0;
        for (int i : weights) {
            right += i;
        }
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (isValidW(mid, weights, D)) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return isValidW(left, weights, D) ? left : right;
    }

    private boolean isValidW(int w, int[] weights, int d) {
        int days = 0, cur = 0;
        for (int i : weights) {
            if (i > w) return false;
            if (cur + i > w) {
                days++;
                cur = i;
            } else {
                cur += i;
            }
            if (days >= d) return false;
        }
        return days < d;
    }
}
