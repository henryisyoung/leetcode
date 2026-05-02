package snowflake.mianjing.selfreview;

import java.util.*;

public class BestSellerTracker2 {
    static class BookEntry {
        int count;
        String name;
        public BookEntry(int count, String name) {
            this.count = count;
            this.name = name;
        }
    }
    Map<String, BookEntry> map;
    TreeSet<BookEntry> set;
    public BestSellerTracker2() {
        this.map = new HashMap<>();
        this.set = new TreeSet<>((a, b) -> {
            if (a.count == b.count) {
                return b.name.compareTo(a.name);
            }
            return b.count - a.count;
        } );
    }

    public List<String> bestSellers(Map<String, Integer> sales, Integer k) {
        for (Map.Entry<String, Integer> sale : sales.entrySet()) {
            String name = sale.getKey();
            int count = sale.getValue();
            if (map.containsKey(name)) {
                BookEntry entry = map.get(name);
                set.remove(entry);
                entry.count += count;
                set.add(entry);
            } else {
                BookEntry entry = new BookEntry(count, name);
                set.add(entry);
                map.put(name, entry);
            }
        }
        List<String> result = new ArrayList<>();
        Iterator<BookEntry> iterator = set.iterator();
        int limit = k;

        while (limit > 0 && iterator.hasNext()) {
            result.add(iterator.next().name);
            limit--;
        }

        return result;
    }

    public static void main(String[] args) {
        BestSellerTracker2 tracker = new BestSellerTracker2();

        // first
        List<String> names = Arrays.asList("A", "V0", "Ad", "F");
        List<Integer> counts = Arrays.asList(12,5,6,4);
        Map<String, Integer> sales = buildTestSales(names, counts);
        List<String> topK = tracker.bestSellers(sales, 2);

        // second
        List<String> names2 = Arrays.asList("Aasd", "V0", "Add", "aF");
        List<Integer> counts2 = Arrays.asList(12,50,6,14);
        Map<String, Integer> sales2 = buildTestSales(names2, counts2);
        List<String> topK2 = tracker.bestSellers(sales2, 2);

        System.out.println(topK2.toString());
    }

    private static Map<String, Integer> buildTestSales(List<String> names, List<Integer> counts) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0 ; i< names.size(); i++) {
            map.put(names.get(i), counts.get(i));
        }
        return map;
    }
}
