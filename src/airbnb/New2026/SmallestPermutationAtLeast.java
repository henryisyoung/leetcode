package airbnb.New2026;
/*
Smallest Permutation >= Lower Bound.

Given a non-negative integer `n` and a lower bound `lb`, return the
smallest integer that can be obtained by permuting the digits of `n`
and that is greater than or equal to `lb`.  Leading zeros in the
permutation are allowed and simply collapse the value (e.g. "0349"
is 349).

Examples
  n = 4139, lb = 200    -> 1349       (smallest perm; all perms are >= 200)
  n = 4039, lb = 400    -> 439        ("0439" = 439 >= 400)
  n = 4039, lb = 9999   -> -1         (max perm is 9430)
  n = 9,    lb = 1      -> 9
  n = 0,    lb = 0      -> 0
  n = 4039, lb = -5     -> 349        (any perm >= -5; pick smallest)

Output convention
  -1 if no permutation of n's digits is >= lb.
  Otherwise the permuted value, interpreted as a base-10 integer
  (leading zeros do not count as significant).

Constraints
  0 <= n  <= Long.MAX_VALUE (up to ~19 digits is fine)
  lb is any long (negative allowed; falls through to "smallest perm")
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
Algorithm: digit DP with a "tight" flag over the digits of lb.

  Let d  = number of digits in n (count[0..9] is its digit multiset).
  Let lb_digits = decimal digits of lb (without leading zeros).

  Three cases on lengths:

    1. d < |lb_digits|
       Any d-digit permutation has at most 10^d - 1 < lb.  Impossible.

    2. d == |lb_digits|
       Compare the d-char permutation string to lb_digits[] directly
       (same length -> lex compare matches numeric compare).

    3. d >  |lb_digits|
       Permutation length is d, but if it starts with leading zeros
       its NUMERIC value can be < 10^d.  Trick: pad lb to width d
       with leading zeros and compare the d-char strings lex.  Same
       length => lex == numeric.  e.g. d=4, lb=200 -> "0200", and
       any d-perm >= "0200" lex is also >= 200 numeric (and vice versa).

  With lb padded to width d, the problem reduces to "smallest d-char
  permutation of count[] that is >= lb_d (lex)".

  Recursion build(pos, tight):
    if pos == d: success.
    for dgt = (tight ? lb_d[pos] : 0) .. 9:
        if count[dgt] == 0: skip.
        take dgt; newTight = tight && dgt == lb_d[pos]
        recurse; if success return.
        untake.
    return failure.

  When `tight == false`, the smallest available digit always works
  (just append remaining digits ascending), so the loop is dominated
  by the tight-chain decisions.

  Complexity
    d = number of digits in n.  Depth is d, branching is at most 10
    per level, but the "loose" subtree is one greedy pick, so total
    work is O(d * 10) digit decisions.  Plus O(d) for parsing and
    O(d) for building the result string.
*/
public class SmallestPermutationAtLeast {

    /** Returns the smallest permutation value >= lb, or -1 if none exists. */
    public long smallestAtLeast(long n, long lb) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative: " + n);

        int[] count = digitCounts(n);
        int d = sum(count);

        // lb <= 0: any non-negative permutation qualifies; pick the smallest one.
        if (lb <= 0) return smallestPerm(count, d);

        int[] lbDigits = digitArray(lb);
        if (lbDigits.length > d) return -1;       // case 1

        // Pad lb_digits to length d with leading zeros (case 3 unifies with case 2).
        int[] lbPadded = new int[d];
        int offset = d - lbDigits.length;
        for (int i = 0; i < lbDigits.length; i++) lbPadded[offset + i] = lbDigits[i];

        char[] buf = new char[d];
        if (!build(buf, 0, d, count, lbPadded, true)) return -1;
        return Long.parseLong(new String(buf));   // leading zeros collapse on parse
    }

    /** Smallest permutation of `count[]` (treats leading-zero result as a shorter value). */
    private static long smallestPerm(int[] count, int d) {
        if (d == 0) return 0;
        char[] buf = new char[d];
        int p = 0;
        for (int dgt = 0; dgt <= 9; dgt++) {
            for (int k = 0; k < count[dgt]; k++) buf[p++] = (char) ('0' + dgt);
        }
        return Long.parseLong(new String(buf));
    }

    /**
     * Construct, in `buf`, the smallest d-char permutation of `count[]`
     * that is lex >= `lb` (also length d).  Returns true on success.
     */
    private static boolean build(char[] buf, int pos, int d, int[] count, int[] lb, boolean tight) {
        if (pos == d) return true;
        int minDgt = tight ? lb[pos] : 0;
        for (int dgt = minDgt; dgt <= 9; dgt++) {
            if (count[dgt] == 0) continue;
            count[dgt]--;
            buf[pos] = (char) ('0' + dgt);
            boolean newTight = tight && (dgt == lb[pos]);
            if (build(buf, pos + 1, d, count, lb, newTight)) return true;
            count[dgt]++;
        }
        return false;
    }

    private static int[] digitCounts(long n) {
        int[] c = new int[10];
        if (n == 0) { c[0] = 1; return c; }
        while (n > 0) { c[(int) (n % 10)]++; n /= 10; }
        return c;
    }

    private static int[] digitArray(long x) {
        if (x == 0) return new int[]{0};
        // Build little-endian then reverse.
        int[] tmp = new int[20];
        int len = 0;
        while (x > 0) { tmp[len++] = (int) (x % 10); x /= 10; }
        int[] out = new int[len];
        for (int i = 0; i < len; i++) out[i] = tmp[len - 1 - i];
        return out;
    }

    private static int sum(int[] a) {
        int s = 0;
        for (int v : a) s += v;
        return s;
    }

    /* --------------------------- O(d!) brute force for tests --------------------------- */

    /** Enumerate all distinct permutations of the digit multiset and pick the smallest >= lb. */
    long smallestAtLeastBrute(long n, long lb) {
        if (n < 0) throw new IllegalArgumentException();
        int[] count = digitCounts(n);
        int d = sum(count);
        char[] buf = new char[d];
        Set<String> seen = new HashSet<>();
        List<Long> all = new ArrayList<>();
        permute(buf, 0, d, count, all, seen);
        long best = -1;
        for (long v : all) {
            if (v >= lb && (best == -1 || v < best)) best = v;
        }
        return best;
    }

    private static void permute(char[] buf, int pos, int d, int[] count,
                                List<Long> out, Set<String> seen) {
        if (pos == d) {
            String s = new String(buf);
            if (seen.add(s)) out.add(Long.parseLong(s));
            return;
        }
        for (int dgt = 0; dgt <= 9; dgt++) {
            if (count[dgt] == 0) continue;
            count[dgt]--;
            buf[pos] = (char) ('0' + dgt);
            permute(buf, pos + 1, d, count, out, seen);
            count[dgt]++;
        }
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

    /** Stdin: "n lb" on one line or on two lines. */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String first = br.readLine().trim();
        long n, lb;
        String[] tok = first.split("\\s+");
        if (tok.length >= 2) {
            n  = Long.parseLong(tok[0]);
            lb = Long.parseLong(tok[1]);
        } else {
            n  = Long.parseLong(tok[0]);
            lb = Long.parseLong(br.readLine().trim());
        }
        System.out.println(new SmallestPermutationAtLeast().smallestAtLeast(n, lb));
    }

    private static void runDemos() {
        SmallestPermutationAtLeast solver = new SmallestPermutationAtLeast();

        // ---- Spec example + variations ----
        check(solver, 4139, 200,  1349);    // every perm of {1,3,4,9} >= 200
        check(solver, 4039, 400,  439);     // "0439" = 439
        check(solver, 4039, 9999, -1);      // max perm 9430 < 9999
        check(solver, 9,    1,    9);
        check(solver, 0,    0,    0);
        check(solver, 4039, -5,   349);     // negative LB: smallest perm wins
        check(solver, 100,  0,    1);       // "001" = 1
        check(solver, 100,  10,   10);      // "010" = 10
        check(solver, 100,  100,  100);     // exact match
        check(solver, 100,  101,  -1);      // perms are 1, 10, 100; none >= 101
        check(solver, 12,   13,   21);      // perms 12 and 21
        check(solver, 1999, 2000, 9199);    // wait — let's compute brute-force first

        // ---- Random fuzz against brute force ----
        Random rnd = new Random(2026);
        int fails = 0, trials = 500;
        for (int t = 0; t < trials; t++) {
            // Keep digit count small to make brute tractable.
            int d = 1 + rnd.nextInt(6);                 // 1..6 digits
            long n = 0;
            for (int i = 0; i < d; i++) n = n * 10 + rnd.nextInt(10);
            // LB roughly in the perm value range; sometimes huge so -1 is hit.
            long lb = (long) rnd.nextInt(2_000_000) - 100_000;
            long fast  = solver.smallestAtLeast(n, lb);
            long brute = solver.smallestAtLeastBrute(n, lb);
            if (fast != brute) {
                fails++;
                System.out.println("MISMATCH n=" + n + " lb=" + lb
                        + " fast=" + fast + " brute=" + brute);
            }
        }
        System.out.println("Random cross-check: " + (trials - fails) + "/" + trials + " ok");

        // ---- Stress: ~18-digit n ----
        long bigN = 9_876_543_210_987_654L;
        long t0 = System.nanoTime();
        long ans = solver.smallestAtLeast(bigN, 5_000_000_000_000_000L);
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=" + bigN + " lb=5e15: ans=" + ans + " in " + us + " us");
    }

    private static void check(SmallestPermutationAtLeast solver, long n, long lb, long expected) {
        long got = solver.smallestAtLeast(n, lb);
        long brute = solver.smallestAtLeastBrute(n, lb);
        boolean ok = got == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + n + " lb=" + lb
                + " expected=" + expected + " fast=" + got + " brute=" + brute);
    }
}
