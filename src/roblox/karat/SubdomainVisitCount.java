package roblox.karat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubdomainVisitCount {
    public static List<String> subdomainVisits(String[] cpdomains) {
        Map<String, Integer> map = new HashMap<>();
        for (String dp : cpdomains) {
            String[] array = dp.split(" ");
            int count = Integer.parseInt(array[0]);
            String domain = array[1];
            map.put(domain, map.getOrDefault(domain, 0) + count);

            int index = domain.indexOf('.');
            while (index > 0) {
                domain = domain.substring(index + 1);
                map.put(domain, map.getOrDefault(domain, 0) + count);
                index = domain.indexOf('.');
            }
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.add(entry.getValue() + " " + entry.getKey());
        }
        return result;
    }

    public static void main(String[] args) {
        String[] cpdomains = {"900 google.mail.com", "50 yahoo.com", "1 intel.mail.com", "5 wiki.org"};
        System.out.println(subdomainVisits(cpdomains));
    }
}
