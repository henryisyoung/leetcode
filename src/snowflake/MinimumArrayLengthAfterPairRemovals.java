package snowflake;

import java.util.List;

public class MinimumArrayLengthAfterPairRemovals {
    // Key insight: since nums is sorted, the most frequent value occupies a
    // contiguous block straddling the middle. Let f = its count.
    //   - If 2*f <= n: pair nums[i] with nums[i + n/2] for i in [0, n/2).
    //                  Each such pair has strictly different values, so we can
    //                  remove everything (or all but 1 if n is odd).
    //   - If 2*f >  n: equal values can't pair with each other, and there are
    //                  only (n - f) non-dominant partners available, leaving
    //                  (2f - n) dominant elements stranded.
    public int minLengthAfterRemovals(List<Integer> nums) {
        int n = nums.size();
        int mid = nums.get(n / 2);

        int lo = lowerBound(nums, mid);
        int hi = upperBound(nums, mid);
        int maxFreq = hi - lo;

        if (2 * maxFreq > n) {
            return n - 2 * (n - maxFreq) ;
        } else {
            return n % 2;
        }
    }

    private int lowerBound(List<Integer> a, int target) {
        int l = 0, r = a.size();
        while (l < r) {
            int m = l + (r - l) / 2;
            if (a.get(m) < target) l = m + 1;
            else r = m;
        }
        return l;
    }

    private int upperBound(List<Integer> a, int target) {
        int l = 0, r = a.size();
        while (l < r) {
            int m = l + (r - l) / 2;
            if (a.get(m) <= target) l = m + 1;
            else r = m;
        }
        return l;
    }
}
