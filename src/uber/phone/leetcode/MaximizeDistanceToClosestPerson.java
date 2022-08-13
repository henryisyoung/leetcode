package uber.phone.leetcode;

import java.util.TreeSet;

public class MaximizeDistanceToClosestPerson {
    public int maxDistToClosest(int[] seats) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] == 1) {
                set.add(i);
            }
        }
        int n = seats.length;
        int max = 0;

        for (int i = 0; i < n; i++) {
            if (seats[i] == 0) {
                Integer floor = set.floor(i);
                Integer ceil = set.ceiling(i);
                int left = floor == null ? Integer.MAX_VALUE : i - floor;
                int right = ceil == null ? Integer.MAX_VALUE : ceil - i;
                max = Math.max(max, Math.min(left, right));
            }
        }
        return max;
    }
    public int maxDistToClosest2(int[] seats) {
        int n = seats.length;
        int max = 0;
        int left = -1, right = 0;

        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                left = i;
            } else {
                while (right < n && (seats[right] == 0 || right <= left)) {
                    right++;
                }
                int leftDist = left == -1 ? Integer.MAX_VALUE : i - left;
                int rightDist = right == n ? Integer.MAX_VALUE : right - i;
                max = Math.max(max, Math.min(leftDist, rightDist));
            }
        }
        return max;
    }
}
