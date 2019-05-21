package pinterest;

import java.util.*;

public class SubdomainVisitCount {
    public List<String> subdomainVisits(String[] cpdomains) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (String domains : cpdomains) {
            String[] arr = domains.split(" ");
            int count = Integer.parseInt(arr[0]);
            String domain = arr[1];
            int pos = domain.indexOf(".");
            while (pos > 0) {
                map.put(domain, map.getOrDefault(domain, 0) + count);
                domain = domain.substring(pos + 1);
                pos = domain.indexOf(".");
            }
            map.put(domain, map.getOrDefault(domain, 0) + count);
        }
        for (String domain : map.keySet()) {
            result.add(map.get(domain) + " " + domain);
        }

        return result;
    }
}
