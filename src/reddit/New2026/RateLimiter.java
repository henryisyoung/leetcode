package reddit.New2026;

import java.util.*;

/** Token bucket rate limiter — one bucket per key, refilled lazily on each call so there
 *  are no timers and idle keys cost nothing. allow() is O(1): one map lookup, one clamped
 *  refill, one decrement. Tokens are a double because an int refill would truncate to 0
 *  whenever ratePerMs < 1 and the bucket would never refill at all. */
public class RateLimiter {
    public static class Bucket {
        double tokens;
        long lastUpdated;

        public Bucket(double tokens, long lastUpdated) {
            this.tokens = tokens;
            this.lastUpdated = lastUpdated;
        }
    }

    public static class BucketLimiter {
        Map<String, Bucket> buckets = new HashMap<>();
        double ratePerMs;
        double capacity;

        public BucketLimiter(double ratePerMs, double capacity) {
            this.capacity = capacity;
            this.ratePerMs = ratePerMs;
        }

        public boolean allow(String req, long timestamp) {
            Bucket bucket = buckets.computeIfAbsent(req, k -> new Bucket(capacity, timestamp));

            long elapsed = Math.max(0, timestamp - bucket.lastUpdated);
            double newTokens = elapsed * ratePerMs;
            bucket.tokens = Math.min(capacity, bucket.tokens + newTokens);
            bucket.lastUpdated = Math.max(bucket.lastUpdated, timestamp);

            System.out.println(bucket.tokens);
            if (bucket.tokens < 1.0) {
                return false;
            }
            bucket.tokens--;

            return true;
        }
    }


    public static class WindowLimiter {
        Map<String, Deque<Long>> buckets = new HashMap<>();
        long window;
        double limit;

        public WindowLimiter(long window, double limit) {
            this.window = window;
            this.limit = limit;
        }

        public boolean allow(String req, long timestamp) {
            Deque<Long> queue = buckets.computeIfAbsent(req, k -> new ArrayDeque<>());

            while (!queue.isEmpty() && (timestamp - queue.peekFirst() >= window)) {
                queue.pollFirst();
            }

            if (queue.size() >= limit) {
                return false;
            }

            queue.add(timestamp);
            return true;
        }

    }

    public static void main(String[] args) {
//        BucketLimiter limiter = new BucketLimiter(1.0, 2.0);
//        System.out.println(limiter.allow("req1", 100));
//        System.out.println(limiter.allow("req1", 101));
//        System.out.println(limiter.allow("req1", 102));
//        System.out.println(limiter.allow("req1", 102));
//        System.out.println(limiter.allow("req1", 102));

        WindowLimiter limiter = new WindowLimiter(2l,2.0);
        System.out.println(limiter.allow("req1", 100));
        System.out.println(limiter.allow("req1", 101));
        System.out.println(limiter.allow("req1", 102));
        System.out.println(limiter.allow("req1", 102));
        System.out.println(limiter.allow("req1", 102));
    }
}
