package pinterest;

import java.util.*;

public class DomainHistory {
    public List<String> subdomainVisits(String[] cpdomains) {
        List<String> result = new ArrayList<>();
        if (cpdomains == null || cpdomains.length == 0) {
            return result;
        }
        Map<String, Integer> domainsMap = new HashMap<>();
        for (String domain : cpdomains) {
            String[] arr = domain.split(" ");
            int count = Integer.parseInt(arr[0]);
            String dom = arr[1];
            domainsMap.put(dom, count);
        }
        Map<String, Integer> subdomainsMap = new HashMap<>();

        for(String domain: domainsMap.keySet()){
            int val = domainsMap.get(domain);
            subdomainsMap.put(domain, subdomainsMap.getOrDefault(domain, 0) + val);
            int pos = domain.indexOf(".");
            while (pos > 0) {
                domain = domain.substring(pos + 1);
                subdomainsMap.put(domain, subdomainsMap.getOrDefault(domain, 0) + val);
                pos = domain.indexOf(".");
            }
        }
        for (String key : subdomainsMap.keySet()) {
//            System.out.println("key " + key + " val " + subdomainsMap.get(key));
            result.add(subdomainsMap.get(key) + " " + key);
        }
        return result;
    }

    public int longestCommonString(String[] a, String[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0) {
            return 0;
        }
        int max = 0;
        int m = a.length, n = b.length;
        int[][] dp = new int[m + 1][n + 1];
        int end = 0;

        for (int i = 1; i <=m ;i++) {
            for (int j = 1; j <= n; j++){
                if (a[i - 1].equals(b[j - 1])) {
                    dp[i][j] = Math.max(dp[i - 1][j - 1], Math.max(dp[i - 1][j], dp[i][j - 1])) + 1;
                } else {
                    dp[i][j] = 0;
                }
                if (dp[i][j] > max) {
                    max = dp[i][j];
                    end = i - 1;
                }
            }
        }
        List<String> words = new ArrayList<>();
        int i = 0;
        while (i < max) {
            i++;
            words.add(a[end--]);
        }
        Collections.reverse(words);
        System.out.println(words.toString());
        return max;
    }

    public static void main(String[] args) {
        DomainHistory sovler = new DomainHistory();
        Map<String, Integer> map = new HashMap<>();
        map.put("google.com", 60);
        map.put("yahoo.com", 50);
        map.put("sports.yahoo.com", 80);
        String[] cpdomains = {"60 google.com", "50 yahoo.com", "80 sports.yahoo.com"};
        System.out.println(sovler.subdomainVisits(cpdomains));

        String[] a = {"3234.html", "xys.html", "7hsaa.html"};
        String[] b = {"3234.html", "sdhsfjdsh.html", "xys.html", "7hsaa.html"};
        System.out.println(sovler.longestCommonString(a,b));
    }
}
