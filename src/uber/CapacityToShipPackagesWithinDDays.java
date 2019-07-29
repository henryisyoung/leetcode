package uber;

public class CapacityToShipPackagesWithinDDays {
    public int shipWithinDays(int[] weights, int D) {
        if (weights == null || weights.length == 0) return 0;
        int sum = 0;
        for (int i : weights) {
            sum += i;
        }
        int left = 1, right = sum;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (isValidCap(weights, D, mid)) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return isValidCap(weights, D, left) ? left : right;
    }

    private boolean isValidCap(int[] weights, int d, int cap) {
        int days = 0, curW = 0;
        for (int w : weights) {
            if (w > cap) return false;
            if (w + curW > cap) {
                curW = w;
                days++;
            } else {
                curW += w;
            }
            if (days >= d) return false;
        }
        return days < d;
    }
}
