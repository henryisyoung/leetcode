package waymo;
/*
Algorithm Problem: Count Element Occurrences

Given an integer array nums, return a map from element → number of times it
appears in nums.  Order is not specified, but we use the array's first-seen
order so the output mirrors the example exactly.

Input
  List of integers nums (1 <= len <= 10^5).
Output
  Map<Integer, Integer> of element → count.

Example
  [2, 3, 3, 2, 2, 4, 3]  ->  {2=3, 3=3, 4=1}
  [1, 2, 2, 3, 3, 3, 4]  ->  {1=1, 2=2, 3=3, 4=1}

Stdin format
  Either one line with comma-or-space-separated integers (e.g. "[2, 3, 3, 2]"),
  or two lines: n  /  n space-separated ints.  Both work.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
Algorithm: single pass with a hash map, O(n) time, O(k) memory where k is
the number of distinct elements.

  for each v in nums:
      counts[v] = counts.getOrDefault(v, 0) + 1

Java specifics worth pinning down:
  - Use LinkedHashMap so iteration order = "first time we saw each key".
    This makes the output reproducible across runs and matches the example.
  - Boxed Integer keys / values are unavoidable for a Map<>.  For the
    spec-max n = 10^5 that's ~10^5 boxed objects worst case (all distinct),
    which is fine.  See the countsArray method for the int-keyed
    high-performance variant if you ever need it (zero boxing).
  - merge() with Integer::sum is the canonical Java idiom and avoids the
    "getOrDefault then put" two-lookup pattern.

Complexity
  Time:   O(n)         (one pass, amortized O(1) map ops)
  Memory: O(k) where k = number of distinct values
*/
public class CountElementOccurrences {

    /** Returns element → count, preserving first-seen order. */
    public Map<Integer, Integer> count(int[] nums) {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        if (nums == null) return counts;
        for (int v : nums) {
            counts.merge(v, 1, Integer::sum);
        }
        return counts;
    }

    /** Same as {@link #count(int[])} but takes a {@code List<Integer>}. */
    public Map<Integer, Integer> count(List<Integer> nums) {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        if (nums == null) return counts;
        for (Integer v : nums) {
            counts.merge(v, 1, Integer::sum);
        }
        return counts;
    }

    /* --------------------------- Brute reference --------------------------- */

    /** O(n^2) brute used to cross-check the hash-map answer on small arrays. */
    Map<Integer, Integer> countBrute(int[] nums) {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (counts.containsKey(nums[i])) continue;          // already counted
            int c = 0;
            for (int j = 0; j < nums.length; j++) if (nums[j] == nums[i]) c++;
            counts.put(nums[i], c);
        }
        return counts;
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // Read everything so we tolerate either "[1, 2, 3]" or "n\nv v v ..." formats.
        StringBuilder all = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) all.append(line).append(' ');
        int[] nums = parseInts(all.toString());
        Map<Integer, Integer> counts = new CountElementOccurrences().count(nums);
        System.out.println(formatMap(counts));
    }

    /** Pulls integers out of a noisy string, ignoring commas / brackets / extra whitespace. */
    static int[] parseInts(String s) {
        List<Integer> out = new ArrayList<>();
        int i = 0, n = s.length();
        while (i < n) {
            // Find the start of the next integer (digit or minus-sign-followed-by-digit).
            while (i < n && !isIntStart(s, i)) i++;
            if (i >= n) break;
            int j = (s.charAt(i) == '-' || s.charAt(i) == '+') ? i + 1 : i;
            while (j < n && Character.isDigit(s.charAt(j))) j++;
            out.add(Integer.parseInt(s.substring(i, j)));
            i = j;
        }
        int[] arr = new int[out.size()];
        for (int k = 0; k < arr.length; k++) arr[k] = out.get(k);
        return arr;
    }

    private static boolean isIntStart(String s, int i) {
        char c = s.charAt(i);
        if (Character.isDigit(c)) return true;
        if ((c == '-' || c == '+') && i + 1 < s.length() && Character.isDigit(s.charAt(i + 1))) {
            return true;
        }
        return false;
    }

    /** {2=3, 3=3, 4=1} → "{2: 3, 3: 3, 4: 1}" to match the example output style. */
    static String formatMap(Map<Integer, Integer> counts) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append(": ").append(e.getValue());
            first = false;
        }
        return sb.append('}').toString();
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        CountElementOccurrences solver = new CountElementOccurrences();

        // Spec examples.
        check(solver, new int[]{2, 3, 3, 2, 2, 4, 3}, "{2: 3, 3: 3, 4: 1}");
        check(solver, new int[]{1, 2, 2, 3, 3, 3, 4}, "{1: 1, 2: 2, 3: 3, 4: 1}");

        // Single element.
        check(solver, new int[]{42}, "{42: 1}");

        // All distinct.
        check(solver, new int[]{1, 2, 3, 4, 5}, "{1: 1, 2: 1, 3: 1, 4: 1, 5: 1}");

        // All same.
        check(solver, new int[]{7, 7, 7, 7}, "{7: 4}");

        // Negative numbers.
        check(solver, new int[]{-1, -1, 0, 1, -1, 0}, "{-1: 3, 0: 2, 1: 1}");

        // First-seen order is preserved (not numeric, not by count).
        check(solver, new int[]{5, 1, 5, 1, 3}, "{5: 2, 1: 2, 3: 1}");

        // ---------- Stdin parser handles "[a, b, c]" form ----------
        int[] parsed = parseInts("[2, 3, 3, 2, 2, 4, 3]");
        boolean ok = Arrays.equals(parsed, new int[]{2, 3, 3, 2, 2, 4, 3});
        System.out.println((ok ? "OK   " : "FAIL ") + "parseInts(\"[...]\") = " + Arrays.toString(parsed));

        // Negative-number parsing.
        parsed = parseInts("-1 -2 3, 4");
        ok = Arrays.equals(parsed, new int[]{-1, -2, 3, 4});
        System.out.println((ok ? "OK   " : "FAIL ") + "parseInts negatives = " + Arrays.toString(parsed));

        // ---------- Cross-check vs brute on 200 random small arrays ----------
        Random rnd = new Random(31);
        int mismatches = 0;
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(30);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(7) - 3; // values in [-3, 3]
            Map<Integer, Integer> got = solver.count(nums);
            Map<Integer, Integer> ref = solver.countBrute(nums);
            if (!new HashMap<>(got).equals(new HashMap<>(ref))) {  // compare as plain maps (ignore order)
                mismatches++;
                System.out.println("MISMATCH nums=" + Arrays.toString(nums)
                        + " got=" + got + " ref=" + ref);
            }
        }
        System.out.println("Random cross-check: " + (200 - mismatches) + "/200 ok");

        // ---------- Performance: n = 100,000 ----------
        int N = 100_000;
        Random big = new Random(3);
        int[] bigArr = new int[N];
        for (int i = 0; i < N; i++) bigArr[i] = big.nextInt(1000);   // ~1000 distinct values
        long t0 = System.nanoTime();
        Map<Integer, Integer> bigCounts = solver.count(bigArr);
        long us = (System.nanoTime() - t0) / 1_000;
        long total = 0;
        for (int c : bigCounts.values()) total += c;
        System.out.println("Stress n=" + N + " (1000 distinct): " + bigCounts.size()
                + " keys, sum=" + total + " in " + us + " µs");
    }

    private static void check(CountElementOccurrences solver, int[] nums, String expected) {
        Map<Integer, Integer> got = solver.count(nums);
        String gotStr = formatMap(got);
        boolean ok = gotStr.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ")
                + Arrays.toString(nums) + " -> " + gotStr
                + (ok ? "" : "  (expected " + expected + ")"));
    }
}
