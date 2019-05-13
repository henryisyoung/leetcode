package quora;

public class MaximizeDistanceToClosestPerson {
    public int maxDistToClosest(int[] seats) {
        int max = 0;
        if (seats == null || seats.length == 0) {
            return max;
        }
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] == 0) {
                int dis = Integer.MAX_VALUE;
                for (int j = 0; j < seats.length; j++) {
                    if (seats[j] == 1) {
                        dis = Math.min(Math.abs(i - j), dis);
                    }
                }
                max = Math.max(dis, max);
            }
        }
        return max;
    }

    public int maxDistToClosestDP(int[] seats) {
        if (seats == null || seats.length == 0) {
            return 0;
        }
        int n = seats.length;
        int[] left = new int[n], right = new int[n];

        for (int i = 1; i < n; i++) {
            if (seats[i] == 1) {
                continue;
            }
            left[i] = left[i - 1] + 1;
        }
        left[0] = Integer.MAX_VALUE;
        for (int i = n - 2; i >= 0; i--) {
            if (seats[i] == 1) {
                continue;
            }
            right[i] = right[i + 1] + 1;
        }
        right[n - 1] = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, Math.min(left[i], right[i]));
        }
        return max;
    }
    public int maxDistToClosestTwoPointer(int[] seats) {
        if (seats == null || seats.length == 0) {
            return 0;
        }
        int n = seats.length, max = 0;
        int left = -1, right = 0;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                left = i;
            } else {
                while (right < n && (seats[right] == 0 || right < i)) {
                    right++;
                }
                int leftDis = left == -1 ? Integer.MAX_VALUE : i - left;
                int rightDis = right == n ? Integer.MAX_VALUE : right - i;
                max = Math.max(max, Math.min(leftDis, rightDis));
            }
        }
        return max;
    }
}
