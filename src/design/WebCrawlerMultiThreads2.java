package design;
//https://blog.csdn.net/haitao111313/article/details/7526313
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerMultiThreads2 {
    private Set<String> CrawedURLs;
    private int threadCount;
    private Queue<String> queue;
    private Object lock;
    private int waitingThreads = 0;
    private volatile boolean running = true;
    public WebCrawlerMultiThreads2(String url, int threads){
        this.threadCount = threads;
        this.queue = new ConcurrentLinkedQueue<>();
        queue.add(url);
        this.CrawedURLs = Collections.synchronizedSet(new HashSet<>());
        this.lock = new Object();
    }

    public void beginCrawl() {
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        String url = getURL();
                        if (url != null) {
                            crawlUrl(url);
                        } else {
                            synchronized (lock) {
                                try {
                                    waitingThreads++;
                                    System.out.println("there are " + waitingThreads + " threads waiting");
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }, "thread-" + i).start();
        }
    }

    private void crawlUrl(String curURL) {
        try {
            URL url = new URL(curURL);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("Crawled web "+ curURL +" successfully "+"that is from " + Thread.currentThread().getName());

            Pattern pattern = Pattern.compile("http[s]*://(\\w+\\.)*(\\w+)");
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                String next = matcher.group();
                if (!CrawedURLs.contains(next)) {
                    queue.add(next);
                    CrawedURLs.add(next);
                    if (waitingThreads > 0) {
                        synchronized (lock) {
                            waitingThreads--;
                            lock.notify();
                        }
                    }
                }
            }


        } catch (MalformedURLException e) {
            System.out.println("\nMalformedURL : "+ curURL +Thread.currentThread().getName() + " " + "\n");
        } catch (IOException e) {
            System.out.println("\nIOException for URL : " + curURL + " " +Thread.currentThread().getName() + "\n");
        }
    }

    private String getURL() {
        if (queue.isEmpty()) return null;
        return queue.poll();
    }

    //Display results from SET marked
    public void displayResults(){
        System.out.println("\n\nResults: ");
        System.out.println("\nWeb sites crawled : "+ CrawedURLs.size()+"\n");
//        for(String s : CrawedURLs){
//            System.out.println(s);
//        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        WebCrawlerMultiThreads2 crawler = new WebCrawlerMultiThreads2("https://www.cnn.com/", 10);
        long start = System.currentTimeMillis();
        System.out.println("Now beginning crawl csdb website........." );
        crawler.beginCrawl();
        try {
            TimeUnit.SECONDS.sleep(23);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        crawler.running = false;
        long end =  System.currentTimeMillis();
        System.out.println("总共爬了"+ crawler.CrawedURLs.size()+"个网页");
        System.out.println("总共耗时"+(end-start)/1000.0+"秒");
        crawler.displayResults();
        System.exit(1);
    }
}
