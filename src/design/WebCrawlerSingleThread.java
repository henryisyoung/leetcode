package design;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerSingleThread {
    private Set<String> CrawedURLs;
    private String URLPattern;
    private String rootURL;
    private Queue<String> queue;
    private Pattern pattern;

    public WebCrawlerSingleThread(String url){
        this.rootURL = url;
        this.CrawedURLs = new HashSet<>();
        this.queue = new LinkedList<>();
        queue.add(rootURL);
        this.URLPattern = "http[s]*://(\\w+\\.)*(\\w+)";
        this.pattern = Pattern.compile(URLPattern);
    }

    public void bfsCrawURLs() throws IOException {
        while (!queue.isEmpty()) {
            String curURL = queue.poll();
            if (CrawedURLs.size() > 100) return;

            boolean OK = true;
            URL url;
            BufferedReader br = null;
            while (OK) {
                try {
                    url = new URL(curURL);
                    br = new BufferedReader(new InputStreamReader(url.openStream()));
                    OK = false;
                } catch (MalformedURLException e) {
                    System.out.println("\nMalformedURL : "+ curURL +"\n");
                    curURL = queue.poll();
                    OK = true;
                } catch (IOException e) {
                    System.out.println("\nIOException for URL : " + curURL + "\n");
                    curURL = queue.poll();
                    OK = true;
                }
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                String next = matcher.group();
                if (!CrawedURLs.contains(next)) {
                    CrawedURLs.add(next);
                    queue.add(next);
                }
            }
        }
    }
    //Display results from SET marked
    public void displayResults(){
        System.out.println("\n\nResults: ");
        System.out.println("\nWeb sites crawled : "+ CrawedURLs.size()+"\n");
//        for(String s : CrawedURLs){
//            System.out.println(s);
//        }
    }

    public static void main(String[] args) throws IOException {
        WebCrawlerSingleThread crawler = new WebCrawlerSingleThread("https://www.cnn.com/");
        long start = System.currentTimeMillis();
        crawler.bfsCrawURLs();
        long end = System.currentTimeMillis();
        System.out.println("Spend " + (end - start) / 1000.0 + " seconds");
        crawler.displayResults();
    }
}
