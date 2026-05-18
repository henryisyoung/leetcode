package airbnb.New2026;
/*
Minimum Eating Speed  (a.k.a. LC 875 "Koko Eating Bananas").

Alex piles candy into `numPiles` piles; the i-th pile has
`candyPiles[i]` candies. Alex sleeps for `numHours` hours. Each hour
you eat from EXACTLY ONE pile at rate c candies/hour (you can't carry
leftover capacity from a partially-finished pile into the next hour).

Find the smallest integer eating rate c such that ALL candies are
finished within `numHours` hours.

I/O
  Input : int[] candyPiles, int numHours
  Output: int (minimum c)

Constraints (typical for this family of problems)
  1 <= numPiles      <= 1e4
  1 <= candyPiles[i] <= 1e9
  numPiles <= numHours <= 1e9          (otherwise impossible — see below)

Example
  candyPiles = [4, 9, 11, 17], numHours = 8  -> 6
    at c=6: ceil(4/6)+ceil(9/6)+ceil(11/6)+ceil(17/6) = 1+2+2+3 = 8 ✓
    at c=5: 1+2+3+4 = 10 > 8 ✗ — so 6 is the threshold.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
Algorithm — binary search on the answer.

  Monotonic predicate
    f(c) = total hours required at rate c = sum_i ceil(pile_i / c).
    Larger c can only finish piles faster, so f(c) is non-increasing
    in c.  Therefore "f(c) <= H" is monotonic in c: false for small
    c, true past some threshold. Binary-search that threshold.

  Search range
    lo = 1                              (rate must be >= 1)
    hi = max(piles)                     (eating the largest pile in
                                         one hour is always fast
                                         enough; rates higher than
                                         that are wasted)
    Invariant: f(hi) = numPiles <= H, so hi always satisfies the
    predicate; lo eventually catches up to the boundary.

  ceil without overflow
    ceil(p / c) = (p + c - 1) / c        when (p + c - 1) doesn't
                                          overflow.
    Safer: 1 + (p - 1) / c when p > 0; 0 when p == 0.
    We use long arithmetic for the running sum (4e10 worst case fits
    in long but not int).

  Early exit
    If H < numPiles even rate = +∞ can't finish (one hour per pile
    minimum). Return -1 in that degenerate case; the caller can
    interpret it as "impossible".

Complexity
  Time:   O(n log(max pile))
  Memory: O(1)
*/
public class MinimumEatingSpeed {

    /** Returns the minimum c, or -1 if H < numPiles (impossible). */
    public int minimumEatingSpeed(int[] candyPiles, int numHours) {
        if (candyPiles == null || candyPiles.length == 0) {
            throw new IllegalArgumentException("candyPiles must be non-empty");
        }
        if (numHours < candyPiles.length) return -1;          // impossible: need >= 1 hr per pile

        int lo = 1, hi = 0;
        for (int p : candyPiles) {
            if (p < 0) throw new IllegalArgumentException("piles must be non-negative");
            if (p > hi) hi = p;
        }
        if (hi == 0) return 1;                                // all piles empty; any c >= 1 works

        // Standard "first index satisfying predicate" binary search.
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (hoursNeeded(candyPiles, mid) <= numHours) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    /** sum_i ceil(pile_i / c). c must be >= 1. */
    private static long hoursNeeded(int[] piles, int c) {
        long sum = 0;
        for (int p : piles) {
            if (p == 0) continue;
            sum += 1L + (p - 1) / c;                          // ceil without (p + c - 1) overflow
        }
        return sum;
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    /**
     * Stdin format:
     *   line 1: space-separated piles
     *   line 2: numHours
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer t = new StringTokenizer(br.readLine());
        int n = t.countTokens();
        int[] piles = new int[n];
        for (int i = 0; i < n; i++) piles[i] = Integer.parseInt(t.nextToken());
        int H = Integer.parseInt(br.readLine().trim());
        System.out.println(new MinimumEatingSpeed().minimumEatingSpeed(piles, H));
    }

    private static void runDemos() {
        MinimumEatingSpeed solver = new MinimumEatingSpeed();

        // ---- Spec example ----
        check("ex1 [4,9,11,17] H=8", solver.minimumEatingSpeed(new int[]{4, 9, 11, 17}, 8), 6);

        // ---- Classic LC 875 examples ----
        check("[3,6,7,11] H=8",   solver.minimumEatingSpeed(new int[]{3, 6, 7, 11}, 8), 4);
        check("[30,11,23,4,20] H=5",  solver.minimumEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5), 30);
        check("[30,11,23,4,20] H=6",  solver.minimumEatingSpeed(new int[]{30, 11, 23, 4, 20}, 6), 23);

        // ---- Boundary: H == numPiles (must eat the biggest in one hour) ----
        check("H == numPiles forces max pile",
                solver.minimumEatingSpeed(new int[]{3, 6, 7, 11}, 4), 11);

        // ---- Boundary: H very large -> rate 1 is enough iff sum(piles) <= H ----
        check("rate=1 sufficient",
                solver.minimumEatingSpeed(new int[]{1, 1, 1, 1}, 10), 1);

        // ---- Single pile ----
        check("single pile size 10, H=3", solver.minimumEatingSpeed(new int[]{10}, 3), 4); // ceil(10/4)=3
        check("single pile size 10, H=1", solver.minimumEatingSpeed(new int[]{10}, 1), 10);

        // ---- Impossible cases ----
        check("H < numPiles -> -1",
                solver.minimumEatingSpeed(new int[]{1, 1, 1}, 2), -1);

        // ---- Empty piles in the array (legal, just take 0 hours) ----
        check("all zero piles", solver.minimumEatingSpeed(new int[]{0, 0, 0}, 3), 1);
        // [0,4,0,9] H=4: c=4 -> 1 + ceil(9/4)=3 -> 4 hours ok.
        //                c=3 -> 2 + 3 = 5 > 4. So answer = 4.
        check("some zero piles", solver.minimumEatingSpeed(new int[]{0, 4, 0, 9}, 4), 4);

        // ---- Big-value sanity (pile = int max safe, with long sum) ----
        check("near-max pile",
                solver.minimumEatingSpeed(new int[]{1_000_000_000}, 2), 500_000_000);
        check("near-max pile, H=1",
                solver.minimumEatingSpeed(new int[]{1_000_000_000}, 1), 1_000_000_000);

        // ---- Stress: 1e4 piles, large H ----
        int n = 10_000;
        int[] big = new int[n];
        long sum = 0;
        for (int i = 0; i < n; i++) { big[i] = 1 + (i * 37) % 1_000_000_000; sum += big[i]; }
        int H = (int) Math.min(Integer.MAX_VALUE, sum);
        long t0 = System.nanoTime();
        int c = solver.minimumEatingSpeed(big, H);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress rate=1 fits when H>=sum", c, 1);
        // Tight H: each pile must be eaten in roughly one hour each.
        long t1 = System.nanoTime();
        int cTight = solver.minimumEatingSpeed(big, n);
        long ms2 = (System.nanoTime() - t1) / 1_000_000;
        int maxPile = 0; for (int p : big) if (p > maxPile) maxPile = p;
        check("stress H=n forces max pile rate", cTight, maxPile);
        System.out.println("Stress n=" + n + ": loose " + ms + " ms / tight " + ms2 + " ms");
    }

    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
