package leetcode.graphAndSearch;

import java.util.*;

public class ReconstructItinerary {
    public static List<String> findItinerary(List<List<String>> tickets) {
        List<String> result = new ArrayList<>();
        Map<String, PriorityQueue<String>> map = new HashMap<>();
        for (List<String> list : tickets) {
            String from = list.get(0), to = list.get(1);
            map.putIfAbsent(from, new PriorityQueue<>());
            map.get(from).add(to);
        }
        dfsSearchAll("JFK", map, result);
        return result;
    }

    private static void dfsSearchAll(String cur, Map<String, PriorityQueue<String>> map, List<String> result) {
        if (map.containsKey(cur)) {
            PriorityQueue<String> pq = map.get(cur);
            while (!pq.isEmpty()) {
                dfsSearchAll(pq.poll(), map, result);
            }
        }
        result.add(0, cur);
    }

    public static void main(String[] args) {
        List<String> l1 = Arrays.asList("JFK","SFO"), l2 = Arrays.asList("JFK","ATL"), l3 = Arrays.asList("SFO","ATL"),
                l4 = Arrays.asList("ATL","JFK"), l5 = Arrays.asList("ATL","SFO");
        List<List<String>> tickets = new ArrayList<>();
        tickets.add(l1);
        tickets.add(l2);
        tickets.add(l3);
        tickets.add(l4);
        tickets.add(l5);
        List<String> result = findItinerary(tickets);
        System.out.println(result.toString());
    }
}
