package airbnb.New2026;
/*
Banking-system "balance change over a period".

Records are append-only (timestamp, amount) adjustments. Given a query
window (start, end) we want the SUM of amounts whose timestamp falls
in the window.

Endpoint convention
  The problem text says "start exclusive, end inclusive", BUT its own
  example sums the adjustment at t == start:
      records = [(1000,50), (1500,-20), (2000,30)]
      window  = (1000, 2000)   -> expected 60 = 50 + (-20) + 30
  That output requires BOTH endpoints inclusive. We default to the
  example's behaviour and expose a flag for the strict-spec variant.

Operations
  void   record(long ts, long amount)       // append; ts must be >= last ts
  long   balanceChange(long start, long end) // [start, end]   (example mode)
  long   balanceChange(long start, long end, boolean startExclusive,
                                              boolean endInclusive)

I/O
  Input : list of (ts, amount), then queries (start, end)
  Output: change per query

Constraints (typical)
  Up to ~1e5 records, ~1e5 queries.
  amounts can be negative; running sums can overflow 32-bit -> use long.

Examples
  records: (1000,50) (1500,-20) (2000,30)
    [1000, 2000] -> 60
    [1001, 2000] -> 10            (drops t=1000)
    (1000, 2000] -> 10            (start exclusive)
    [1500, 1500] -> -20
    [3000, 4000] ->  0            (empty)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
Design

  Records arrive in non-decreasing timestamp order. Keep two parallel
  growable arrays:
      ts[]     — the timestamp of each record
      prefix[] — prefix[i] = sum of amounts[0..i-1]; prefix[0] = 0.

  After n records, prefix has length n+1.

  A query [start, end] reduces to:
      lo = first index with ts[i] >= start       (lower_bound)
      hi = first index with ts[i] >  end         (upper_bound)
      answer = prefix[hi] - prefix[lo]

  The startExclusive / endInclusive flags shift the comparator:
      lo' = startExclusive ? first ts[i] >  start : first ts[i] >= start
      hi' = endInclusive   ? first ts[i] >  end   : first ts[i] >= end

  Why not java.util.Arrays.binarySearch:
    Behaviour on duplicate keys is unspecified — for a banking system
    multiple adjustments can share a timestamp. Hand-rolled
    lower_bound / upper_bound give deterministic O(log n).

  Why "ts must be non-decreasing":
    The spec says timestamps are processed sequentially. Enforcing
    monotonicity lets us use the prefix-sum trick without sorting on
    every query. If out-of-order inserts were allowed we'd need a
    Fenwick tree keyed on coordinate-compressed timestamps — O(log n)
    per insert AND query — same big-O, more code.

Complexity
  record:        O(1) amortised
  balanceChange: O(log n)
  memory:        O(n)
*/
public class BalanceChangeOverPeriod {

    private long[] ts     = new long[16];
    private long[] prefix = new long[16];     // prefix[0] = 0, length = n + 1
    private int n = 0;                        // number of records

    public BalanceChangeOverPeriod() {
        prefix[0] = 0;
    }

    /** Append an adjustment. Timestamps must be non-decreasing. */
    public void record(long timestamp, long amount) {
        if (n > 0 && timestamp < ts[n - 1]) {
            throw new IllegalArgumentException(
                    "timestamps must be non-decreasing: got " + timestamp +
                    " after " + ts[n - 1]);
        }
        ensureCapacity(n + 1);
        ts[n] = timestamp;
        prefix[n + 1] = prefix[n] + amount;
        n++;
    }

    /** [start, end] inclusive on both — matches the problem's example. */
    public long balanceChange(long start, long end) {
        return balanceChange(start, end, /*startExclusive=*/false, /*endInclusive=*/true);
    }

    public long balanceChange(long start, long end,
                              boolean startExclusive, boolean endInclusive) {
        if (n == 0 || start > end) return 0;
        int lo = startExclusive ? upperBound(start) : lowerBound(start);
        int hi = endInclusive   ? upperBound(end)   : lowerBound(end);
        if (hi <= lo) return 0;
        return prefix[hi] - prefix[lo];
    }

    /** First i in [0, n] with ts[i] >= key (n if none). */
    private int lowerBound(long key) {
        int lo = 0, hi = n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ts[mid] < key) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /** First i in [0, n] with ts[i] > key (n if none). */
    private int upperBound(long key) {
        int lo = 0, hi = n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ts[mid] <= key) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    private void ensureCapacity(int need) {
        if (need < ts.length) return;
        int cap = ts.length;
        while (cap <= need) cap <<= 1;
        ts     = Arrays.copyOf(ts, cap);
        prefix = Arrays.copyOf(prefix, cap + 1);  // prefix is one longer than ts
    }

    public int size() { return n; }

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
     *   Line 1: N (number of records)
     *   Next N lines: "ts amount"
     *   Then Q (number of queries)
     *   Next Q lines: "start end"
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BalanceChangeOverPeriod sys = new BalanceChangeOverPeriod();
        int N = Integer.parseInt(br.readLine().trim());
        for (int i = 0; i < N; i++) {
            StringTokenizer t = new StringTokenizer(br.readLine());
            sys.record(Long.parseLong(t.nextToken()), Long.parseLong(t.nextToken()));
        }
        int Q = Integer.parseInt(br.readLine().trim());
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Q; i++) {
            StringTokenizer t = new StringTokenizer(br.readLine());
            long s = Long.parseLong(t.nextToken());
            long e = Long.parseLong(t.nextToken());
            out.append(sys.balanceChange(s, e)).append('\n');
        }
        System.out.print(out);
    }

    private static void runDemos() {
        BalanceChangeOverPeriod sys = new BalanceChangeOverPeriod();
        sys.record(1000,  50);
        sys.record(1500, -20);
        sys.record(2000,  30);

        // ---- Spec example (both endpoints inclusive) ----
        check("ex [1000,2000]", sys.balanceChange(1000, 2000), 60);

        // ---- Endpoint variants on the same data ----
        check("[1001,2000]", sys.balanceChange(1001, 2000), 10);
        check("(1000,2000] (start excl, end incl)",
                sys.balanceChange(1000, 2000, true, true), 10);
        check("[1000,2000) (start incl, end excl)",
                sys.balanceChange(1000, 2000, false, false), 30);
        check("(1000,2000) (both excl)",
                sys.balanceChange(1000, 2000, true, false), -20);

        // ---- Single-point / point-query cases ----
        check("[1500,1500] single",   sys.balanceChange(1500, 1500), -20);
        check("[2000,2000] last",     sys.balanceChange(2000, 2000),  30);
        check("[3000,4000] beyond",   sys.balanceChange(3000, 4000),   0);
        check("[0,999] before any",   sys.balanceChange(0, 999),       0);
        check("[0, 10^18] all",       sys.balanceChange(0, 1_000_000_000_000_000_000L), 60);
        check("start > end empty",    sys.balanceChange(2000, 1000),   0);

        // ---- Duplicate timestamps ----
        BalanceChangeOverPeriod dup = new BalanceChangeOverPeriod();
        dup.record(5,  10);
        dup.record(5,  20);
        dup.record(5, -30);
        dup.record(6,   7);
        check("dup ts [5,5]",  dup.balanceChange(5, 5),  0);   // 10 + 20 - 30
        check("dup ts [5,6]",  dup.balanceChange(5, 6),  7);
        check("dup ts (5,6]",  dup.balanceChange(5, 6, true, true),  7);
        check("dup ts [5,5)",  dup.balanceChange(5, 5, false, false), 0);

        // ---- Out-of-order record() should throw ----
        try {
            new BalanceChangeOverPeriod() {{
                record(100, 1);
                record(50, 1);
            }};
            check("rejects out-of-order ts", false, true);
        } catch (IllegalArgumentException ok) {
            check("rejects out-of-order ts", true, true);
        }

        // ---- Stress: 1e5 records + 1e5 queries ----
        BalanceChangeOverPeriod big = new BalanceChangeOverPeriod();
        int N = 100_000;
        long sumAll = 0;
        for (int i = 1; i <= N; i++) { big.record(i, i); sumAll += i; }
        // sum of [1..N] should match.
        check("stress full range", big.balanceChange(1, N), sumAll);

        long t0 = System.nanoTime();
        long total = 0;
        for (int q = 0; q < N; q++) {
            int a = 1 + (q * 7919) % N;
            int b = 1 + (q * 5417) % N;
            if (a > b) { int t = a; a = b; b = t; }
            total += big.balanceChange(a, b);
        }
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress 1e5 records + 1e5 queries: agg=" + total + " in " + ms + " ms");
    }

    private static void check(String label, long got, long expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static void check(String label, boolean got, boolean expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
