package snowflake.mianjing.selfreview;

import java.util.*;

public class BestSellerTracker {
    static class Entry {
        int count;
        String name;
        public Entry(String name, int count) {
            this.count = count;
            this.name = name;
        }
    }

    Map<String, Entry> map;
    TreeSet<Entry> set;
    public BestSellerTracker() {
        this.map = new HashMap<>();
        this.set = new TreeSet<>((a, b) -> {
           if (a.count != b.count) return b.count - a.count;
           return b.name.compareTo(a.name);
        });
    }

    public List<String> bestSellers(Map<String, Integer> sales, Integer k) {
        for (Map.Entry<String, Integer> sale : sales.entrySet()) {
            String name = sale.getKey();
            int count = sale.getValue();

            if (map.containsKey(name)) {
                Entry entry = map.get(name);
                set.remove(entry);
                entry.count += count;
                set.add(entry);
            } else {
                Entry entry = new Entry(name, count);
                set.add(entry);
                map.put(name, entry);
            }
        }

        Iterator<Entry> iterator = set.iterator();
        List<String> result = new ArrayList<>();
        while (k > 0 && iterator.hasNext()) {
            result.add(iterator.next().name);
            k--;
        }

        return result;
    }
}
