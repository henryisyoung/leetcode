package design;
//https://blog.csdn.net/haitao111313/article/details/7526313

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerMultiThreadsExecutors {
    private Set<String> crawedURLs;
    private String URLPattern;
    private String rootURL;

    private Pattern pattern;
    private final int threadsCount;
    ExecutorService exec;
    List<Future<List<String>>> futures;
    public WebCrawlerMultiThreadsExecutors(String url, int threads){
        this.threadsCount = threads;
        this.rootURL = url;
        this.crawedURLs = Collections.synchronizedSet(new HashSet<>());
        this.URLPattern = "http[s]*://(\\w+\\.)*(\\w+)";
        this.pattern = Pattern.compile(URLPattern);
        this.exec = Executors.newFixedThreadPool(threadsCount);
    }

    public void beginCrawl() {
        String cur = rootURL;
        crawlURL(cur);
        while (canCrawlMore());
    }

    private boolean canCrawlMore() {
        List<String> newURLs = new ArrayList<>();
        Iterator<Future<List<String>>> iterator = futures.iterator();
        while (iterator.hasNext()) {
            Future<List<String>> future = iterator.next();
            if (future.isDone()) {
                iterator.remove();
                try {
                    newURLs.addAll(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String u : newURLs) {
            crawlURL(u);
        }
        return futures.size() != 0;
    }

    private void crawlURL(String url) {
        if (crawedURLs.contains(url)) return;
        crawedURLs.add(url);
        Crawler crawler = new Crawler(url);
        Future<List<String>> future = exec.submit(crawler);
        futures.add(future);
    }

    private List<String> getUrls(String sUrl) {
        URL url;
        List<String> result = new ArrayList<>();
        try {
            url = new URL(sUrl);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer sb = new StringBuffer();//sb为爬到的网页内容
            String rLine = null;
            while((rLine=bReader.readLine())!=null){
                sb.append(rLine);
            }

            System.out.println("爬网页"+sUrl+"成功 "+" 是由线程"+Thread.currentThread().getName()+"来爬");
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                String next = matcher.group();
                if (!crawedURLs.contains(next)) {
                    result.add(next);
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("\nMalformedURL : "+ sUrl +Thread.currentThread().getName() + " " + "\n");
        } catch (IOException e) {
            System.out.println("\nIOException for URL : " + sUrl + " " +Thread.currentThread().getName() + "\n");
        }
        return result;
    }

    class Crawler implements Callable<List<String>> {
        String url;
        public Crawler(String url) {
            this.url = url;
        }
        @Override
        public List<String> call() throws Exception {
            return getUrls(url);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

    }
}
