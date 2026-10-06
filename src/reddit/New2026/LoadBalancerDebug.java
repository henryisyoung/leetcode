package reddit.New2026;

import java.util.*;

/*
================================================================================
  LoadBalancerDebug — practice for Reddit "Debug and Improve a Load Balancer"
================================================================================
  Everything runs in one process. Clock.sleep() only moves a fake clock,
  so tests run instantly but latency numbers are real.

  Run:  javac -d /tmp/lb src/reddit/New2026/LoadBalancerDebug.java
        java -cp /tmp/lb reddit.New2026.LoadBalancerDebug

  Story: we just shipped a config change. Now every request returns 500.

  Part 1  All backends look unhealthy, but sending to a server directly works.
          Find the root cause and fix it.
  Part 2  Requests are still slow sometimes. Implement LatencyStats, find
          which backend is slow and why, then fix it.
  Part 3  Implement CircuitBreaker (spec on the class).
  Part 4  Implement LatencyRouter (spec on the class).

  Keep the roles split:
    HealthChecker  = fault detection  (is the server up?)
    CircuitBreaker = admission        (should this request go there?)
    Router         = selection        (which server is best?)

  Say it out loud: what you see -> what you guess -> how you check -> fix.
================================================================================
*/
public class LoadBalancerDebug {

    static class Clock {
        private long now = 0;
        long now() { return now; }
        void sleep(long ms) { now += ms; }
    }

    static class Response {
        final int status;   // 0 = no response (connection refused)
        final String body;
        Response(int status, String body) { this.status = status; this.body = body; }
    }

    // ---------------------------------------------------------------- servers

    static class Server {
        static final long REFRESH_BACKOFF_MS = 50;

        final String name;
        final Clock clock;
        final long workMs;
        final int refreshEvery;   // refresh cache every N requests, 0 = never
        boolean broken = false;
        private int served = 0;

        Server(String name, Clock clock, long workMs, int refreshEvery) {
            this.name = name;
            this.clock = clock;
            this.workMs = workMs;
            this.refreshEvery = refreshEvery;
        }

        Response handle(String path) {
            if (path.equals("/health")) return new Response(200, "ok");
            clock.sleep(workMs);
            if (broken) return new Response(500, "internal error");
            served++;
            if (refreshEvery > 0 && served % refreshEvery == 0) refreshCache();
            return new Response(200, "hello from " + name);
        }

        private void refreshCache() {
            for (int attempt = 1; attempt <= 3; attempt++) {
                if (cacheReady(attempt)) return;
                // ===== P2 FIX =====
                // Bug: REFRESH_BACKOFF_MS is already ms, "* 1000" made it seconds:
                //   50s + 100s = 150s sleep on every 5th request to server-2.
                // Found by: per-backend p99 -> only server-2 high, only every 5th request.
                clock.sleep(REFRESH_BACKOFF_MS * attempt);
                // ===== END P2 FIX =====
            }
        }

        private boolean cacheReady(int attempt) { return attempt == 3; }
    }

    static class Network {
        private final Map<String, Server> byAddress = new HashMap<>();

        void listen(String host, int port, Server s) { byAddress.put(host + ":" + port, s); }

        Response send(String host, int port, String path) {
            Server s = byAddress.get(host + ":" + port);
            if (s == null) return new Response(0, "connection refused " + host + ":" + port);
            return s.handle(path);
        }
    }

    // ---------------------------------------------------------------- config

    static class Backend {
        final String name;
        final String host;
        final int port;
        boolean healthy = false;
        final CircuitBreaker breaker;

        Backend(String name, String host, int port, CircuitBreaker breaker) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.breaker = breaker;
        }
    }

    static List<Backend> loadBackends(Map<String, String> cfg) {
        List<Backend> list = new ArrayList<>();
        for (String name : cfg.get("backends").split(",")) {
            String host = cfg.get(name + ".host");
            int port = Integer.parseInt(cfg.get(name + ".port"));
            list.add(new Backend(name, host, port, new CircuitBreaker(5, 10_000)));
        }
        return list;
    }

    static class HealthChecker {
        final Network net;
        final Map<String, String> cfg;

        HealthChecker(Network net, Map<String, String> cfg) { this.net = net; this.cfg = cfg; }

        void checkAll(List<Backend> backends) {
            for (Backend b : backends) {
                // ===== P1 FIX =====
                // Bug: read old keys "<name>.hostname" / "<name>.health_port". The config
                //   change renamed them to "host" / "port", so getOrDefault silently fell back
                //   to "server-1:8080" -> connection refused -> every backend unhealthy -> 500.
                // Fix: probe the same host:port the LB routes to (loaded once in loadBackends).
                Response r = net.send(b.host, b.port, "/health");
                // ===== END P1 FIX =====
                b.healthy = r.status == 200;
            }
        }
    }

    // ---------------------------------------------------------------- Part 2

    /** Per-backend request latency. p99 = value at index ceil(0.99 * n) - 1 of sorted samples. */
    static class LatencyStats {
        // ===== P2 INSTRUMENT =====
        // One sample list per backend (label = backend name only, never request id,
        // so the number of series stays small). LB calls record() once per attempt.
        private final Map<String, List<Long>> samples = new HashMap<>();

        void record(String backend, long ms) {
            samples.computeIfAbsent(backend, k -> new ArrayList<>()).add(ms);
        }

        int count(String backend) {
            return samples.getOrDefault(backend, List.of()).size();
        }

        /** -1 if no samples. */
        long p99(String backend) {
            List<Long> s = samples.get(backend);
            if (s == null || s.isEmpty()) return -1;
            List<Long> sorted = new ArrayList<>(s);
            Collections.sort(sorted);
            return sorted.get((int) Math.ceil(0.99 * sorted.size()) - 1);
        }

        long max(String backend) {
            return samples.getOrDefault(backend, List.of()).stream().mapToLong(x -> x).max().orElse(-1);
        }

        void print() {
            for (String b : new TreeSet<>(samples.keySet()))
                System.out.printf("  %-9s count=%-4d p99=%-7d max=%d%n", b, count(b), p99(b), max(b));
        }
        // ===== END P2 INSTRUMENT =====
    }

    // ---------------------------------------------------------------- Part 3

    /*
      CLOSED    : allow all. `failureThreshold` failures in a row -> OPEN.
      OPEN      : reject all until `cooldownMs` passed since it opened.
      HALF_OPEN : after cooldown, the FIRST allowRequest() gets true (the probe).
                  Every other call gets false until the probe reports back.
                  probe success -> CLOSED, probe failure -> OPEN (new cooldown).
      Any success in CLOSED resets the failure count.
    */
    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }

        final int failureThreshold;
        final long cooldownMs;

        CircuitBreaker(int failureThreshold, long cooldownMs) {
            this.failureThreshold = failureThreshold;
            this.cooldownMs = cooldownMs;
        }

        // ===== P3 CIRCUIT BREAKER =====
        // synchronized = check + change state is one step. After cooldown, many threads
        // may call allowRequest at once; only the first sees OPEN and flips it to
        // HALF_OPEN, so exactly one probe goes out. The rest see HALF_OPEN -> false.
        private State state = State.CLOSED;
        private int failures = 0;
        private long openedAt = 0;

        synchronized boolean allowRequest(long now) {
            if (state == State.CLOSED) return true;
            if (state == State.OPEN && now - openedAt >= cooldownMs) {
                state = State.HALF_OPEN;
                return true;
            }
            return false;
        }

        synchronized void onSuccess() {
            state = State.CLOSED;
            failures = 0;
        }

        synchronized void onFailure(long now) {
            failures++;
            if (state == State.HALF_OPEN || failures >= failureThreshold) {
                state = State.OPEN;
                openedAt = now;
            }
        }

        synchronized State state() {
            return state;
        }
        // ===== END P3 CIRCUIT BREAKER =====
    }

    // ---------------------------------------------------------------- routing

    interface Router {
        Backend pick(List<Backend> candidates);
        default void record(Backend b, long ms, boolean ok) {}
    }

    static class RoundRobinRouter implements Router {
        private int next = 0;

        public Backend pick(List<Backend> candidates) {
            return candidates.get(next++ % candidates.size());
        }
    }

    /*
      Part 4. Prefer the backend with the lowest EWMA latency.
        - ewma = 0.3 * sample + 0.7 * ewma   (first sample: ewma = sample)
        - a backend with no samples yet is picked first (cold start)
        - every 10th pick: rotate through candidates instead (exploration,
          so a backend that was slow once can win again)
        - a failed request counts as max(ms, 1000) (fast failures are not fast)
    */
    static class LatencyRouter implements Router {
        // ===== P4 LEAST LATENCY =====
        // EWMA, not lifetime average: reacts to change, one double per backend.
        // Exploration every 10th pick: otherwise a backend that was slow once is
        // never picked again, so its estimate never gets a chance to improve.
        private final Map<Backend, Double> ewma = new HashMap<>();
        private int picks = 0;

        public synchronized Backend pick(List<Backend> candidates) {
            picks++;
            if (picks % 10 == 0) return candidates.get((picks / 10) % candidates.size());
            Backend best = null;
            for (Backend b : candidates) {
                if (!ewma.containsKey(b)) return b;
                if (best == null || ewma.get(b) < ewma.get(best)) best = b;
            }
            return best;
        }

        public synchronized void record(Backend b, long ms, boolean ok) {
            double sample = ok ? ms : Math.max(ms, 1000);
            Double old = ewma.get(b);
            ewma.put(b, old == null ? sample : 0.3 * sample + 0.7 * old);
        }
        // ===== END P4 LEAST LATENCY =====
    }

    // ---------------------------------------------------------------- LB

    static class LoadBalancer {
        final List<Backend> backends;
        final Network net;
        final Clock clock;
        final Router router;
        final LatencyStats stats = new LatencyStats();

        LoadBalancer(List<Backend> backends, Network net, Clock clock, Router router) {
            this.backends = backends;
            this.net = net;
            this.clock = clock;
            this.router = router;
        }

        Response handle(String path) {
            List<Backend> candidates = new ArrayList<>();
            for (Backend b : backends) if (b.healthy) candidates.add(b);

            while (!candidates.isEmpty()) {
                Backend b = router.pick(candidates);
                if (!b.breaker.allowRequest(clock.now())) {
                    candidates.remove(b);
                    continue;
                }
                long start = clock.now();
                Response r = net.send(b.host, b.port, path);
                long ms = clock.now() - start;
                boolean ok = r.status != 0 && r.status < 500;

                stats.record(b.name, ms);
                if (ok) b.breaker.onSuccess(); else b.breaker.onFailure(clock.now());
                router.record(b, ms, ok);
                return r;
            }
            return new Response(500, "no healthy backend");
        }
    }

    // ---------------------------------------------------------------- setup

    static class World {
        final Clock clock = new Clock();
        final Network net = new Network();
        final Map<String, Server> servers = new LinkedHashMap<>();
        final Map<String, String> cfg = new HashMap<>();
        final List<Backend> backends;
        final HealthChecker checker;

        World(long... workMs) {
            List<String> names = new ArrayList<>();
            for (int i = 0; i < workMs.length; i++) {
                String name = "server-" + (i + 1);
                String host = "10.0.0." + (i + 1);
                int port = 8081 + i;
                Server s = new Server(name, clock, workMs[i], name.equals("server-2") ? 5 : 0);
                net.listen(host, port, s);
                servers.put(name, s);
                names.add(name);
                cfg.put(name + ".host", host);
                cfg.put(name + ".port", String.valueOf(port));
            }
            cfg.put("backends", String.join(",", names));
            backends = loadBackends(cfg);
            checker = new HealthChecker(net, cfg);
            checker.checkAll(backends);
        }

        LoadBalancer lb(Router router) { return new LoadBalancer(backends, net, clock, router); }

        Backend backend(String name) {
            for (Backend b : backends) if (b.name.equals(name)) return b;
            throw new IllegalArgumentException(name);
        }
    }

    // ---------------------------------------------------------------- tests

    static int pass = 0, fail = 0;

    static void check(String name, Runnable test) {
        try {
            test.run();
            pass++;
            System.out.println("PASS  " + name);
        } catch (Throwable e) {
            fail++;
            System.out.println("FAIL  " + name + "  -> " + e.getMessage());
        }
    }

    static void expect(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check("P1 direct request to a server works", () -> {
            World w = new World(10, 10, 10);
            Response r = w.net.send("10.0.0.1", 8081, "/feed");
            expect(r.status == 200, "status " + r.status);
        });

        check("P1 all backends healthy after health check", () -> {
            World w = new World(10, 10, 10);
            for (Backend b : w.backends) expect(b.healthy, b.name + " unhealthy");
        });

        check("P1 LB returns 200", () -> {
            World w = new World(10, 10, 10);
            LoadBalancer lb = w.lb(new RoundRobinRouter());
            for (int i = 0; i < 30; i++) {
                Response r = lb.handle("/feed");
                expect(r.status == 200, "request " + i + ": " + r.status + " " + r.body);
            }
        });

        System.out.println("P2 debug: latency per backend after 300 requests");
        World dbg = new World(10, 10, 10);
        LoadBalancer dbgLb = dbg.lb(new RoundRobinRouter());
        for (int i = 0; i < 300; i++) dbgLb.handle("/feed");
        dbgLb.stats.print();

        check("P2 stats count every request per backend", () -> {
            World w = new World(10, 10, 10);
            LoadBalancer lb = w.lb(new RoundRobinRouter());
            for (int i = 0; i < 30; i++) lb.handle("/feed");
            for (Backend b : w.backends)
                expect(lb.stats.count(b.name) == 10, b.name + " count " + lb.stats.count(b.name));
            expect(lb.stats.p99("server-1") == 10, "server-1 p99 " + lb.stats.p99("server-1"));
            expect(lb.stats.p99("nobody") == -1, "p99 with no samples should be -1");
        });

        check("P2 no backend has p99 over 1s", () -> {
            World w = new World(10, 10, 10);
            LoadBalancer lb = w.lb(new RoundRobinRouter());
            for (int i = 0; i < 300; i++) lb.handle("/feed");
            for (Backend b : w.backends) {
                long p99 = lb.stats.p99(b.name);
                expect(p99 >= 0 && p99 < 1000, b.name + " p99 " + p99 + "ms");
            }
        });

        check("P3 breaker opens after 5 failures in a row", () -> {
            CircuitBreaker cb = new CircuitBreaker(5, 10_000);
            for (int i = 0; i < 4; i++) cb.onFailure(0);
            expect(cb.state() == CircuitBreaker.State.CLOSED, "opened too early");
            cb.onFailure(0);
            expect(cb.state() == CircuitBreaker.State.OPEN, "state " + cb.state());
            expect(!cb.allowRequest(9_999), "allowed during cooldown");
        });

        check("P3 success resets the failure count", () -> {
            CircuitBreaker cb = new CircuitBreaker(5, 10_000);
            for (int i = 0; i < 4; i++) cb.onFailure(0);
            cb.onSuccess();
            for (int i = 0; i < 4; i++) cb.onFailure(0);
            expect(cb.state() == CircuitBreaker.State.CLOSED, "state " + cb.state());
        });

        check("P3 only one probe after cooldown", () -> {
            CircuitBreaker cb = new CircuitBreaker(5, 10_000);
            for (int i = 0; i < 5; i++) cb.onFailure(0);
            expect(cb.allowRequest(10_000), "probe not allowed");
            expect(cb.state() == CircuitBreaker.State.HALF_OPEN, "state " + cb.state());
            expect(!cb.allowRequest(10_001), "second probe allowed");
            cb.onSuccess();
            expect(cb.state() == CircuitBreaker.State.CLOSED, "state " + cb.state());
            expect(cb.allowRequest(10_002), "closed but rejected");
        });

        check("P3 failed probe opens again with new cooldown", () -> {
            CircuitBreaker cb = new CircuitBreaker(5, 10_000);
            for (int i = 0; i < 5; i++) cb.onFailure(0);
            expect(cb.allowRequest(10_000), "probe not allowed");
            cb.onFailure(10_000);
            expect(cb.state() == CircuitBreaker.State.OPEN, "state " + cb.state());
            expect(!cb.allowRequest(19_999), "allowed during new cooldown");
            expect(cb.allowRequest(20_000), "probe not allowed after new cooldown");
        });

        check("P3 LB stops sending to a broken server", () -> {
            World w = new World(10, 10, 10);
            w.servers.get("server-3").broken = true;
            LoadBalancer lb = w.lb(new RoundRobinRouter());
            int ok = 0;
            for (int i = 0; i < 60; i++) if (lb.handle("/feed").status == 200) ok++;
            expect(lb.stats.count("server-3") == 5, "server-3 got " + lb.stats.count("server-3"));
            expect(ok == 55, "ok " + ok);
        });

        check("P4 most traffic goes to the fastest backend", () -> {
            World w = new World(10, 50, 30);
            LoadBalancer lb = w.lb(new LatencyRouter());
            for (int i = 0; i < 200; i++) lb.handle("/feed");
            int fast = lb.stats.count("server-1");
            expect(fast >= 160, "server-1 got " + fast + "/200");
            for (Backend b : w.backends) expect(lb.stats.count(b.name) > 0, b.name + " starved");
        });

        check("P4 backend gets traffic back after it recovers", () -> {
            World w = new World(30, 10, 10);
            w.backend("server-2").healthy = false;
            Server s3 = w.servers.get("server-3");
            LoadBalancer lb = w.lb(new LatencyRouter());
            s3.broken = true;
            for (int i = 0; i < 40; i++) lb.handle("/feed");
            s3.broken = false;
            int before = lb.stats.count("server-3");
            for (int i = 0; i < 400; i++) lb.handle("/feed");
            int gained = lb.stats.count("server-3") - before;
            expect(gained > 100, "server-3 got " + gained + "/400 after recovery");
        });

        check("P4 failures count as slow", () -> {
            World w = new World(10, 10);
            LatencyRouter router = new LatencyRouter();
            Backend s1 = w.backend("server-1"), s2 = w.backend("server-2");
            router.record(s1, 1, false);
            router.record(s2, 50, true);
            int s2Picks = 0;
            for (int i = 0; i < 9; i++) if (router.pick(w.backends) == s2) s2Picks++;
            expect(s2Picks >= 8, "server-2 picked " + s2Picks + "/9");
        });

        System.out.println("\n" + pass + " passed, " + fail + " failed");
    }
}
