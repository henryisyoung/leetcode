package leetcode.graphAndSearch;

import java.util.*;

public class ReconstructItinerary {
    public static List<String> findItinerary(List<List<String>> tickets) {
        List<String> result = new ArrayList<>();
        Map<String, PriorityQueue> map = new HashMap<>();
        for (List<String> list : tickets) {
            String from = list.get(0), to = list.get(1);
            if (!map.containsKey(from)) {
                map.put(from, new PriorityQueue());
            }
            map.get(from).add(to);
        }
        dfsSearch(map, result, "JFK");
        Collections.reverse(result);
        return result;
    }

    private static void dfsSearch(Map<String, PriorityQueue> map, List<String> result, String cur) {
        PriorityQueue<String> next = map.get(cur);
        while (next != null && !next.isEmpty()) {
            dfsSearch(map, result, next.poll());
        }
        result.add(cur);
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
