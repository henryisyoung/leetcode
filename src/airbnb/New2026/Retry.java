package airbnb.New2026;
/*
Airbnb phone-screen: implement a Retry class with three policies, sharing
the retry loop via one helper.

  executeOnce(cb)              — call; on failure retry exactly once.
  execute(cb, maxRetry, ms)    — up to maxRetry retries, FIXED sleep ms.
  executeExp(cb, maxRetry, ms) — up to maxRetry retries, EXPONENTIAL sleep:
                                 ms, 2*ms, 4*ms, ...

Rules
  - The first call is not a "retry": at most (1 + maxRetry) total attempts.
  - If every attempt fails, rethrow the LAST exception (keep its type).
  - Supplier#get can't throw checked exceptions, so we catch RuntimeException.

All three differ only in the sleep duration before each retry, so the loop
lives in ONE private helper and each public method is a one-liner.
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Retry {

    /** Retry exactly once (up to 2 attempts), no sleep. */
    public <T> T executeOnce(Supplier<T> cb) {
        return run(cb, 1, 0, false);
    }

    /** Up to maxRetry retries, fixed sleep between attempts. */
    public <T> T execute(Supplier<T> cb, int maxRetry, long backoffMs) {
        return run(cb, maxRetry, backoffMs, false);
    }

    /** Up to maxRetry retries, exponential sleep: base, 2*base, 4*base, ... */
    public <T> T executeExp(Supplier<T> cb, int maxRetry, long baseMs) {
        return run(cb, maxRetry, baseMs, true);
    }

    /* The whole point: one loop, shared by all three policies. */
    private <T> T run(Supplier<T> cb, int maxRetry, long baseMs, boolean exponential) {
        int attempt = 0;
        while (true) {
            try {
                return cb.get();
            } catch (RuntimeException e) {
                if (attempt >= maxRetry) throw e;          // out of retries -> propagate
                long sleep = exponential ? baseMs << attempt : baseMs;
                sleepMs(sleep);
                attempt++;
            }
        }
    }

    private static void sleepMs(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("retry interrupted", e);
        }
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        Retry retry = new Retry();

        // executeOnce: success on first try.
        AtomicInteger c1 = new AtomicInteger();
        String r1 = retry.executeOnce(() -> { c1.incrementAndGet(); return "ok"; });
        check("once: 1st try ok", r1.equals("ok") && c1.get() == 1);

        // executeOnce: fail once, succeed on the retry.
        AtomicInteger c2 = new AtomicInteger();
        String r2 = retry.executeOnce(() -> {
            if (c2.incrementAndGet() == 1) throw new RuntimeException("flaky");
            return "ok";
        });
        check("once: 2nd try ok", r2.equals("ok") && c2.get() == 2);

        // executeOnce: both attempts fail -> rethrow last exception.
        AtomicInteger c3 = new AtomicInteger();
        boolean threw3 = false;
        try {
            retry.executeOnce(() -> { c3.incrementAndGet(); throw new RuntimeException("nope"); });
        } catch (RuntimeException e) {
            threw3 = "nope".equals(e.getMessage());
        }
        check("once: 2 fails -> rethrow", threw3 && c3.get() == 2);

        // execute: succeeds on the 4th attempt (1 + 3 retries).
        AtomicInteger c4 = new AtomicInteger();
        int r4 = retry.execute(() -> {
            int v = c4.incrementAndGet();
            if (v < 4) throw new RuntimeException("retry me");
            return v;
        }, 3, 0);
        check("execute n=3: ok on attempt 4", r4 == 4 && c4.get() == 4);

        // execute: all attempts fail.
        AtomicInteger c5 = new AtomicInteger();
        boolean threw5 = false;
        try {
            retry.execute(() -> { c5.incrementAndGet(); throw new RuntimeException("x"); }, 3, 0);
        } catch (RuntimeException e) { threw5 = true; }
        check("execute n=3: 4 fails -> rethrow", threw5 && c5.get() == 4);

        // execute n=0: behaves like a single plain call.
        AtomicInteger c6 = new AtomicInteger();
        boolean threw6 = false;
        try {
            retry.execute(() -> { c6.incrementAndGet(); throw new RuntimeException("x"); }, 0, 100);
        } catch (RuntimeException e) { threw6 = true; }
        check("execute n=0: 1 attempt only", threw6 && c6.get() == 1);

        // executeExp: 5 retries, base=1ms -> sleeps 1+2+4+8+16 = 31ms, 6 attempts.
        AtomicInteger c7 = new AtomicInteger();
        boolean threw7 = false;
        try {
            retry.executeExp(() -> { c7.incrementAndGet(); throw new RuntimeException("x"); }, 5, 1);
        } catch (RuntimeException e) { threw7 = true; }
        check("executeExp: 6 attempts", threw7 && c7.get() == 6);
    }

    private static void check(String label, boolean ok) {
        System.out.println((ok ? "OK   " : "FAIL ") + label);
    }
}
