package netflix;

/*
LC 485. Length of the longest run of consecutive 1s in a 0/1 array.

Invariant: after processing nums[i], `cur` = length of the run of 1s
ending exactly at i (0 if nums[i] == 0); `best` = max `cur` so far.

Followup (LC 1004): "at most K flips" → sliding window keeping the
window's zero-count <= K, expand right, shrink left when it overflows.
*/
public class MaxConsecutiveOnes {
    public int findMaxConsecutiveOnes(int[] nums) {
        int best = 0, cur = 0;
        for (int x : nums) {
            cur = x == 1 ? cur + 1 : 0;
            best = Math.max(best, cur);
        }
        return best;
    }

    public int findMaxConsecutiveOnes2(int[] nums) {
        int max = 0;
        int count = 0;

        for (int i : nums) {
            count = i == 1 ? count + 1 : 0;
            max = Math.max(max, count);
        }
        return max;
    }
}