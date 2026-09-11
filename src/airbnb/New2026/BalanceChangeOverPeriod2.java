//package airbnb.New2026;
///*
//Banking-system "balance change over a period".
//
//Records are append-only (timestamp, amount) adjustments. Given a query
//window (start, end) we want the SUM of amounts whose timestamp falls
//in the window.
//
//Endpoint convention
//  The problem text says "start exclusive, end inclusive", BUT its own
//  example sums the adjustment at t == start:
//      records = [(1000,50), (1500,-20), (2000,30)]
//      window  = (1000, 2000)   -> expected 60 = 50 + (-20) + 30
//  That output requires BOTH endpoints inclusive. We default to the
//  example's behaviour and expose a flag for the strict-spec variant.
//
//Operations
//  void   record(long ts, long amount)       // append; ts must be >= last ts
//  long   balanceChange(long start, long end) // [start, end]   (example mode)
//  long   balanceChange(long start, long end, boolean startExclusive,
//                                              boolean endInclusive)
//
//I/O
//  Input : list of (ts, amount), then queries (start, end)
//  Output: change per query
//
//Constraints (typical)
//  Up to ~1e5 records, ~1e5 queries.
//  amounts can be negative; running sums can overflow 32-bit -> use long.
//
//Examples
//  records: (1000,50) (1500,-20) (2000,30)
//    [1000, 2000] -> 60
//    [1001, 2000] -> 10            (drops t=1000)
//    (1000, 2000] -> 10            (start exclusive)
//    [1500, 1500] -> -20
//    [3000, 4000] ->  0            (empty)
//*/
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.StringTokenizer;
//
///*
//Design
//
//  Records arrive in non-decreasing timestamp order. Keep two parallel
//  growable arrays:
//      ts[]     — the timestamp of each record
//      prefix[] — prefix[i] = sum of amounts[0..i-1]; prefix[0] = 0.
//
//  After n records, prefix has length n+1.
//
//  A query [start, end] reduces to:
//      lo = first index with ts[i] >= start       (lower_bound)
//      hi = first index with ts[i] >  end         (upper_bound)
//      answer = prefix[hi] - prefix[lo]
//
//  The startExclusive / endInclusive flags shift the comparator:
//      lo' = startExclusive ? first ts[i] >  start : first ts[i] >= start
//      hi' = endInclusive   ? first ts[i] >  end   : first ts[i] >= end
//
//  Why not java.util.Arrays.binarySearch:
//    Behaviour on duplicate keys is unspecified — for a banking system
//    multiple adjustments can share a timestamp. Hand-rolled
//    lower_bound / upper_bound give deterministic O(log n).
//
//  Why "ts must be non-decreasing":
//    The spec says timestamps are processed sequentially. Enforcing
//    monotonicity lets us use the prefix-sum trick without sorting on
//    every query. If out-of-order inserts were allowed we'd need a
//    Fenwick tree keyed on coordinate-compressed timestamps — O(log n)
//    per insert AND query — same big-O, more code.
//
//Complexity
//  record:        O(1) amortised
//  balanceChange: O(log n)
//  memory:        O(n)
//*/
//public class BalanceChangeOverPeriod2 {
//
//    List<Long> times;
//    List<Long> balances;
//    public BalanceChangeOverPeriod2() {
//        this.times = new ArrayList<>();
//        this.balances = new ArrayList<>();
//    }
//
//    /** Append an adjustment. Timestamps must be non-decreasing. */
//    public void record(long timestamp, long amount) {
//        if (!times.isEmpty() && timestamp < times.getLast()) throw new IllegalArgumentException("Unordered input");
//
//        times.add(timestamp);
//        long prev = balances.isEmpty() ? 0 : balances.getLast();
//        balances.add(prev + amount);
//    }
//
//    /** Balance after applying all records with timestamp <= t. */
//    public long balance(long t) {
//        if (times.isEmpty()) return 0;
//        int i = floorIndex(t);
//        return i < 0 ? 0 : balances.get(i);
//    }
//
//    /** [start, end] inclusive on both — matches the problem's example. */
//    public long balanceChange(long start, long end) {
//        if (times.isEmpty() || start > end) return 0;
//
//        int leftIndex = lowerBound(start);
//        int rightIndex = floorIndex(end);
//        if (leftIndex == times.size() || rightIndex < leftIndex) return 0;
//        long beforeLeft = leftIndex == 0 ? 0 : balances.get(leftIndex - 1);
//        return balances.get(rightIndex) - beforeLeft;
//    }
//
//    /** First index i where times[i] >= time. */
//    private int lowerBound(long time) {
//        int left = 0, right = times.size() - 1;
//        while (left + 1 < right) {
//            int mid = left + (right - left) / 2;
//            if (times.get(mid) < time) left = mid;
//            else right = mid;
//        }
//        if (times.get(left) >= time) return left;
//        if (times.get(right) >= time) return right;
//        return times.size();
//    }
//
//    /** Last index i where times[i] <= time. */
//    private int floorIndex(long time) {
//        int left = 0, right = times.size() - 1;
//        while (left + 1 < right) {
//            int mid = left + (right - left) / 2;
//            if (times.get(mid) <= time) left = mid;
//            else right = mid;
//        }
//        if (times.get(right) <= time) return right;
//        if (times.get(left) <= time) return left;
//        return -1;
//    }
//
//    public int size() { return times.size();  }
//}
