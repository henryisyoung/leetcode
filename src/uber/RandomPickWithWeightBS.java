package uber;

import java.util.*;

public class RandomPickWithWeightBS {
    private int max;
    private Random rd;
    private int[] prevSum;

    public RandomPickWithWeightBS(int[] w) {
        this.prevSum = new int[w.length];
        int sum = 0;
        for(int i = 0; i < w.length; i++) {
            prevSum[i] = sum;
            sum += w[i];
        }

        this.rd = new Random();
        this.max = sum;
    }

    public int pickIndex() {
        int t = rd.nextInt(max);
        int lo = 0, hi = prevSum.length - 1;
        while(lo < hi) {
            int mid = lo + (hi - lo) / 2 + 1;
            if(prevSum[mid] == t) return mid;

            if(prevSum[mid] > t) {
                hi = mid - 1;
            } else {
                lo = mid;
            }
        }
        return lo;
    }
}
