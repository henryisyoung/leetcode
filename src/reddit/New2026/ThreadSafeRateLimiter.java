package reddit.New2026;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
================================================================================
  ThreadSafeRateLimiter — interview version
================================================================================
  Same two limiters as RateLimiter.java, safe for many threads.

  The whole idea in two lines:
    1. ConcurrentHashMap.computeIfAbsent is atomic → exactly ONE deque/bucket
       per key, even if two threads see a new key at the same time.
    2. synchronized on that key's deque/bucket → check + update is one step,
       and different keys never block each other (lock per key, not global).

  Why a lock is needed at all: "check size, then add" is two steps. Without a
  lock, two threads both see size = limit - 1, both add → limit + 1 allowed.

  Out-of-order timestamps: thread A reads now=100, thread B reads now=101,
  B gets the lock first. Clamp `now` to the newest stored time so the deque
  stays sorted and the bucket clock never moves back.

  Things to say if asked:
    - Idle keys: remove a key only inside map.computeIfPresent, so no other
      thread is using that deque at the same moment.
    - Many servers: per-process locks don't help. Move state to Redis and do
      check + update in one Lua script (atomic on the Redis side).
================================================================================
*/
public class ThreadSafeRateLimiter {

    /** At most `limit` requests in any window of `window` ms, per key. */
    public static class WindowLimiter {
        private final ConcurrentHashMap<String, Deque<Long>> map = new ConcurrentHashMap<>();
        private final long window;
        private final int limit;

        public WindowLimiter(long window, int limit) {
            this.window = window;
            this.limit = limit;
        }

        public boolean allow(String key, long now) {
            Deque<Long> deque = map.computeIfAbsent(key, k -> new ArrayDeque<>());
            synchronized (deque) {
                if (!deque.isEmpty()) now = Math.max(now, deque.peekLast());
                while (!deque.isEmpty() && now - deque.peekFirst() >= window) {
                    deque.pollFirst();
                }
                if (deque.size() >= limit) {
                    return false;
                }
                deque.addLast(now);
                return true;
            }
        }
    }

    /** Token bucket: refill `ratePerMs` tokens per ms, burst up to `capacity`. */
    public static class BucketLimiter {
        private static class Bucket {
            double tokens;
            long lastUpdated;

            Bucket(double tokens, long lastUpdated) {
                this.tokens = tokens;
                this.lastUpdated = lastUpdated;
            }
        }

        private final ConcurrentHashMap<String, Bucket> map = new ConcurrentHashMap<>();
        private final double capacity;
        private final double ratePerMs;

        public BucketLimiter(double capacity, double ratePerMs) {
            this.capacity = capacity;
            this.ratePerMs = ratePerMs;
        }

        public boolean allow(String key, long now) {
            Bucket bucket = map.computeIfAbsent(key, k -> new Bucket(capacity, now));
            synchronized (bucket) {
                long elapsed = Math.max(0, now - bucket.lastUpdated);
                bucket.tokens = Math.min(capacity, bucket.tokens + elapsed * ratePerMs);
                bucket.lastUpdated = Math.max(bucket.lastUpdated, now);
                if (bucket.tokens < 1) {
                    return false;
                }
                bucket.tokens--;
                return true;
            }
        }
    }

    /* ============================== Tests ============================== */

    public static void main(String[] args) throws Exception {
        // Single-thread behaviour (PracHub example: N=3, W=10).
        WindowLimiter w = new WindowLimiter(10, 3);
        List<Boolean> got = new ArrayList<>();
        for (long t : new long[]{1, 2, 3, 4, 12}) got.add(w.allow("u1", t));
        expect("window basic", got, List.of(true, true, true, false, true));

        BucketLimiter b = new BucketLimiter(2, 0.001);       // 2 tokens, 1 per second
        got = new ArrayList<>();
        for (int i = 0; i < 4; i++) got.add(b.allow("u1", 1000));
        got.add(b.allow("u1", 2000));                          // 1 s later → 1 token back
        expect("bucket basic", got, List.of(true, true, false, false, true));

        // Concurrency: 64 threads × 1,000 calls on ONE key at the same timestamp.
        // Exactly `limit` (or `capacity`) must be allowed — never more.
        expect("window 64 threads, same key", hammer(new WindowLimiter(1_000, 50)::allow), 50);
        expect("bucket 64 threads, same key", hammer(new BucketLimiter(50, 0.0)::allow), 50);
    }

    private interface Allow { boolean allow(String key, long now); }

    private static int hammer(Allow limiter) throws Exception {
        int threads = 64, callsPerThread = 1_000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger allowed = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                start.await();
                for (int j = 0; j < callsPerThread; j++) {
                    if (limiter.allow("hot", 0)) allowed.incrementAndGet();
                }
                return null;
            }));
        }
        start.countDown();
        for (Future<?> f : futures) f.get();
        pool.shutdown();
        return allowed.get();
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
