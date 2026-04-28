package snowflake.mianjing;
/*
Tracking Top Selling Books
We need to build a system that keeps track of total book sales. It should also give us a list of the top-selling books every time we update the sales data.

Class Requirements
You need to implement the BestSellerTracker class with the following methods:

BestSellerTracker(): Sets up the tracker with no sales data initially.
List<String> bestSellers(Map<String, Integer> sales, Integer k): Adds new sales numbers to the existing totals for each book. After updating the totals, it returns the top k book titles.
Ranking Rules
Sort the books using this order:

Sales Count: Books with higher total sales come first.
Alphabetical Order: If two books have the exact same sales total, pick the title that is "larger" alphabetically (for example, "beta" comes before "alpha").
Note: If the requested k is bigger than the total number of books tracked, simply return all the books in the correct order.

Sample Cases
Case 1:

Input: ["BestSellerTracker","bestSellers","bestSellers"] [[],[{"a":5,"b":10,"c":15},2],[{"a":20,"b":20,"c":5},2]]

Output: [null,["c","b"],["b","a"]]

Breakdown:

First Update: We add sales {a:5, b:10, c:15}. The totals are now {a:5, b:10, c:15}. The top 2 books are "c" (15) and "b" (10).
Second Update: We add more sales {a:20, b:20, c:5} to the old totals.
Book "a": 5 + 20 = 25
Book "b": 10 + 20 = 30
Book "c": 15 + 5 = 20
New totals are {a:25, b:30, c:20}. The top 2 are now "b" (30) and "a" (25).
Case 2:

Input: ["BestSellerTracker","bestSellers"] [[],[{"alpha":4,"beta":4,"gamma":1},5]]

Output: [null,["beta","alpha","gamma"]]

Breakdown: "alpha" and "beta" are tied with 4 sales each. Since "beta" is alphabetically larger than "alpha", "beta" is placed first.

Technical Limits
0 <= sales.size() <= 10^4
0 <= sales[title] <= 10^6
1 <= title.length <= 100
1 <= k <= 10^4
The bestSellers method will be called at most 10^4 times.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class BestSellerTracker {

    /*
    Design choice — keep a sorted live ranking, not re-sort on every query.

    Data structures:
      - totals       : title -> running cumulative sales.
      - entryByTitle : title -> the *immutable* Entry currently sitting in the
                       TreeSet, so we can remove it in O(log N) when we update.
      - ranking      : TreeSet of Entry(title, sales) ordered by:
                         1) sales DESC  (higher sales first)
                         2) title DESC  (alphabetically larger first on tie,
                                          per the problem: "beta" before "alpha")
        IMPORTANT: the Entry's sales is final, so the comparator never sees
        a value change while the entry is in the tree (avoids the classic
        "mutated key in TreeSet" bug).

    bestSellers(sales, k):
      For each (title, delta) in the new sales batch:
        - Remove the old Entry from the TreeSet (if present).
        - Compute new total, build a fresh Entry with the new sales, and
          insert it.
      Then walk the TreeSet from the front and collect up to k titles.

    Complexity per call:
      M = sales.size(), N = total tracked titles.
      Updates: O(M log N).  Top-k walk: O(k).  Total: O((M + k) log N).
     */

    private static final class Entry {
        final String title;
        final int sales;
        Entry(String title, int sales) {
            this.title = title;
            this.sales = sales;
        }
    }

    private final Map<String, Integer> totals = new HashMap<>();
    private final Map<String, Entry> entryByTitle = new HashMap<>();
    private final TreeSet<Entry> ranking = new TreeSet<>((a, b) -> {
        if (a.sales != b.sales) return Integer.compare(b.sales, a.sales);
        return b.title.compareTo(a.title);
    });

    public BestSellerTracker() {}

    public List<String> bestSellers(Map<String, Integer> sales, Integer k) {
        for (Map.Entry<String, Integer> e : sales.entrySet()) {
            String title = e.getKey();
            int delta = e.getValue();

            Entry old = entryByTitle.get(title);
            if (old != null) ranking.remove(old);

            int newTotal = (old == null ? 0 : old.sales) + delta;
            Entry next = new Entry(title, newTotal);

            ranking.add(next);
            entryByTitle.put(title, next);
            totals.put(title, newTotal);
        }

        int limit = Math.min(k, ranking.size());
        List<String> result = new ArrayList<>(limit);
        Iterator<Entry> it = ranking.iterator();
        while (it.hasNext() && result.size() < limit) {
            result.add(it.next().title);
        }
        return result;
    }

    public static void main(String[] args) {
        BestSellerTracker t1 = new BestSellerTracker();
        Map<String, Integer> s1 = new HashMap<>();
        s1.put("a", 5); s1.put("b", 10); s1.put("c", 15);
        System.out.println(t1.bestSellers(s1, 2)); // [c, b]

        Map<String, Integer> s2 = new HashMap<>();
        s2.put("a", 20); s2.put("b", 20); s2.put("c", 5);
        System.out.println(t1.bestSellers(s2, 2)); // [b, a]

        BestSellerTracker t2 = new BestSellerTracker();
        Map<String, Integer> s3 = new HashMap<>();
        s3.put("alpha", 4); s3.put("beta", 4); s3.put("gamma", 1);
        System.out.println(t2.bestSellers(s3, 5)); // [beta, alpha, gamma]

        // k larger than total tracked -> return all in order.
        System.out.println(t2.bestSellers(new HashMap<>(), 10)); // [beta, alpha, gamma]
    }
}
