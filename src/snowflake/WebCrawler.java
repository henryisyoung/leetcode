package snowflake;

import java.util.*;
import java.util.concurrent.*;

public class WebCrawler {

    // The LeetCode 1242 interface — repeated here so the file compiles standalone.
    public interface HtmlParser {
        List<String> getUrls(String url);
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        String host = getHost(startUrl);

        // ConcurrentHashMap-backed set: add() is atomic and returns false on
        // duplicate, giving us race-free "claim this URL" semantics.
        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        // Workers only do blocking I/O (one getUrls call each) and return.
        // They never wait on other futures, so no risk of pool starvation.
        ExecutorService pool = Executors.newFixedThreadPool(16);
        Deque<Future<List<String>>> pending = new ArrayDeque<>();
        pending.add(pool.submit(() -> htmlParser.getUrls(startUrl)));

        try {
            // Dispatcher loop: drain finished futures, fan out new work.
            // Termination = no pending futures left == nothing else to crawl.
            while (!pending.isEmpty()) {
                Future<List<String>> f = pending.poll();
                for (String url : f.get()) {
                    if (host.equals(getHost(url)) && visited.add(url)) {
                        pending.add(pool.submit(() -> htmlParser.getUrls(url)));
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }

        return new ArrayList<>(visited);
    }

    private String getHost(String url) {
        // URLs are guaranteed to look like http://host/...  (no port, http only)
        // so splitting on '/' and taking index 2 is safe and faster than URI parsing.
        return url.split("/")[2];
    }
}
