package airbnb.New2026;
/*
Minimum Cost to Cover Desired Items (Bitmask DP).

You are given a list of bundles.  Each bundle contains some food items
(strings) and has a positive cost.  You also have a desired list `want`.
You may purchase any subset of bundles (each bundle at most once).  You
must cover every item in `want` (extras are fine).  Return the minimum
total cost, or -1 if impossible.

Input
  bundles[i] : items contained in bundle i
  cost[i]    : cost of bundle i
  want       : distinct desired items

Output
  Minimum total cost to cover all desired items, or -1.

Constraints
  1 <= |want|     <= 20
  1 <= |bundles|  <= 200
  1 <= cost[i]    <= 1e9     -> sums need long (200 * 1e9 = 2e11)

Example
  bundles = [["a","b","c"], ["d","e"], ["a","c"]]
  cost    = [5, 2, 1]
  want    = ["a","c","d"]   ->  buy bundles 1 and 2: 2 + 1 = 3
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
Algorithm: 0/1 set-cover DP over a bitmask of `want`.

  Encode each bundle's contribution as a |want|-bit mask: bit j is 1
  iff the bundle contains want[j].  Items NOT in `want` are ignored.

  Let dp[mask] = minimum total cost achievable s.t. the union of
  covered want-items equals exactly `mask` (covering more items
  outside `want` is fine and costs nothing extra in this model).

  Base: dp[0] = 0 (buy nothing), dp[*] = +inf.
  Transition (per bundle b with mask mb and cost cb):
       dp_new[mask | mb] = min(dp_new[mask | mb], dp_old[mask] + cb)

  Each bundle can be used at most once, so we use the standard 0/1
  knapsack pattern: maintain TWO arrays (prev / curr) per bundle.
  In-place updates would risk applying a bundle twice (`mask | mb`
  later read as a source still containing `mb`'s bits).

  Answer: dp[FULL] where FULL = (1 << k) - 1, or -1 if it stayed +inf.

  Why long, not int:
    cost[i] up to 1e9, |bundles| up to 200, worst-case total 2e11
    overflows int.  Use Long.MAX_VALUE / 2 as the "infinity" so an
    accidental "+ cb" can't wrap.

  Bundles that contribute no want-bits (mb == 0) are skipped — they
  can never lower the cost for any state.

Complexity
  Let k = |want|, B = |bundles|.
  Time:   O(B * 2^k) = up to 200 * 2^20 ≈ 2 * 10^8 cell updates.
  Memory: O(2^k) longs.
*/
public class MinimumCostToCoverDesiredItems {

    private static final long INF = Long.MAX_VALUE / 2;

    public long minCost(List<List<String>> bundles, long[] cost, List<String> want) {
        if (want == null || want.isEmpty()) return 0;        // nothing to cover
        if (bundles == null || cost == null || bundles.size() != cost.length) {
            throw new IllegalArgumentException("bundles/cost size mismatch");
        }
        int k = want.size();
        if (k > 30) throw new IllegalArgumentException("|want| > 30 won't fit in 32-bit mask");

        // Map each desired item to its bit index.  Treats `want` as a SET
        // (distinct per spec); duplicates would silently be deduped here.
        Map<String, Integer> idx = new HashMap<>();
        for (int j = 0; j < k; j++) idx.putIfAbsent(want.get(j), j);

        int FULL = (1 << k) - 1;
        long[] dp = new long[1 << k];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        long[] next = new long[1 << k];
        for (int b = 0; b < bundles.size(); b++) {
            int mb = 0;
            for (String item : bundles.get(b)) {
                Integer j = idx.get(item);
                if (j != null) mb |= 1 << j;
            }
            long cb = cost[b];
            if (mb == 0) continue;                              // bundle covers nothing in want

            System.arraycopy(dp, 0, next, 0, dp.length);        // "skip this bundle" branch
            for (int mask = 0; mask <= FULL; mask++) {
                if (dp[mask] >= INF) continue;
                int nm = mask | mb;
                long candidate = dp[mask] + cb;
                if (candidate < next[nm]) next[nm] = candidate;
            }
            // swap (avoids realloc)
            long[] tmp = dp; dp = next; next = tmp;
        }
        return dp[FULL] >= INF ? -1L : dp[FULL];
    }

    /* --------------------------- O(2^B) brute force for tests --------------------------- */

    /** Reference: enumerate every subset of bundles. Only safe for small B. */
    long minCostBrute(List<List<String>> bundles, long[] cost, List<String> want) {
        int B = bundles.size();
        long best = -1;
        for (int sub = 0; sub < (1 << B); sub++) {
            long total = 0;
            // Items covered by this subset.
            java.util.Set<String> covered = new java.util.HashSet<>();
            for (int i = 0; i < B; i++) {
                if ((sub & (1 << i)) != 0) {
                    total += cost[i];
                    covered.addAll(bundles.get(i));
                }
            }
            boolean ok = true;
            for (String w : want) if (!covered.contains(w)) { ok = false; break; }
            if (ok && (best == -1 || total < best)) best = total;
        }
        return best;
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
     * Stdin format (loose, matches the prompt):
     *   bundles=[[a,b,c],[d,e],[a,c]]
     *   cost=[5,2,1]
     *   want=[a,c,d]
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String bundlesLine = br.readLine();
        String costLine    = br.readLine();
        String wantLine    = br.readLine();

        List<List<String>> bundles = parseBundles(stripPrefix(bundlesLine, "bundles="));
        long[] cost                = parseCostArray(stripPrefix(costLine, "cost="));
        List<String> want          = parseFlatList(stripPrefix(wantLine, "want="));

        System.out.println(new MinimumCostToCoverDesiredItems().minCost(bundles, cost, want));
    }

    private static String stripPrefix(String line, String prefix) {
        return line.startsWith(prefix) ? line.substring(prefix.length()) : line;
    }

    private static List<String> parseFlatList(String s) {
        s = s.trim();
        if (s.startsWith("[")) s = s.substring(1);
        if (s.endsWith("]"))   s = s.substring(0, s.length() - 1);
        s = s.trim();
        if (s.isEmpty()) return new ArrayList<>();
        String[] parts = s.split("\\s*,\\s*");
        List<String> out = new ArrayList<>(parts.length);
        for (String p : parts) out.add(p);
        return out;
    }

    private static long[] parseCostArray(String s) {
        List<String> tokens = parseFlatList(s);
        long[] out = new long[tokens.size()];
        for (int i = 0; i < out.length; i++) out[i] = Long.parseLong(tokens.get(i).trim());
        return out;
    }

    /** Parse "[[a,b,c],[d,e],[a,c]]" into a list of lists. */
    private static List<List<String>> parseBundles(String s) {
        s = s.trim();
        if (s.startsWith("[")) s = s.substring(1);
        if (s.endsWith("]"))   s = s.substring(0, s.length() - 1);
        List<List<String>> out = new ArrayList<>();
        // Walk and split top-level [...] groups.
        int depth = 0, start = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '[') {
                if (depth == 0) start = i + 1;
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) out.add(parseFlatList(s.substring(start, i)));
            }
        }
        return out;
    }

    private static void runDemos() {
        MinimumCostToCoverDesiredItems solver = new MinimumCostToCoverDesiredItems();

        // Spec example.
        check(solver,
                Arrays.asList(Arrays.asList("a","b","c"), Arrays.asList("d","e"), Arrays.asList("a","c")),
                new long[]{5, 2, 1},
                Arrays.asList("a", "c", "d"),
                3);

        // Single bundle covers everything; comparison with two-bundle alternative.
        check(solver,
                Arrays.asList(Arrays.asList("a","b","c"), Arrays.asList("a"), Arrays.asList("b"), Arrays.asList("c")),
                new long[]{10, 1, 1, 1},
                Arrays.asList("a","b","c"),
                3);   // 1+1+1 < 10

        // Single bundle wins.
        check(solver,
                Arrays.asList(Arrays.asList("a","b","c"), Arrays.asList("a"), Arrays.asList("b"), Arrays.asList("c")),
                new long[]{2, 5, 5, 5},
                Arrays.asList("a","b","c"),
                2);

        // Impossible: missing item.
        check(solver,
                Arrays.asList(Arrays.asList("a"), Arrays.asList("b")),
                new long[]{1, 1},
                Arrays.asList("a", "c"),
                -1);

        // want is empty -> 0 (nothing to buy).
        check(solver,
                Arrays.asList(Arrays.asList("a")),
                new long[]{100},
                new ArrayList<>(),
                0);

        // Bundles contain irrelevant items; mask collapses correctly.
        check(solver,
                Arrays.asList(Arrays.asList("x","y","a"), Arrays.asList("z","b"), Arrays.asList("c","w")),
                new long[]{4, 3, 2},
                Arrays.asList("a","b","c"),
                9);

        // Two bundles cover want via different subsets — pick the cheaper combo.
        check(solver,
                Arrays.asList(Arrays.asList("a","b"), Arrays.asList("b","c"), Arrays.asList("a","c")),
                new long[]{3, 4, 5},
                Arrays.asList("a","b","c"),
                7);   // best: bundles {0,1} = 3+4

        // ---- Random fuzz against O(2^B) brute force ----
        Random rnd = new Random(2026);
        int trials = 200, fails = 0;
        for (int t = 0; t < trials; t++) {
            int k = 1 + rnd.nextInt(6);            // |want| 1..6
            int B = 1 + rnd.nextInt(10);           // |bundles| 1..10
            List<String> want = new ArrayList<>();
            for (int j = 0; j < k; j++) want.add(String.valueOf((char) ('a' + j)));
            List<List<String>> bundles = new ArrayList<>();
            long[] cost = new long[B];
            for (int i = 0; i < B; i++) {
                List<String> b = new ArrayList<>();
                for (int j = 0; j < k; j++) {
                    if (rnd.nextDouble() < 0.5) b.add(want.get(j));
                }
                if (rnd.nextDouble() < 0.3) b.add("noise" + rnd.nextInt(3));   // unrelated item
                bundles.add(b);
                cost[i] = 1 + rnd.nextInt(20);
            }
            long fast  = solver.minCost(bundles, cost, want);
            long brute = solver.minCostBrute(bundles, cost, want);
            if (fast != brute) {
                fails++;
                System.out.println("MISMATCH fast=" + fast + " brute=" + brute
                        + " bundles=" + bundles + " cost=" + Arrays.toString(cost) + " want=" + want);
            }
        }
        System.out.println("Random cross-check: " + (trials - fails) + "/" + trials + " ok");

        // ---- Stress: |want|=20, |bundles|=200.  Just confirm it runs. ----
        int K = 20, BB = 200;
        List<String> want = new ArrayList<>();
        for (int j = 0; j < K; j++) want.add("w" + j);
        List<List<String>> bundles = new ArrayList<>();
        long[] cost = new long[BB];
        Random brnd = new Random(1);
        for (int i = 0; i < BB; i++) {
            List<String> b = new ArrayList<>();
            for (int j = 0; j < K; j++) if (brnd.nextDouble() < 0.15) b.add(want.get(j));
            bundles.add(b);
            cost[i] = 1 + brnd.nextInt(1_000_000_000);
        }
        long t0 = System.nanoTime();
        long ans = solver.minCost(bundles, cost, want);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress |want|=" + K + " |bundles|=" + BB
                + ": ans=" + ans + " in " + ms + " ms");
    }

    private static void check(MinimumCostToCoverDesiredItems solver,
                              List<List<String>> bundles, long[] cost,
                              List<String> want, long expected) {
        long fast = solver.minCost(bundles, cost, want);
        long brute = solver.minCostBrute(bundles, cost, want);
        boolean ok = fast == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "expected=" + expected + " fast=" + fast + " brute=" + brute
                + " want=" + want);
    }
}
