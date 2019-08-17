package design;
//https://blog.csdn.net/haitao111313/article/details/7526313
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerMultiThreads {
    private Set<String> CrawedURLs;
    private String URLPattern;
    private String rootURL;
    private Queue<String> queue;
    private Pattern pattern;
    private final int threadsCount;
    private int WaitingThreadsCount = 0;
    volatile boolean running = true;
    public static final Object lock = new Object();   //线程间通信变量

    public WebCrawlerMultiThreads(String url, int threads){
        this.threadsCount = threads;
        this.rootURL = url;
        this.CrawedURLs = Collections.synchronizedSet(new HashSet<>());

        this.queue = new ConcurrentLinkedQueue<>();
        queue.add(rootURL);
        this.URLPattern = "http[s]*://(\\w+\\.)*(\\w+)";
        this.pattern = Pattern.compile(URLPattern);
    }

    public void beginCrawl() {
        for (int i = 0; i < threadsCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        String curUrl = getURL();
                        if (curUrl != null) {
                            crawlURL(curUrl);
                        } else {
                            synchronized(lock) {  //------------------（2）
                                try {
                                    WaitingThreadsCount++;
                                    System.out.println("当前有" + WaitingThreadsCount + "个线程在等待");
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            },"thread-" + i).start();
        }
    }

    private void crawlURL(String sUrl) {
        URL url;
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
                if (!CrawedURLs.contains(next)) {
                    CrawedURLs.add(next);
                    queue.add(next);
                    if(WaitingThreadsCount > 0){ //如果有等待的线程，则唤醒
                        synchronized(lock) {
                            WaitingThreadsCount--;
                            lock.notify();
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("\nMalformedURL : "+ sUrl +Thread.currentThread().getName() + " " + "\n");
        } catch (IOException e) {
            System.out.println("\nIOException for URL : " + sUrl + " " +Thread.currentThread().getName() + "\n");
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
        for(String s : CrawedURLs){
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        WebCrawlerMultiThreads crawler = new WebCrawlerMultiThreads("https://blog.csdn.net/", 10);
        long start = System.currentTimeMillis();
        System.out.println("Now beginning crawl csdb website........." );
        crawler.beginCrawl();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        crawler.running = false;
        long end =  System.currentTimeMillis();
        System.out.println("总共爬了"+ crawler.CrawedURLs.size()+"个网页");
        System.out.println("总共耗时"+(end-start)/1000+"秒");
        crawler.displayResults();
        System.exit(1);
    }
}
