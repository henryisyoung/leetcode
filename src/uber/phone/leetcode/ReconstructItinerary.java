package uber.phone.leetcode;

import java.util.*;

public class ReconstructItinerary {
    public static List<String> findItinerary(List<List<String>> tickets) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (List<String> t : tickets) {
            String from = t.get(0), to = t.get(1);
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            Collections.sort(entry.getValue());
        }
        int ticketsNumber = tickets.size();
        result.add("JFK");
        dfsSearch(result, "JFK", map, 0, ticketsNumber);

        return result;
    }

    private static boolean dfsSearch(List<String> result, String cur, Map<String, List<String>> map, int count, int ticketsNumber) {
        if (ticketsNumber == count) {
            return true;
        }
        if (map.containsKey(cur)) {
            List<String> list = map.get(cur);
            for (int i = 0; i < list.size(); i++){
                String next = list.get(i);
                list.remove(next);
                result.add(next);
                if (dfsSearch(result, next, map, count + 1, ticketsNumber)) {
                    return true;
                }
                list.add(i, next);
                result.remove(result.size() - 1);
            }
        }
        return false;
    }


    public static void main(String[] args) {
        List<List<String>> tickets = Arrays.asList(
                Arrays.asList("JFK", "KUL"),
                Arrays.asList("JFK", "NRT"),
                Arrays.asList("NRT", "JFK")
        );
        System.out.println(findItinerary(tickets));
    }
}
