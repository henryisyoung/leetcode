package snowflake.mianjing;
/*
Overview
You have access to these two helper functions:

fetch_page(url) -> str: Gets the text content of a web page.
parse_urls(content) -> list[str]: Finds all links on that page.
Your task is to design a system to visit these links. You must also handle errors properly.

The interview has three parts:

Crawl URLs on a single website.
Scale the solution for large systems.
Handle API errors robustly.
Phase 1: Basic Crawling
The Goal
Start at a given start_url. Find all other URLs you can reach that belong to the same domain.

Rules:

Same Domain: Only visit links where the hostname is the same as the start_url.
No Duplicates: Do not process the same URL twice.
Tools: Use fetch_page and parse_urls.
You can use BFS (Breadth-First Search) or DFS (Depth-First Search). The solution below uses BFS.

Phase 2: Scaling Up
The Challenge
What if the website is huge and has millions of URLs? The basic approach will be too slow.

Key Concepts
Concurrency: Use a "worker pool" with a limit. This lets you process multiple pages at once without crashing your machine.
Thread Safety: Make sure multiple threads check for duplicates safely.
Network: Use multithreading or async I/O because waiting for the internet is the slowest part.
Distributed Systems: If one machine is not enough, split the work across many machines based on URL hash.

Phase 3: Handling Failures
The Problem
Sometimes fetch_page or parsing fails. This might happen due to timeouts, server errors (5xx), or temporary blocks. How do you stop the crawler from crashing?

Phase 3 Code
We use "exponential backoff." This means we retry, but wait longer after each failure. We also add "jitter" (randomness) so threads don't all retry at the exact same split second.

 Important Tips
Transient Errors Only: Only retry temporary errors (like timeouts). Do not retry if the URL is broken or invalid.
Circuit Breakers: If a website fails too often, stop hitting it for a while.
Dead-Letter Queue: If a URL fails after all retries, save it to a special list (dead-letter queue) to inspect later.
Phase 3 Analysis
Big-O: Time and space complexity stay the same.
Latency: Retrying takes time, so the system will be slower when errors occur. Throughput depends on how often failures happen.
 */


import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WebURLCrawleratScale {

    /*
     * The two helpers given by the interviewer.
     *   fetchPage(url)     -> raw page content
     *   parseUrls(content) -> all links found on the page
     * In a real interview we'd be told to assume both exist; here we make it
     * an interface so we can plug in a mock for testing.
     */
    public interface HtmlParser {
        String fetchPage(String url);
        List<String> parseUrls(String content);
    }

    /*
     * Phase 1: Basic Crawling (single machine, no concurrency).
     *
     * Approach (BFS):
     *   1. Extract the hostname of startUrl. Only links sharing this hostname
     *      are in scope.
     *   2. Maintain a `visited` HashSet to dedupe URLs (no URL is fetched twice).
     *   3. Maintain a FIFO queue of URLs to visit, seeded with startUrl.
     *   4. Pop a URL, fetchPage, parseUrls, and for every discovered link:
     *        - skip if already visited,
     *        - skip if not on the same hostname,
     *        - otherwise mark visited and enqueue.
     *   5. Return the visited set as a list when the queue drains.
     *
     * Time:  O(V + E), where V = number of pages on the same domain and
     *                   E = total outbound links on those pages.
     * Space: O(V) for the visited set + queue.
     */
    public List<String> crawl(String startUrl, HtmlParser parser) {
        String host = hostOf(startUrl);
        if (host == null) return new ArrayList<>();

        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        visited.add(startUrl);
        queue.offer(startUrl);

        while (!queue.isEmpty()) {
            String url = queue.poll();
            String content = parser.fetchPage(url);
            if (content == null) continue;

            for (String next : parser.parseUrls(content)) {
                if (visited.contains(next)) continue;
                if (!host.equals(hostOf(next))) continue;
                visited.add(next);
                queue.offer(next);
            }
        }
        return new ArrayList<>(visited);
    }

    /*
     * Hostname extractor. Returns null for malformed URLs so we can
     * defensively skip them without crashing the crawl.
     */
    private static String hostOf(String url) {
        if (url == null) return null;
        try {
            return URI.create(url).getHost();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /*
     * Phase 3: Handle transient fetch failures with retry + exponential
     * backoff + jitter. Mirrors the standard Python pattern:
     *
     *   sleep = base * (2 ** attempt)
     *   sleep *= 0.5 + random()           # jitter so threads don't sync up
     *
     * Returns the page content on success, or rethrows the last exception
     * after `maxAttempts` failures (caller decides what to do — usually
     * skip the URL).
     */
    private static String fetchWithRetry(HtmlParser parser, String url, int maxAttempts) {
        long baseMs = 200;
        RuntimeException last = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return parser.fetchPage(url);
            } catch (RuntimeException e) {
                last = e;
                if (attempt == maxAttempts - 1) break;
                long sleepMs = baseMs * (1L << attempt);
                sleepMs = (long) (sleepMs * (0.5 + ThreadLocalRandom.current().nextDouble()));
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw last;
    }

    /*
     * Phase 2: Scaling Up — thread pool with a queue of in-flight Futures.
     *
     * Idea: each task fetches one page and returns its parsed links. The main
     * thread drains a queue of Futures; whenever a future's links contain a
     * NEW same-domain URL, we submit a new task for it. When the queue is
     * empty, every page is done.
     *
     * Why this is simple:
     *   - No AtomicInteger / monitor / notify dance — the queue length itself
     *     tracks "work outstanding".
     *   - `visited.add(url)` on a ConcurrentHashMap.newKeySet() returns true
     *     only for the FIRST thread to claim a URL, so we never submit it twice.
     *
     * Time: O(V + E). Wall-clock drops by ~threads for I/O-bound work.
     *
     * If "many machines": shard by hash(host); replace the in-process visited
     * set with Redis (SETNX). Out of scope for this implementation.
     */
    public List<String> crawlConcurrent(String startUrl, HtmlParser parser, int threads) {
        String host = hostOf(startUrl);
        if (host == null) return new ArrayList<>();

        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        Deque<Future<List<String>>> tasks = new ArrayDeque<>();
        tasks.add(pool.submit(() -> fetchAndParse(parser, startUrl)));

        while (!tasks.isEmpty()) {
            try {
                for (String next : tasks.poll().get()) {
                    if (host.equals(hostOf(next)) && visited.add(next)) {
                        tasks.add(pool.submit(() -> fetchAndParse(parser, next)));
                    }
                }
            } catch (Exception e) {
                // One bad page shouldn't sink the crawl.
            }
        }
        pool.shutdown();
        return new ArrayList<>(visited);
    }

    private static List<String> fetchAndParse(HtmlParser parser, String url) {
        try {
            String content = fetchWithRetry(parser, url, 5);
            return content == null ? new ArrayList<>() : parser.parseUrls(content);
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
    }

    /* ------------------------------------------------------------------
     * In-memory mock parser for a quick smoke test.
     * ------------------------------------------------------------------ */
    private static final class MockParser implements HtmlParser {
        private final Map<String, List<String>> graph;
        MockParser(Map<String, List<String>> graph) { this.graph = graph; }
        public String fetchPage(String url) { return graph.containsKey(url) ? url : null; }
        public List<String> parseUrls(String content) {
            return graph.getOrDefault(content, new ArrayList<>());
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> site = new HashMap<>();
        site.put("http://example.com/a", List.of(
                "http://example.com/b",
                "http://example.com/c",
                "http://other.com/x"));        // off-domain, must be skipped
        site.put("http://example.com/b", List.of(
                "http://example.com/a",        // already visited
                "http://example.com/d"));
        site.put("http://example.com/c", List.of(
                "http://example.com/d",
                "http://example.com/e"));
        site.put("http://example.com/d", List.of());
        site.put("http://example.com/e", List.of("http://example.com/a"));
        site.put("http://other.com/x",   List.of("http://other.com/y"));

        WebURLCrawleratScale crawler = new WebURLCrawleratScale();

        long t0 = System.currentTimeMillis();
        List<String> serial = crawler.crawl("http://example.com/a", new SlowParser(site, 100));
        long t1 = System.currentTimeMillis();
        serial.sort(String::compareTo);
        System.out.println("Serial    : " + serial + " in " + (t1 - t0) + " ms");

        long t2 = System.currentTimeMillis();
        List<String> concurrent = crawler.crawlConcurrent(
                "http://example.com/a", new SlowParser(site, 100), 4);
        long t3 = System.currentTimeMillis();
        concurrent.sort(String::compareTo);
        System.out.println("Concurrent: " + concurrent + " in " + (t3 - t2) + " ms");

        // Phase 3: retry handles transient failures on /c (fails twice, then succeeds).
        FlakyParser flaky = new FlakyParser(site, Map.of("http://example.com/c", 2));
        List<String> resilient = crawler.crawlConcurrent("http://example.com/a", flaky, 4);
        resilient.sort(String::compareTo);
        System.out.println("WithRetry : " + resilient);
    }

    /* Throws RuntimeException K times per URL (per the map) then succeeds. */
    private static final class FlakyParser implements HtmlParser {
        private final HtmlParser inner;
        private final Map<String, AtomicInteger> failsLeft = new ConcurrentHashMap<>();
        FlakyParser(Map<String, List<String>> graph, Map<String, Integer> transientFails) {
            this.inner = new MockParser(graph);
            transientFails.forEach((k, v) -> failsLeft.put(k, new AtomicInteger(v)));
        }
        public String fetchPage(String url) {
            AtomicInteger left = failsLeft.get(url);
            if (left != null && left.getAndDecrement() > 0) {
                throw new RuntimeException("transient fail " + url);
            }
            return inner.fetchPage(url);
        }
        public List<String> parseUrls(String content) { return inner.parseUrls(content); }
    }

    /* Wraps a MockParser with an artificial fetch delay so the speedup of
       Phase 2 is visible. */
    private static final class SlowParser implements HtmlParser {
        private final HtmlParser inner;
        private final long delayMs;
        SlowParser(Map<String, List<String>> graph, long delayMs) {
            this.inner = new MockParser(graph);
            this.delayMs = delayMs;
        }
        public String fetchPage(String url) {
            try { Thread.sleep(delayMs); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return inner.fetchPage(url);
        }
        public List<String> parseUrls(String content) {
            return inner.parseUrls(content);
        }
    }
}
