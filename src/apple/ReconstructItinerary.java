package apple;

import java.util.*;

public class ReconstructItinerary {
    public List<String> findItinerary(List<List<String>> tickets) {
        List<String> result = new ArrayList<>();
        Map<String, PriorityQueue<String>> map = new HashMap<>();
        for (List<String> ticket : tickets) {
            String from = ticket.get(0), to = ticket.get(1);
            map.putIfAbsent(from, new PriorityQueue<String>());
            map.get(from).add(to);
        }

        findPath(result, map, "JFK");
        return result;
    }

    private void findPath(List<String> result, Map<String, PriorityQueue<String>> map, String cur) {
        if (map.containsKey(cur)) {
            PriorityQueue<String> pq = map.get(cur);
            while (pq.size() > 0) {
                String next = pq.poll();
                findPath(result, map, next);
            }
        }
        result.add(0, cur);

    }
}
