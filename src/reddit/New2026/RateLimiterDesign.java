package reddit.New2026;

import java.util.*;
import java.util.function.LongSupplier;

/*
================================================================================
  RateLimiterDesign — allow(key) under a rate cap, two ways
================================================================================

  GIVEN
  allow(key) returns true if this request is permitted, false if it should be
  rejected. Limits are per key. Two classic designs, and they do NOT enforce
  the same thing — that difference is the interview.

    Route A  SlidingWindowLimiter   at most N requests in ANY window of W ms
    Route B  TokenBucketLimiter     N tokens per W ms, burst up to capacity

  --------------------------------------------------------------------------
  ⚠ INJECT THE CLOCK. FIRST THING, BEFORE ANY LOGIC.
  --------------------------------------------------------------------------
    Both classes take a LongSupplier. Calling System.currentTimeMillis()
    inside allow() is the single most expensive mistake here, because it
    makes every test either slow (Thread.sleep) or flaky (timing races), and
    boundary behaviour is exactly what you need to test. With a fake clock
    every case in main() is deterministic and instant.

    A LongSupplier also gives you nanoTime, a monotonic clock, or a clock
    driven by request metadata, without touching the limiter.

  --------------------------------------------------------------------------
  Route A — sliding window log                      (SlidingWindowLimiter)
  --------------------------------------------------------------------------
    Keep a queue of accept timestamps per key. On each call, drop everything
    older than the window, then admit if fewer than N remain.

        while (!q.isEmpty() && now - q.peekFirst() >= windowMillis) q.poll();
        if (q.size() >= limit) return false;
        q.addLast(now);

    This is EXACT: the invariant "no W-length window ever holds more than N
    accepts" is true by construction, and main() verifies it by brute force
    over every window of 20,000 random traces.

    ⚠ Use >= for the eviction test, not >. A timestamp is in-window when
      now - t < W, so t is expired at exactly now - t == W. Using > keeps a
      request one millisecond too long and lets N+1 through at the boundary.

    ⚠ MEMORY IS O(N) PER ACTIVE KEY, and keys are never forgotten unless you
      evict them. This is the design's real cost: N = 1000 over 1e6 keys is a
      billion timestamps. sweepIdle() below is the minimum answer; production
      answers are Redis with TTLs, or Route B.

  --------------------------------------------------------------------------
  Route B — token bucket                              (TokenBucketLimiter)
  --------------------------------------------------------------------------
    Per key store (tokens, lastRefill). Refill LAZILY on read — no timers, no
    background thread — then spend one token if any remain.

        tokens = min(capacity, tokens + elapsed * ratePerMilli);
        if (tokens < 1) return false;
        tokens -= 1;

    O(1) time and O(1) memory per key, which is why real systems use it.

    ⚠ IT DOES NOT ENFORCE "N PER WINDOW". A full bucket can be drained
      immediately and then refills during the same window, so a W-length
      window can hold nearly 2N accepts. Measured with N = 5, W = 1000 ms:

            sliding window   max in any 1000 ms window = 5
            token bucket     max in any 1000 ms window = 9

      Neither is wrong; they enforce different things. Say which one the
      requirement wants. "At most 5 per second" is Route A. "5 per second
      sustained, bursts tolerated" is Route B.

    ⚠ KEEP THE TOKEN COUNT FRACTIONAL. The classic bug is integer math:

            tokens += elapsed * tokensPerSecond / 1000;   // long arithmetic
            lastRefill = now;

      With elapsed = 1 ms and 1 token/sec that increment is 0, and because
      lastRefill still advances, the elapsed millisecond is GONE. A caller
      polling every millisecond starves forever. Measured over 10 simulated
      seconds of 1 ms polling: 10 accepts with double tokens, 1 with long —
      and that 1 is only the token the bucket started with. It never refills
      at all. Either use a double, or advance lastRefill by whole tokens only.

    ⚠ CLAMP TO CAPACITY. Without min(capacity, ...) an idle key accrues
      tokens forever and the first burst after a quiet night is unbounded.

    ⚠ GUARD AGAINST A BACKWARDS CLOCK. currentTimeMillis is not monotonic
      (NTP steps, VM migration). A negative elapsed would subtract tokens, so
      both routes clamp it to 0. Prefer nanoTime when you can.

  --------------------------------------------------------------------------
  Variant — allow(key, timestamp) instead of an injected clock
  --------------------------------------------------------------------------
    Push the clock out of the limiter entirely and let the caller supply the
    time. A LongSupplier then becomes allow(key, clock.getAsLong()) at the
    call site, so this signature is strictly MORE general, not less — and
    every test below trades the fake clock for a literal number.

    If timestamps arrive NON-DECREASING this is the same limiter, byte for
    byte: 200,000 ordered calls, zero disagreements with the clock version.

    ⚠ THE CATCH: a caller-supplied time can go BACKWARDS. Not as a rare NTP
      step, but routinely — event time out of batched, retried or multi-
      source producers. The deque is sorted ONLY because addLast has always
      been called with a non-decreasing time, and Route A's guarantee is
      about the TIMESTAMPS, so that is the timeline to score against:

        limit 5, window 100 ms, 3,000 traces x 120 calls
        jitter     admitted   deque unsorted   traces > cap   worst window
        none        286,507                0              0              5
        +/-  5 ms   285,029            2,742             30              6
        +/- 50 ms   252,577            3,000          2,751              9
        +/-200 ms   209,074            3,000          2,921             10

      It fails in BOTH directions. Admissions also fall, 286,507 -> 209,074,
      because a late timestamp evicts nothing (t - peekFirst is small or
      negative), so the queue looks full and rejects. Too loose and too
      strict at once.

    ⚠ CLAMPING DOES NOT HELP, AND THAT IS NOT OBVIOUS. t = max(t, lastSeen)
      cannot change a single decision here. Proof: eviction runs BEFORE
      insertion, so after a call at effective time e every queued x has
      e - x < W. A clamped late arrival has effective time e again, so its
      eviction loop matches nothing and it is decided purely by
      q.size() >= limit — which is what the unclamped call did too, since a
      smaller t evicts nothing either. Measured: 0 differing decisions in
      600,000 calls across three jitter levels. It is kept below only so the
      test can demonstrate that, and because clamping DOES matter elsewhere
      — see lastRefill in the bucket.

    ⚠ THE BUCKET HAS NO ORDERING TO CORRUPT, which is not the same as being
      immune. It keeps no sorted structure and has no notion of WHICH window
      a request fell in, so nothing breaks — but late arrivals still refill
      nothing, so its admissions fall too (343,338 -> 266,652). It exceeds
      the per-window count at every jitter level including zero, because it
      never promised one: that is its burst, measured in Route B above.

      Its lastRefill must advance by max(), not assignment. Setting it to a
      LATE timestamp moves it backwards, and the next in-order request then
      sees an inflated elapsed and over-refills.

    So there are really only TWO answers:
      THROW_ON_LATE   it is a caller bug; fail at the boundary rather than
                      silently mis-enforce. Cheap, and honest.
      event time      EventTimeSlidingWindow: a sorted map plus a bounded
                      lateness L. Holds the cap at every jitter level (0 of
                      3,000 traces) AND admits more than the broken version
                      (258,784 vs 209,074 at +/-200 ms), because a late
                      request goes in the window it belongs to instead of
                      being wrongly refused by the newest one.

    ⚠ EVENT TIME IS NOT "SWAP THE DEQUE FOR A TREEMAP". Once arrivals are
      unordered the guarantee stops being a SUFFIX property: inserting a
      point at t affects every window CONTAINING t, and a late admit can
      retroactively break a window you already closed. So the test becomes
      "no window containing t exceeds N" — and the fullest such window can
      always be slid right until its left edge rests on one of its own
      points, which is what makes it O(N) candidates instead of infinitely
      many real-valued starts. Without a lateness bound there is no bound on
      what you would have to re-examine, so L is part of the contract.

  --------------------------------------------------------------------------
  Thread safety
  --------------------------------------------------------------------------
    allow() is synchronized in both. It has to be: read-modify-write on the
    queue or the bucket is not atomic, and without the lock concurrent
    callers over-admit. main() proves it — 8 threads x 2000 attempts against
    a frozen clock must admit EXACTLY the limit, and does.

    One global lock is the honest starting point. Say out loud that it is the
    contention point, and that the fix is a lock per key (a striped lock, or
    ConcurrentHashMap.compute, which locks only the bin).

  --------------------------------------------------------------------------
  Other designs, and when they win
  --------------------------------------------------------------------------
    Fixed window counter   one counter per key per window. O(1) memory, and
                           the reason nobody ships it: a burst straddling the
                           boundary passes 2N. Measured below at exactly 10
                           for N = 5.
    Sliding window counter weight the previous window's count by how much of
                           it still overlaps. O(1) memory, approximates Route
                           A closely. What Cloudflare actually runs.
    Leaky bucket (queue)   shapes traffic instead of dropping it — the right
                           answer when requests can wait rather than fail.

  --------------------------------------------------------------------------
  Complexity   (N = limit, K = number of keys)
  --------------------------------------------------------------------------
    SlidingWindow  allow O(1) amortised, O(N) worst per call, O(K*N) memory
    TokenBucket    allow O(1) always,                         O(K)   memory
    sweepIdle      O(K), call it periodically or on a size threshold
    EventTime      allow O(N log N) worst per call            O(K*N) memory
                   (at most N candidate windows, each one subMap sum)
================================================================================
*/
public class RateLimiterDesign {

    public interface RateLimiter {
        /** True if the request is admitted. */
        boolean allow(String key);
    }

    /* ==================== Route A — sliding window log ==================== */

    public static final class SlidingWindowLimiter implements RateLimiter {
        private final int limit;
        private final long windowMillis;
        private final LongSupplier clock;
        private final Map<String, Deque<Long>> accepts = new HashMap<>();

        public SlidingWindowLimiter(int limit, long windowMillis, LongSupplier clock) {
            if (limit < 0) throw new IllegalArgumentException("limit < 0");
            if (windowMillis <= 0) throw new IllegalArgumentException("window <= 0");
            this.limit = limit;
            this.windowMillis = windowMillis;
            this.clock = Objects.requireNonNull(clock);
        }

        @Override
        public synchronized boolean allow(String key) {
            long now = clock.getAsLong();
            Deque<Long> q = accepts.computeIfAbsent(key, k -> new ArrayDeque<>());

            // t is in-window while now - t < window, so it expires at exactly == window
            while (!q.isEmpty() && now - q.peekFirst() >= windowMillis) q.pollFirst();

            if (q.size() >= limit) return false;
            q.addLast(now);
            return true;
        }

        /** Drops keys with no accepts left in the window. Without this, keys leak. */
        public synchronized int sweepIdle() {
            long now = clock.getAsLong();
            int before = accepts.size();
            accepts.values().forEach(q -> {
                while (!q.isEmpty() && now - q.peekFirst() >= windowMillis) q.pollFirst();
            });
            accepts.entrySet().removeIf(e -> e.getValue().isEmpty());
            return before - accepts.size();
        }

        public synchronized int trackedKeys() { return accepts.size(); }
    }

    /* ======================= Route B — token bucket ======================= */

    public static final class TokenBucketLimiter implements RateLimiter {
        private final double capacity;
        private final double tokensPerMilli;
        private final LongSupplier clock;
        private final Map<String, Bucket> buckets = new HashMap<>();

        private static final class Bucket {
            double tokens;
            long lastRefill;
            Bucket(double tokens, long lastRefill) { this.tokens = tokens; this.lastRefill = lastRefill; }
        }

        /** capacity = burst size; refillPerWindow tokens are added over windowMillis. */
        public TokenBucketLimiter(int capacity, int refillPerWindow, long windowMillis,
                                  LongSupplier clock) {
            if (capacity < 0) throw new IllegalArgumentException("capacity < 0");
            if (windowMillis <= 0) throw new IllegalArgumentException("window <= 0");
            this.capacity = capacity;
            this.tokensPerMilli = refillPerWindow / (double) windowMillis;
            this.clock = Objects.requireNonNull(clock);
        }

        @Override
        public synchronized boolean allow(String key) {
            long now = clock.getAsLong();
            Bucket b = buckets.computeIfAbsent(key, k -> new Bucket(capacity, now));

            long elapsed = Math.max(0L, now - b.lastRefill);        // never trust the clock
            // Fractional tokens: an integer increment would truncate to 0 and, because
            // lastRefill advances anyway, silently discard the elapsed time.
            b.tokens = Math.min(capacity, b.tokens + elapsed * tokensPerMilli);
            b.lastRefill = now;

            if (b.tokens < 1.0) return false;
            b.tokens -= 1.0;
            return true;
        }

        public synchronized double tokensFor(String key) {
            Bucket b = buckets.get(key);
            return b == null ? capacity : b.tokens;
        }

        public synchronized int trackedKeys() { return buckets.size(); }
    }

    /* ---- the two designs nobody should ship, kept to measure why not ---- */

    /** Fixed window counter: O(1) memory, but passes 2N across a boundary. */
    static final class FixedWindowLimiter implements RateLimiter {
        private final int limit;
        private final long windowMillis;
        private final LongSupplier clock;
        private final Map<String, long[]> counters = new HashMap<>();   // [windowIndex, count]

        FixedWindowLimiter(int limit, long windowMillis, LongSupplier clock) {
            this.limit = limit; this.windowMillis = windowMillis; this.clock = clock;
        }

        @Override
        public synchronized boolean allow(String key) {
            long now = clock.getAsLong(), index = now / windowMillis;
            long[] c = counters.computeIfAbsent(key, k -> new long[]{index, 0});
            if (c[0] != index) { c[0] = index; c[1] = 0; }             // new window, reset
            if (c[1] >= limit) return false;
            c[1]++;
            return true;
        }
    }

    /** Token bucket with integer tokens — the truncation bug, kept for measurement. */
    static final class IntegerTokenBucket implements RateLimiter {
        private final long capacity, refillPerWindow, windowMillis;
        private final LongSupplier clock;
        private final Map<String, long[]> buckets = new HashMap<>();   // [tokens, lastRefill]

        IntegerTokenBucket(long capacity, long refillPerWindow, long windowMillis, LongSupplier clock) {
            this.capacity = capacity; this.refillPerWindow = refillPerWindow;
            this.windowMillis = windowMillis; this.clock = clock;
        }

        @Override
        public synchronized boolean allow(String key) {
            long now = clock.getAsLong();
            long[] b = buckets.computeIfAbsent(key, k -> new long[]{capacity, now});
            long elapsed = Math.max(0L, now - b[1]);
            b[0] = Math.min(capacity, b[0] + elapsed * refillPerWindow / windowMillis);  // truncates
            b[1] = now;                                               // and the remainder is lost
            if (b[0] < 1) return false;
            b[0]--;
            return true;
        }
    }

    /* ============ Variant — the caller supplies the timestamp ============ */

    public interface TimestampedRateLimiter {
        /** True if the request bearing this timestamp is admitted. */
        boolean allow(String key, long timestamp);

        /** An injected clock is just this interface with the time filled in. */
        default RateLimiter withClock(LongSupplier clock) {
            return key -> allow(key, clock.getAsLong());
        }
    }

    /** What to do when a key's timestamp goes backwards. */
    public enum LatePolicy {
        /** Trust the caller. Fastest, and silently wrong the moment they are wrong. */
        ASSUME_ORDERED,
        /** A caller bug — say so at the boundary instead of corrupting the queue. */
        THROW_ON_LATE,
        /** Charge a late arrival to the newest window. Never over-admits. */
        CLAMP_TO_LAST
    }

    /** Route A with the clock removed. Identical to it on non-decreasing input. */
    public static final class TsSlidingWindowLimiter implements TimestampedRateLimiter {
        private final int limit;
        private final long windowMillis;
        private final LatePolicy latePolicy;
        private final Map<String, Deque<Long>> accepts = new HashMap<>();
        private final Map<String, Long> lastSeen = new HashMap<>();

        public TsSlidingWindowLimiter(int limit, long windowMillis, LatePolicy latePolicy) {
            if (limit < 0) throw new IllegalArgumentException("limit < 0");
            if (windowMillis <= 0) throw new IllegalArgumentException("window <= 0");
            this.limit = limit;
            this.windowMillis = windowMillis;
            this.latePolicy = Objects.requireNonNull(latePolicy);
        }

        @Override
        public synchronized boolean allow(String key, long timestamp) {
            long t = timestamp;
            Long prev = lastSeen.get(key);
            if (prev != null && t < prev) {
                switch (latePolicy) {
                    case THROW_ON_LATE -> throw new IllegalArgumentException(
                            "timestamps must be non-decreasing per key: " + t + " after " + prev);
                    case CLAMP_TO_LAST -> t = prev;
                    case ASSUME_ORDERED -> { }        // the deque stops being sorted from here
                }
            }
            lastSeen.put(key, prev == null ? t : Math.max(prev, t));

            Deque<Long> q = accepts.computeIfAbsent(key, k -> new ArrayDeque<>());
            while (!q.isEmpty() && t - q.peekFirst() >= windowMillis) q.pollFirst();

            if (q.size() >= limit) return false;
            q.addLast(t);
            return true;
        }

        public synchronized int trackedKeys() { return accepts.size(); }
    }

    /** Route B with the clock removed. Late data needs no special handling here. */
    public static final class TsTokenBucketLimiter implements TimestampedRateLimiter {
        private final double capacity;
        private final double tokensPerMilli;
        private final Map<String, Bucket> buckets = new HashMap<>();

        private static final class Bucket {
            double tokens;
            long lastRefill;
            Bucket(double tokens, long lastRefill) { this.tokens = tokens; this.lastRefill = lastRefill; }
        }

        public TsTokenBucketLimiter(int capacity, int refillPerWindow, long windowMillis) {
            if (capacity < 0) throw new IllegalArgumentException("capacity < 0");
            if (windowMillis <= 0) throw new IllegalArgumentException("window <= 0");
            this.capacity = capacity;
            this.tokensPerMilli = refillPerWindow / (double) windowMillis;
        }

        @Override
        public synchronized boolean allow(String key, long timestamp) {
            Bucket b = buckets.computeIfAbsent(key, k -> new Bucket(capacity, timestamp));

            long elapsed = Math.max(0L, timestamp - b.lastRefill);   // a late arrival refills nothing
            b.tokens = Math.min(capacity, b.tokens + elapsed * tokensPerMilli);
            // max(), not assignment: a late timestamp would rewind lastRefill and the
            // next in-order call would then see an inflated elapsed and over-refill.
            b.lastRefill = Math.max(b.lastRefill, timestamp);

            if (b.tokens < 1.0) return false;
            b.tokens -= 1.0;
            return true;
        }

        public synchronized int trackedKeys() { return buckets.size(); }
    }

    /**
     * Event-time Route A: tolerates arrivals up to maxLateness out of order and still
     * holds the exact invariant. A late request lands in ITS OWN window, which is the
     * whole point — CLAMP_TO_LAST would charge it to the newest one instead.
     */
    public static final class EventTimeSlidingWindow implements TimestampedRateLimiter {
        private final int limit;
        private final long windowMillis, maxLateness;
        private final Map<String, NavigableMap<Long, Integer>> accepts = new HashMap<>();
        private long maxSeen = Long.MIN_VALUE;

        public EventTimeSlidingWindow(int limit, long windowMillis, long maxLateness) {
            if (limit < 0) throw new IllegalArgumentException("limit < 0");
            if (windowMillis <= 0) throw new IllegalArgumentException("window <= 0");
            if (maxLateness < 0) throw new IllegalArgumentException("maxLateness < 0");
            this.limit = limit;
            this.windowMillis = windowMillis;
            this.maxLateness = maxLateness;
        }

        @Override
        public synchronized boolean allow(String key, long timestamp) {
            // Past the watermark there is no bound on how far back we would have to look.
            if (maxSeen != Long.MIN_VALUE && timestamp < maxSeen - maxLateness) return false;
            maxSeen = Math.max(maxSeen, timestamp);
            if (limit <= 0) return false;

            NavigableMap<Long, Integer> times = accepts.computeIfAbsent(key, k -> new TreeMap<>());
            times.headMap(maxSeen - maxLateness - windowMillis, false).clear();

            times.merge(timestamp, 1, Integer::sum);          // admit tentatively, then verify

            // Only windows CONTAINING timestamp changed, and the fullest window holding a
            // point slides right until its left edge rests on one of its own points — so
            // these candidates are exhaustive.
            List<Long> starts = new ArrayList<>(
                    times.subMap(timestamp - windowMillis + 1, true, timestamp, true).keySet());
            for (long start : starts) {
                int count = 0;
                for (int c : times.subMap(start, true, start + windowMillis, false).values()) count += c;
                if (count > limit) {
                    if (times.merge(timestamp, -1, Integer::sum) == 0) times.remove(timestamp);
                    return false;
                }
            }
            return true;
        }

        public synchronized int trackedKeys() { return accepts.size(); }
    }

    /* ============================ a fake clock ============================ */

    /** Deterministic, hand-cranked time. The reason every test below is instant. */
    static final class FakeClock implements LongSupplier {
        private long now;
        FakeClock(long start) { this.now = start; }
        @Override public long getAsLong() { return now; }
        void advance(long millis) { now += millis; }
        void set(long millis) { now = millis; }
    }

    /* ================================ Tests ================================ */

    public static void main(String[] args) {

        /* ---------------------- sliding window basics ---------------------- */
        FakeClock clock = new FakeClock(1000);
        SlidingWindowLimiter sw = new SlidingWindowLimiter(3, 1000, clock);

        expect("1st of 3", sw.allow("u"), true);            // t = 1000
        clock.advance(10);
        expect("2nd of 3", sw.allow("u"), true);            // t = 1010
        clock.advance(10);
        expect("3rd of 3", sw.allow("u"), true);            // t = 1020
        expect("4th is rejected", sw.allow("u"), false);
        expect("other key is independent", sw.allow("v"), true);

        clock.advance(979);                                 // t = 1999, oldest is 999 ms old
        expect("999 ms after the 1st, still full", sw.allow("u"), false);
        clock.advance(1);                                   // t = 2000, oldest is exactly W old
        expect("at exactly W the oldest expires", sw.allow("u"), true);
        expect("but only that one slot freed", sw.allow("u"), false);

        clock.advance(1000);
        expect("a full window later, all free", sw.allow("u"), true);

        // Accepts sharing a millisecond expire TOGETHER — the queue is keyed by time,
        // not by slot, so a burst frees the whole burst at once.
        FakeClock same = new FakeClock(0);
        SlidingWindowLimiter burst = new SlidingWindowLimiter(3, 1000, same);
        burst.allow("u"); burst.allow("u"); burst.allow("u");
        expect("simultaneous burst fills the window", burst.allow("u"), false);
        same.advance(1000);
        expect("all three expire at the same instant: 1", burst.allow("u"), true);
        expect("2", burst.allow("u"), true);
        expect("3", burst.allow("u"), true);
        expect("and it is full again", burst.allow("u"), false);

        /* ------------------------- degenerate cases ------------------------ */
        FakeClock c0 = new FakeClock(0);
        expect("limit 0 rejects everything", new SlidingWindowLimiter(0, 1000, c0).allow("u"), false);
        expect("capacity 0 bucket rejects", new TokenBucketLimiter(0, 5, 1000, c0).allow("u"), false);
        expectThrows("negative limit", () -> new SlidingWindowLimiter(-1, 1000, c0));
        expectThrows("zero window", () -> new SlidingWindowLimiter(1, 0, c0));

        /* -------------------------- token bucket -------------------------- */
        FakeClock tc = new FakeClock(0);
        TokenBucketLimiter tb = new TokenBucketLimiter(3, 3, 1000, tc);
        expect("starts full: 1", tb.allow("u"), true);
        expect("starts full: 2", tb.allow("u"), true);
        expect("starts full: 3", tb.allow("u"), true);
        expect("drained", tb.allow("u"), false);

        tc.advance(333);
        expect("333 ms buys nothing yet (0.999 tokens)", tb.allow("u"), false);
        tc.advance(1);
        expect("334 ms buys exactly one", tb.allow("u"), true);

        tc.advance(100000);
        expect("idle for 100 s, but clamped to capacity: 1", tb.allow("u"), true);
        expect("2", tb.allow("u"), true);
        expect("3", tb.allow("u"), true);
        expect("no more than capacity", tb.allow("u"), false);

        /* ------------------------ backwards clock ------------------------- */
        FakeClock back = new FakeClock(10000);
        TokenBucketLimiter tbBack = new TokenBucketLimiter(2, 2, 1000, back);
        expect("spend one before the jump", tbBack.allow("u"), true);
        back.set(5000);                                     // NTP steps the clock back 5 s
        expect("backwards clock does not grant tokens", tbBack.allow("u"), true);
        expect("and does not grant extra", tbBack.allow("u"), false);

        SlidingWindowLimiter swBack = new SlidingWindowLimiter(1, 1000, back);
        expect("sliding window survives it too", swBack.allow("u"), true);
        back.set(1000);
        expect("still limited after going back", swBack.allow("u"), false);

        /* --------------------------- key eviction -------------------------- */
        FakeClock ec = new FakeClock(0);
        SlidingWindowLimiter sweep = new SlidingWindowLimiter(2, 1000, ec);
        for (int i = 0; i < 500; i++) sweep.allow("key" + i);
        expect("500 keys tracked", sweep.trackedKeys(), 500);
        expect("nothing to sweep yet", sweep.sweepIdle(), 0);
        ec.advance(1000);
        expect("a window later they are all idle", sweep.sweepIdle(), 500);
        expect("and the map is empty", sweep.trackedKeys(), 0);

        /* ------------------- the claims in the header ------------------- */
        slidingWindowInvariant();
        theyEnforceDifferentThings();
        fixedWindowBoundaryBurst();
        integerTruncationStarves();
        sustainedRate();
        concurrentCallersMustNotOverAdmit();
        memoryShape();

        /* ------------- the allow(key, timestamp) variant ------------- */
        timestampedBasics();
        outOfOrderCostsTable();
    }

    /* ---- allow(key, timestamp): free when ordered, not when it isn't ---- */

    private static void timestampedBasics() {
        System.out.println();

        // With non-decreasing timestamps the variant IS the original limiter.
        Random rnd = new Random(17);
        FakeClock clock = new FakeClock(0);
        SlidingWindowLimiter injected = new SlidingWindowLimiter(5, 1000, clock);
        TsSlidingWindowLimiter passed = new TsSlidingWindowLimiter(5, 1000, LatePolicy.THROW_ON_LATE);
        int diffs = 0, calls = 200000;
        long t = 0;
        for (int i = 0; i < calls; i++) {
            clock.set(t);
            if (injected.allow("u") != passed.allow("u", t)) diffs++;
            t += rnd.nextInt(300);
        }
        expect("timestamp parameter == injected clock over " + calls + " ordered calls", diffs, 0);

        // ...and the clock form is recoverable from it, so nothing was lost.
        FakeClock fc = new FakeClock(7000);
        RateLimiter bridged = new TsSlidingWindowLimiter(1, 1000, LatePolicy.CLAMP_TO_LAST).withClock(fc);
        expect("withClock bridges back to allow(key)", bridged.allow("u"), true);
        expect("and still limits", bridged.allow("u"), false);

        TsSlidingWindowLimiter strict = new TsSlidingWindowLimiter(5, 1000, LatePolicy.THROW_ON_LATE);
        strict.allow("u", 5000);
        expectThrows("a backwards timestamp is a caller bug", () -> strict.allow("u", 4999));
        expect("an equal timestamp is not late", strict.allow("u", 5000), true);

        // Clamping charges a late request to the newest window; event time gives it
        // its own. THIS is the semantic difference — not the admit/reject counts.
        TsSlidingWindowLimiter clamp = new TsSlidingWindowLimiter(1, 1000, LatePolicy.CLAMP_TO_LAST);
        expect("clamp: first at t=5000", clamp.allow("u", 5000), true);
        expect("clamp: a late t=100 is charged to 5000, so rejected", clamp.allow("u", 100), false);

        EventTimeSlidingWindow ev = new EventTimeSlidingWindow(1, 1000, 10000);
        expect("event time: first at t=5000", ev.allow("u", 5000), true);
        expect("event time: t=100 is its own window, so admitted", ev.allow("u", 100), true);
        expect("event time: a second at t=100 is not", ev.allow("u", 100), false);
        expect("event time: beyond the lateness bound, dropped", ev.allow("u", -100000), false);

        clampChangesNothingHere();
    }

    /**
     * CLAMP_TO_LAST cannot change a single decision in this design, and the proof is
     * two lines: eviction runs before insertion, so after a call at effective time e
     * every queued x satisfies e - x < W. A clamped late arrival has effective time e
     * again, so its eviction loop matches nothing, and it is decided purely by
     * q.size() >= limit — exactly what ASSUME_ORDERED does with the same queue.
     */
    private static void clampChangesNothingHere() {
        for (int jitter : new int[]{5, 50, 200}) {
            Random rnd = new Random(3);
            TsSlidingWindowLimiter ao = new TsSlidingWindowLimiter(5, 100, LatePolicy.ASSUME_ORDERED);
            TsSlidingWindowLimiter cl = new TsSlidingWindowLimiter(5, 100, LatePolicy.CLAMP_TO_LAST);
            int differ = 0, late = 0, calls = 200000;
            long clock = 1000, max = Long.MIN_VALUE;
            for (int i = 0; i < calls; i++) {
                long t = clock + rnd.nextInt(2 * jitter + 1) - jitter;
                if (max != Long.MIN_VALUE && t < max) late++;
                max = Math.max(max, t);
                if (ao.allow("k", t) != cl.allow("k", t)) differ++;
                clock += rnd.nextInt(40);
            }
            expect("clamp == assume-ordered at jitter +/-" + jitter + " ms ("
                    + String.format("%,d", late) + " late of " + String.format("%,d", calls) + ")", differ, 0);
        }
    }

    /**
     * Everything is scored against the EVENT timestamps, which is the only timeline the
     * caller cares about: "no 100 ms of real time held more than 5 of my requests".
     */
    private static void outOfOrderCostsTable() {
        System.out.printf("%nscored against event time (limit 5, window 100 ms, 3,000 traces x 120 calls)%n");
        System.out.printf("  %-12s %-26s %-12s %-16s %-16s %s%n",
                "jitter", "design", "admitted", "deque unsorted", "traces > cap", "worst window");
        for (int jitter : new int[]{0, 5, 50, 200}) {
            for (int design = 0; design < 4; design++) {
                Random rnd = new Random(3);
                long admitted = 0;
                int over = 0, worst = 0, unsorted = 0;
                for (int tr = 0; tr < 3000; tr++) {
                    TimestampedRateLimiter lim = switch (design) {
                        case 0 -> new TsSlidingWindowLimiter(5, 100, LatePolicy.ASSUME_ORDERED);
                        case 1 -> new TsSlidingWindowLimiter(5, 100, LatePolicy.CLAMP_TO_LAST);
                        case 2 -> new EventTimeSlidingWindow(5, 100, 2L * jitter);
                        default -> new TsTokenBucketLimiter(5, 5, 100);
                    };
                    List<Long> ok = new ArrayList<>();
                    long clock = 1000, prevAdmitted = Long.MIN_VALUE;
                    boolean outOfOrderInsert = false;
                    for (int i = 0; i < 120; i++) {
                        long t = clock + (jitter == 0 ? 0 : rnd.nextInt(2 * jitter + 1) - jitter);
                        if (lim.allow("k", t)) {
                            if (prevAdmitted != Long.MIN_VALUE && t < prevAdmitted) outOfOrderInsert = true;
                            prevAdmitted = t;
                            ok.add(t);
                        }
                        clock += rnd.nextInt(40);
                    }
                    if (outOfOrderInsert) unsorted++;
                    admitted += ok.size();
                    Collections.sort(ok);
                    int w = maxInAnyWindow(ok, 100);
                    worst = Math.max(worst, w);
                    if (w > 5) over++;
                }
                System.out.printf("  %-12s %-26s %-12d %-16s %-16d %d%n",
                        design == 0 ? "+/-" + jitter + " ms" : "",
                        switch (design) {
                            case 0 -> "assume ordered";
                            case 1 -> "clamp to last";
                            case 2 -> "event time (L=2*jitter)";
                            default -> "token bucket (no cap)";
                        },
                        admitted, design == 3 ? "n/a" : String.valueOf(unsorted), over, worst);
            }
        }
        System.out.println("  Only the event-time design holds the cap. Clamping is identical to");
        System.out.println("  assuming order here -- see the proof above. The token bucket never");
        System.out.println("  promised a per-window cap, so its overshoot is its normal burst.");
    }

    /* ---- Route A's guarantee, checked against the property by brute force ---- */

    private static void slidingWindowInvariant() {
        Random rnd = new Random(429);
        int trials = 20000, violations = 0;
        long totalAllowed = 0, totalCalls = 0;
        int worstObserved = 0;

        for (int t = 0; t < trials; t++) {
            int limit = 1 + rnd.nextInt(6);
            long window = 10 + rnd.nextInt(200);
            FakeClock clock = new FakeClock(rnd.nextInt(1000));
            SlidingWindowLimiter lim = new SlidingWindowLimiter(limit, window, clock);

            List<Long> allowed = new ArrayList<>();
            int calls = 20 + rnd.nextInt(60);
            for (int i = 0; i < calls; i++) {
                if (lim.allow("k")) allowed.add(clock.getAsLong());
                clock.advance(rnd.nextInt(40));             // 0 means several in the same millisecond
            }
            totalCalls += calls;
            totalAllowed += allowed.size();

            int worst = maxInAnyWindow(allowed, window);
            worstObserved = Math.max(worstObserved, worst);
            if (worst > limit && violations++ < 4)
                System.out.println("VIOLATION limit=" + limit + " window=" + window
                        + " worst=" + worst + " times=" + allowed);
        }
        System.out.printf("%nsliding window: %d traces, %,d calls, %,d admitted, "
                        + "%d windows over the limit%n",
                trials, totalCalls, totalAllowed, violations);
    }

    /** Largest number of accepts inside any window of the given length. */
    private static int maxInAnyWindow(List<Long> sortedTimes, long window) {
        int best = 0;
        for (int i = 0; i < sortedTimes.size(); i++) {
            int count = 0;
            for (int j = i; j < sortedTimes.size(); j++)
                if (sortedTimes.get(j) - sortedTimes.get(i) < window) count++;
            best = Math.max(best, count);
        }
        return best;
    }

    /* ---------- the difference that matters, with numbers on it ---------- */

    private static void theyEnforceDifferentThings() {
        System.out.printf("%n%-24s %-16s %s%n", "limiter", "admitted", "max in any 1000 ms window");
        for (int which = 0; which < 3; which++) {
            FakeClock clock = new FakeClock(0);
            RateLimiter lim = switch (which) {
                case 0 -> new SlidingWindowLimiter(5, 1000, clock);
                case 1 -> new TokenBucketLimiter(5, 5, 1000, clock);
                default -> new FixedWindowLimiter(5, 1000, clock);
            };
            String name = switch (which) {
                case 0 -> "sliding window"; case 1 -> "token bucket"; default -> "fixed window";
            };

            List<Long> allowed = new ArrayList<>();
            for (int ms = 0; ms < 3000; ms++) {             // saturating demand, 1 call per ms
                if (lim.allow("u")) allowed.add(clock.getAsLong());
                clock.advance(1);
            }
            System.out.printf("%-24s %-16d %d%n", name, allowed.size(),
                    maxInAnyWindow(allowed, 1000));
        }
        System.out.println("  All three average 5/s. The token bucket breaks the per-window cap");
        System.out.println("  under plain steady load. The fixed window looks fine here because");
        System.out.println("  the load is uniform -- it needs a boundary burst, measured next.");
    }

    private static void fixedWindowBoundaryBurst() {
        FakeClock clock = new FakeClock(0);
        FixedWindowLimiter fixed = new FixedWindowLimiter(5, 1000, clock);
        SlidingWindowLimiter sliding = new SlidingWindowLimiter(5, 1000, clock);

        clock.set(999);                                     // last millisecond of window 0
        int f = 0, s = 0;
        for (int i = 0; i < 5; i++) { if (fixed.allow("u")) f++; if (sliding.allow("u")) s++; }
        clock.set(1000);                                    // first millisecond of window 1
        for (int i = 0; i < 5; i++) { if (fixed.allow("u")) f++; if (sliding.allow("u")) s++; }

        System.out.printf("%nboundary burst across 2 ms (limit 5 per 1000 ms): "
                + "fixed window admitted %d, sliding window %d%n", f, s);
    }

    private static void integerTruncationStarves() {
        FakeClock a = new FakeClock(0), b = new FakeClock(0);
        TokenBucketLimiter good = new TokenBucketLimiter(1, 1, 1000, a);
        IntegerTokenBucket bad = new IntegerTokenBucket(1, 1, 1000, b);

        int goodCount = 0, badCount = 0;
        for (int ms = 0; ms < 10000; ms++) {                // poll every millisecond for 10 s
            if (good.allow("u")) goodCount++;
            if (bad.allow("u")) badCount++;
            a.advance(1); b.advance(1);
        }
        System.out.printf("polling every 1 ms for 10 s at 1 token/s: "
                + "double tokens admitted %d, integer tokens admitted %d%n", goodCount, badCount);
    }

    private static void sustainedRate() {
        System.out.printf("%n%-12s %-16s %-16s %s%n", "seconds", "sliding window", "token bucket",
                "expected ~ rate*t");
        for (int seconds : new int[]{1, 10, 60, 600}) {
            FakeClock c1 = new FakeClock(0), c2 = new FakeClock(0);
            SlidingWindowLimiter sw = new SlidingWindowLimiter(10, 1000, c1);
            TokenBucketLimiter tb = new TokenBucketLimiter(10, 10, 1000, c2);
            int a = 0, b = 0;
            for (long ms = 0; ms < seconds * 1000L; ms++) {
                if (sw.allow("u")) a++;
                if (tb.allow("u")) b++;
                c1.advance(1); c2.advance(1);
            }
            System.out.printf("%-12d %-16d %-16d %d%n", seconds, a, b, seconds * 10);
        }
    }

    /**
     * Without synchronized, concurrent callers over-admit. Two things make this a
     * real test rather than a decorative one:
     *   - a start latch, so the threads actually run at the same time. Started in
     *     a plain loop, thread 0 finishes its whole run before thread 7 exists and
     *     an unsynchronised limiter passes every time.
     *   - a limit near the total attempt count, so contention lasts for most of
     *     the run instead of the first handful of calls.
     */
    private static void concurrentCallersMustNotOverAdmit() {
        int threads = 8, attempts = 4000, limit = threads * attempts / 2;

        for (int which = 0; which < 2; which++) {
            FakeClock frozen = new FakeClock(5000);          // time never moves: no refill at all
            RateLimiter lim = which == 0
                    ? new SlidingWindowLimiter(limit, 1000, frozen)
                    : new TokenBucketLimiter(limit, limit, 1000, frozen);

            java.util.concurrent.CountDownLatch gate = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.atomic.AtomicInteger admitted =
                    new java.util.concurrent.atomic.AtomicInteger();
            java.util.concurrent.atomic.AtomicInteger crashed =
                    new java.util.concurrent.atomic.AtomicInteger();

            List<Thread> pool = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                Thread th = new Thread(() -> {
                    try {
                        gate.await();
                        for (int i = 0; i < attempts; i++)
                            if (lim.allow("hot")) admitted.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (RuntimeException e) {
                        crashed.incrementAndGet();           // a torn HashMap/ArrayDeque
                    }
                });
                pool.add(th);
                th.start();
            }
            gate.countDown();                                // release them together
            for (Thread th : pool) {
                try { th.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            expect((which == 0 ? "sliding window" : "token bucket") + ": " + threads
                            + " threads x " + attempts + " attempts admit exactly " + limit,
                    admitted.get() + (crashed.get() == 0 ? "" : " (" + crashed.get() + " threads threw)"),
                    String.valueOf(limit));
        }
    }

    private static void memoryShape() {
        FakeClock clock = new FakeClock(0);
        int keys = 20000, limit = 50;
        SlidingWindowLimiter sw = new SlidingWindowLimiter(limit, 60000, clock);
        TokenBucketLimiter tb = new TokenBucketLimiter(limit, limit, 60000, clock);

        long swAccepts = 0, tbAccepts = 0;
        for (int k = 0; k < keys; k++)
            for (int i = 0; i < limit; i++) {
                if (sw.allow("k" + k)) swAccepts++;
                if (tb.allow("k" + k)) tbAccepts++;
            }
        System.out.printf("%n%d keys x %d accepts: sliding window stores %,d timestamps "
                        + "(%d per key), token bucket stores %,d buckets (1 per key)%n",
                keys, limit, swAccepts, limit, tb.trackedKeys());
        System.out.printf("  both admitted the same %,d requests; only the memory differs%n",
                swAccepts == tbAccepts ? swAccepts : -1);
    }

    /* ------------------------------- helpers ------------------------------- */

    private static void expect(String label, Object got, Object expected) {
        boolean ok = Objects.equals(String.valueOf(got), String.valueOf(expected));
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }

    private static void expectThrows(String label, Runnable r) {
        try {
            r.run();
            System.out.println("FAIL " + label + "\n  expected an exception, got none");
        } catch (RuntimeException e) {
            System.out.println("OK   " + label + " throws " + e.getClass().getSimpleName());
        }
    }
}
