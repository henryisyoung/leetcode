package facebook;

public class _KokoEatingBananas {
    public static int minEatingSpeed(int[] piles, int H) {
        int left = Integer.MAX_VALUE, right = Integer.MIN_VALUE;
        for (int i : piles) {
            right = Math.max(right, i);
            left = Math.min(left, i);
        }

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            int count = calTimes(mid, piles);
//            System.out.println("left " + left + " right " + right + " mid " + mid + " count " + count);
            if(H >= count) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (calTimes(left, piles) <= H) return left;
        return right;
    }

    private static int calTimes(int rate, int[] piles) {
        int count  = 0;
        for (int i : piles) {
            if(i % rate == 0) {
                count += i / rate;
            } else {
                count += i / rate + 1;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] piles = {3,6,7,11};
        int[] piles2 = {30,11,23,4,20};
        int H = 8;
        int H2 = 5;
        System.out.println(minEatingSpeed(piles, H));
        System.out.println(minEatingSpeed(piles2, H2));
    }
}
