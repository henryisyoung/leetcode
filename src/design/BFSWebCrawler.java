package design;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * WebCrawler
 * <p/>
 * Using TrackingExecutorService to save unfinished tasks for later execution
 * The multi-threaded crawler is using BFS
 */
public abstract class BFSWebCrawler {
    private static final long SEARCH_TIME = 1000;
    public static final String SEARCH = "Honorificabilitudinitatibus";
    public static final String startpage = "http://xyzcode.blogspot.com/2017/08/test-crawler.html";

    private static final Predicate<String> urlPred = i -> i.contains("html")
            && i.contains("xyzcode");

    private volatile TrackingExecutor exec;
    private final Set<String> urlsToCrawl = new ConcurrentSkipListSet<>();

    private final ConcurrentMap<String, Boolean> seen = new ConcurrentHashMap<>();
    private final static ConcurrentMap<String, Integer> found = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> problem = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 10;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private volatile static long Delay = 100;
    private static final long LIMIT = 5000;  //max delay time

    public static void main(String... args) {
        try {
            BFSWebCrawler crawler = new BFSWebCrawler(startpage) {
                @Override
                protected Set<String> processPage(String url) {
                    if (url != null) {
                        Set<String> urls = getContainedUrls(url);
                        return urls;
                    }
                    return new HashSet<String>();
                }
            };

            System.out.println("Will finish searching " + SEARCH
                    + " start from " + startpage + " in: " + SEARCH_TIME / 1000
                    + " seconds");

            crawler.start();
            Thread.sleep(SEARCH_TIME);
            crawler.stop();

            System.out.println("url visited: " + crawler.getSeenURLs().size());
            System.out.println("occurance of "+ SEARCH + ": "+ found.values().stream()
                    .mapToInt(i -> i.intValue()).sum());
            System.out.println(found.keySet());
        } catch (Exception e) {
            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt();
        }

    }

    //equivalent to graph API: Bag<Vertex> adjacentTo(Vertex v);
    private static Set<String> getContainedUrls(String url) {
        String line;
        Set<String> urls = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream())) ){
            while ((line = br.readLine()) != null) {
                Matcher m = Pattern.compile(SEARCH).matcher(line);  //waste memory, yes, but thread safe
                while (m.find())
                    found.merge(url, 1, (v1, v2) -> v1 = v1 + 1);
                urls.addAll(new HTMLLinkExtractor().grabHTMLLinks(line)
                        .stream().filter(urlPred).collect(Collectors.toSet()));
            }
        } catch (IOException ioe) {
            if (ioe.getMessage().contains(
                    "Server returned HTTP response code: 503")) {
                Delay = Delay < LIMIT ? Delay * 2 : LIMIT;
                try {
                    Thread.sleep(Delay);
                } catch (InterruptedException e) {
                }
            }
        }
        return urls;
    }

    public BFSWebCrawler(String startUrl) {
        urlsToCrawl.add(startUrl);
    }

    public final Set<String> getSeenURLs() {
        return this.seen.keySet();
    }

    public final Map<String, Integer> getProblemURLs() {
        return this.problem;
    }

    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (String url : urlsToCrawl)
            submitCrawlTask(url.toString());
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(TIMEOUT, UNIT))
                saveUncrawled(exec.getCancelledTasks());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // exec = null;
        }
    }

    protected abstract Set<String> processPage(String url);

    private void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable task : uncrawled)
            urlsToCrawl.add(((CrawlTask) task).getPage());
    }

    private void submitCrawlTask(String u) {
        try {
            Thread.sleep(Delay);
            exec.execute(new CrawlTask(u));
        } catch (Exception e) {
            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt();
        }
    }

    private class CrawlTask implements Runnable {
        private final String url;

        CrawlTask(String url) {
            this.url = url;
        }

        boolean alreadyCrawled() {
            return seen.putIfAbsent(url, true) != null;
        }

        void markUncrawled() {
            seen.remove(url);
        }

        public void run() {
            if (alreadyCrawled()) {
                return;
            }
            Set<String> list = processPage(url);
            if (url.toString().contains("xyzcode")
                    && url.toString().contains("html") && list.size() == 0) {
                markUncrawled();
                problem.merge(url.toString(), 1, (v1, v2) -> v1 = v1 + 1);
                return;
            }
            for (String link : list) {
                if (Thread.currentThread().isInterrupted())
                    return;
                //BFS search
                submitCrawlTask(link);
            }
        }

        public String getPage() {
            return url;
        }
    }
}

class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtShutdown = Collections
            .synchronizedSet(new HashSet<Runnable>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    public void shutdown() {
        exec.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    public boolean isShutdown() {
        return exec.isShutdown();
    }

    public boolean isTerminated() {
        return exec.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }

    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated())
            throw new IllegalStateException(/* ... */);
        return new ArrayList<Runnable>(tasksCancelledAtShutdown);
    }

    public void execute(final Runnable runnable) {
        exec.execute(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } finally {
                    if (isShutdown() && Thread.currentThread().isInterrupted())
                        tasksCancelledAtShutdown.add(runnable);
                }
            }
        });
    }
}

class HTMLLinkExtractor {

    private Pattern patternTag, patternLink;
    private Matcher matcherTag, matcherLink;

    private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
    private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

    public HTMLLinkExtractor() {
        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
        patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
    }

    /**
     * Extract href String with regular expression
     *
     */
    public Set<String> grabHTMLLinks(final String html) {
        Set<String> result = new HashSet<String>();
        matcherTag = patternTag.matcher(html);
        while (matcherTag.find()) {
            String href = matcherTag.group(1); // href
            matcherLink = patternLink.matcher(href);
            while (matcherLink.find()) {
                String link = matcherLink.group(1); // link
                link = link.replaceAll("'", "");
                link = link.replaceAll("\"", "");
                result.add(link);
            }
        }
        return result;
    }

}
