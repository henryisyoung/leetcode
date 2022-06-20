package datastructure.graph;

import java.util.*;

public class ReconstructItinerary {


    public List<String> findItinerary(List<List<String>> tickets) {
        List<String> result = new ArrayList<>();
        if (tickets == null || tickets.size() == 0) return result;
        Map<String, PriorityQueue<String>> map = new HashMap<>();
        for (List<String> list : tickets) {
            String from = list.get(0), to = list.get(1);
            map.putIfAbsent(from, new PriorityQueue<>());
            map.get(from).add(to);
        }
        dfsSearch(map, result, "JFK");
        return result;
    }

    private void dfsSearch(Map<String, PriorityQueue<String>> map, List<String> result, String cur) {
        if (map.containsKey(cur)) {
            PriorityQueue<String> children = map.get(cur);
            while (children.size() > 0) {
                dfsSearch(map, result, children.poll());
            }
        }
        result.add(0, cur);
    }
}
