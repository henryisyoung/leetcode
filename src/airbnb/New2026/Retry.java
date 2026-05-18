package airbnb.New2026;
/*
Airbnb phone-screen: implement a Retry class with three policies and
share the loop body via a single helper.

  1. executeOnce(cb)               — call once, on failure retry exactly once.
  2. execute(cb, maxRetry, fixed)  — up to `maxRetry` retries, fixed sleep.
  3. executeExp(cb, maxRetry, base) — up to `maxRetry` retries, exponential
                                      back-off: base, 2*base, 4*base, ...
  4. retryer(cb, maxRetries)        — spec-shaped convenience for the
                                      "async retryer" variant: base = 2s,
                                      max sleep = 16s. Identical to
                                      executeExp(cb, maxRetries, 2_000) with
                                      maxBackoffMs temporarily forced to 16s.

In all three: the FIRST call doesn't count as a "retry". So we make at
most (1 + maxRetry) total attempts. If every attempt throws, rethrow
the LAST exception (preserve its type/stack).

I/O
  Generic in T. Caller supplies a Supplier<T>; we return T or rethrow.

Constraints (interview-style)
  maxRetry >= 0
  backoffMs >= 0
  Exponential back-off is capped to avoid overflow and runaway sleeps.
*/

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/*
Refactor

  All three policies share the same skeleton: try; on failure check the
  retry budget, sleep, repeat. The only thing that varies is the sleep
  duration for attempt `i`. So we extract ONE helper:

      <T> T executeWithBackoff(Supplier<T> cb, int maxRetry,
                               IntUnaryOperator sleepFor)

  and express each public method as a one-liner:

      executeOnce(cb)              -> executeWithBackoff(cb, 1, i -> 0)
      execute(cb, n, fixed)        -> executeWithBackoff(cb, n, i -> fixed)
      executeExp(cb, n, base)      -> executeWithBackoff(cb, n,
                                          i -> capExp(base, i))

  capExp(base, i) returns min(base * 2^i, MAX_BACKOFF_MS) using long
  math so a chain of doublings can't overflow int. Default cap is 60s
  per sleep — interview-safe, configurable via setter if a follow-up
  asks.

Exception strategy
  The interviewer's snippet catches "...e" — i.e. RuntimeException is
  fine for a Supplier-based API since Supplier#get can't throw checked
  exceptions. We propagate the LAST exception unchanged, so callers
  see the real failure (not a wrapping RetryException) when retries
  are exhausted.

Thread interruption
  If Thread.sleep() is interrupted we restore the interrupt flag and
  throw a RuntimeException — sleeping shorter than requested would
  silently violate the back-off contract.

Complexity
  attempts: at most (1 + maxRetry)
  time:     sum of back-offs + caller work
  memory:   O(1)
*/
public class Retry {

    public static final long DEFAULT_MAX_BACKOFF_MS = 60_000L;

    private long maxBackoffMs = DEFAULT_MAX_BACKOFF_MS;

    public void setMaxBackoffMs(long cap) {
        if (cap < 0) throw new IllegalArgumentException("cap must be >= 0: " + cap);
        this.maxBackoffMs = cap;
    }

    /** Retry exactly once (i.e. up to 2 attempts total), no back-off. */
    public <T> T executeOnce(Supplier<T> callback) {
        return executeWithBackoff(callback, 1, i -> 0L);
    }

    /** Up to `maxRetry` retries (1 + maxRetry attempts), fixed back-off. */
    public <T> T execute(Supplier<T> callback, int maxRetry, long backoffMs) {
        validate(maxRetry, backoffMs);
        return executeWithBackoff(callback, maxRetry, i -> backoffMs);
    }

    /** Up to `maxRetry` retries, exponential back-off: base, 2*base, 4*base ... */
    public <T> T executeExp(Supplier<T> callback, int maxRetry, long baseMs) {
        validate(maxRetry, baseMs);
        final long cap = maxBackoffMs;
        return executeWithBackoff(callback, maxRetry, i -> capExp(baseMs, i, cap));
    }

    /**
     * Spec convenience for the async-retryer variant:
     *   base = 2s, sleeps = 2s, 4s, 8s, 16s, 16s, ...   (capped at 16s)
     * Equivalent to executeExp(cb, maxRetries, 2_000) with the cap forced
     * to 16s for this call only.
     */
    public <T> T retryer(Supplier<T> callback, int maxRetries) {
        validate(maxRetries, 0);
        return executeWithBackoff(callback, maxRetries, i -> capExp(2_000L, i, 16_000L));
    }

    /* --------------------------- core helper --------------------------- */

    /**
     * Single source of truth for the retry loop. `sleepFor.applyAsInt(i)`
     * yields the millis to sleep BEFORE retry attempt (i+1), where i is
     * the zero-based index of the failure we just observed.
     */
    private <T> T executeWithBackoff(Supplier<T> callback, int maxRetry, IntToLong sleepFor) {
        if (callback == null) throw new IllegalArgumentException("callback is null");
        int attempt = 0;
        while (true) {
            try {
                return callback.get();
            } catch (RuntimeException e) {
                if (attempt >= maxRetry) throw e;          // budget exhausted -> propagate
                sleepMs(sleepFor.apply(attempt));
                attempt++;
            }
        }
    }

    /** Local functional iface — IntUnaryOperator only returns int; we want long. */
    @FunctionalInterface private interface IntToLong { long apply(int i); }

    /* --------------------------- helpers --------------------------- */

    private static void validate(int maxRetry, long backoffMs) {
        if (maxRetry < 0) throw new IllegalArgumentException("maxRetry must be >= 0: " + maxRetry);
        if (backoffMs < 0) throw new IllegalArgumentException("backoffMs must be >= 0: " + backoffMs);
    }

    /** min(base * 2^i, cap), in long math so we don't overflow. */
    static long capExp(long base, int i, long cap) {
        if (base == 0) return 0;
        // After ~62 shifts a long would overflow; cap immediately past that point.
        if (i >= 62) return cap;
        long shifted = base << i;
        // Detect overflow: a << i underflowed if base != shifted >> i (or sign flipped).
        if ((shifted >> i) != base || shifted < 0) return cap;
        return Math.min(shifted, cap);
    }

    private static void sleepMs(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("retry sleep interrupted", ie);
        }
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) throws IOException {
        runDemos();
    }

    private static void runDemos() {
        Retry retry = new Retry();

        // ---- executeOnce: succeed on first try ----
        AtomicInteger c1 = new AtomicInteger();
        String r1 = retry.executeOnce(() -> { c1.incrementAndGet(); return "ok"; });
        check("once: 1st try ok", r1.equals("ok") && c1.get() == 1);

        // ---- executeOnce: succeed on 2nd try ----
        AtomicInteger c2 = new AtomicInteger();
        String r2 = retry.executeOnce(() -> {
            if (c2.incrementAndGet() == 1) throw new RuntimeException("flaky");
            return "ok";
        });
        check("once: 2nd try ok", r2.equals("ok") && c2.get() == 2);

        // ---- executeOnce: 2 failures -> rethrow ----
        AtomicInteger c3 = new AtomicInteger();
        boolean threw3 = false;
        try {
            retry.executeOnce(() -> { c3.incrementAndGet(); throw new RuntimeException("nope"); });
        } catch (RuntimeException e) {
            threw3 = "nope".equals(e.getMessage());
        }
        check("once: 2 fails -> rethrow", threw3 && c3.get() == 2);

        // ---- execute(cb, 3, 0): succeeds on attempt 4 ----
        AtomicInteger c4 = new AtomicInteger();
        Integer r4 = retry.execute(() -> {
            int v = c4.incrementAndGet();
            if (v < 4) throw new RuntimeException("retry me");
            return v;
        }, 3, 0);
        check("execute n=3: succeeds on attempt 4", r4 == 4 && c4.get() == 4);

        // ---- execute(cb, 3, 0): fails after 4 attempts (1 + 3 retries) ----
        AtomicInteger c5 = new AtomicInteger();
        boolean threw5 = false;
        try {
            retry.execute(() -> { c5.incrementAndGet(); throw new RuntimeException("x"); }, 3, 0);
        } catch (RuntimeException e) { threw5 = true; }
        check("execute n=3: 4 fails -> rethrow", threw5 && c5.get() == 4);

        // ---- execute with maxRetry=0: behaves like a plain call ----
        AtomicInteger c6 = new AtomicInteger();
        boolean threw6 = false;
        try {
            retry.execute(() -> { c6.incrementAndGet(); throw new RuntimeException("x"); }, 0, 100);
        } catch (RuntimeException e) { threw6 = true; }
        check("execute n=0: 1 attempt only", threw6 && c6.get() == 1);

        // ---- executeExp: 5 retries, base=1ms — should observe ~1+2+4+8+16 = 31ms of sleeps ----
        AtomicInteger c7 = new AtomicInteger();
        long t0 = System.nanoTime();
        boolean threw7 = false;
        try {
            retry.executeExp(() -> { c7.incrementAndGet(); throw new RuntimeException("x"); }, 5, 1);
        } catch (RuntimeException e) { threw7 = true; }
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        check("executeExp: 6 attempts", threw7 && c7.get() == 6);
        // Loose bound — CI / shared machines vary. 31ms expected, accept up to 5s.
        check("executeExp: roughly exponential sleep", elapsedMs >= 25 && elapsedMs < 5_000);

        // ---- capExp arithmetic (no sleeps) ----
        check("capExp 1 i=0",  capExp(1, 0, 1000) == 1);
        check("capExp 1 i=10", capExp(1, 10, 1000) == 1000);   // 1024 capped to 1000
        check("capExp 100 i=3", capExp(100, 3, 60_000) == 800);
        check("capExp huge i, never overflows",
                capExp(1_000_000_000L, 60, 60_000) == 60_000);
        check("capExp i=62 forced cap", capExp(2, 62, 60_000) == 60_000);

        // ---- retryer (async-spec shape): wiring smoke test ----
        // Use maxRetries = 0 to avoid the real 2s+ sleeps; behavioural cases
        // are covered by the executeExp tests above. The exponential sequence
        // itself is verified below via capExp().
        AtomicInteger c8 = new AtomicInteger();
        Integer r8 = retry.retryer(() -> { c8.incrementAndGet(); return 42; }, 0);
        check("retryer: success returns value", r8 == 42 && c8.get() == 1);

        AtomicInteger c9 = new AtomicInteger();
        boolean threw9 = false;
        String msg9 = null;
        try {
            retry.retryer(() -> { c9.incrementAndGet(); throw new RuntimeException("Failure"); }, 0);
        } catch (RuntimeException e) { threw9 = true; msg9 = e.getMessage(); }
        check("retryer: rethrows original on exhaustion", threw9 && c9.get() == 1 && "Failure".equals(msg9));

        // ---- retryer back-off sequence matches spec: 2s, 4s, 8s, 16s, 16s, ... ----
        check("retryer seq i=0", capExp(2_000L, 0, 16_000L) ==  2_000L);
        check("retryer seq i=1", capExp(2_000L, 1, 16_000L) ==  4_000L);
        check("retryer seq i=2", capExp(2_000L, 2, 16_000L) ==  8_000L);
        check("retryer seq i=3", capExp(2_000L, 3, 16_000L) == 16_000L);
        check("retryer seq i=4 (capped)", capExp(2_000L, 4, 16_000L) == 16_000L);
        check("retryer seq i=10 (capped)", capExp(2_000L, 10, 16_000L) == 16_000L);

        // ---- Validation ----
        boolean v1 = false; try { retry.execute(() -> 1, -1, 0); } catch (IllegalArgumentException e) { v1 = true; }
        boolean v2 = false; try { retry.execute(() -> 1, 1, -1); } catch (IllegalArgumentException e) { v2 = true; }
        boolean v3 = false; try { retry.executeOnce(null); }      catch (IllegalArgumentException e) { v3 = true; }
        check("validates maxRetry < 0", v1);
        check("validates backoffMs < 0", v2);
        check("validates null callback", v3);
    }

    private static void check(String label, boolean ok) {
        System.out.println((ok ? "OK   " : "FAIL ") + label);
    }
}
