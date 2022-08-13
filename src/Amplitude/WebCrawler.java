package Amplitude;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    public static void main(String[] args) {
        HtmlParser htmlParser = new HtmlParser();
        String startUrl = "https://kalacloud.com/blog/architect-design-based-on-rbac-kalacloud/";
        System.out.println(crawl(startUrl, htmlParser));
    }
    public static Set<String> crawl(String startUrl, HtmlParser htmlParser) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(startUrl);
        visited.add(startUrl);

        while (!queue.isEmpty() && visited.size() < 100) {
            String url = queue.poll();
            for (String nextUrl : htmlParser.getUrls(url, visited)) {
                if (!visited.contains(nextUrl)) {
                    visited.add(nextUrl);
                    queue.add(nextUrl);
                }
            }
        }
        return visited;
    }

    static class HtmlParser {
        public HtmlParser() {

        }

        public List<String> getUrls(String s, Set<String> visited) {
            System.setProperty("sun.net.client.defaultConnectTimeout", "500");
            System.setProperty("sun.net.client.defaultReadTimeout",    "1000");
            String rawHTML = "";
            List<String> result = new ArrayList<>();
            try {
                // create url with the string.
                URL url = new URL(s);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine = in.readLine();

                // read every line of the HTML content in the URL
                // and concat each line to the rawHTML string until every line is read.
                while (inputLine != null) {
                    rawHTML += inputLine;

                    inputLine = in.readLine();
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // create a regex pattern matching a URL
            // that will validate the content of HTML in search of a URL.
            String urlPattern = "^(http|https):\\/\\/(www).([a-z\\.]*)?(\\/[a-z1-9\\/]*)*\\??([\\&a-z1-9=]*)?\n";
            Pattern pattern = Pattern.compile(urlPattern);
            Matcher matcher = pattern.matcher(rawHTML);

            while (matcher.find()) {
                String actualURL = matcher.group();
                System.out.println(actualURL);
                if (visited.contains(actualURL)) {
                    continue;
                }
                result.add(actualURL);
            }
            return result;
        }
    }
}
