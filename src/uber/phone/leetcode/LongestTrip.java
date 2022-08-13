package uber.phone.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongestTrip {

    public static List<String> longestTrip(String[][] trips) {
        Map<String, String> map = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();
        for (String[] trip : trips) {
            String from = trip[0], to = trip[1];
            map.put(from, to);
            indegree.put(to, indegree.getOrDefault(to, 0) + 1);
            indegree.putIfAbsent(from, 0);
        }
        Map<String, List<String>> memo = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (String key : indegree.keySet()) {
            if (indegree.get(key) == 0) {
                List<String> list = findPath(map, memo, key);
                if (list.size() > result.size()) {
                    result.clear();
                    result = new ArrayList<>(list);
                }
            }
        }
        return result;
    }

    private static List<String> findPath(Map<String, String> map, Map<String, List<String>> memo, String key) {
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        List<String> result = new ArrayList<>();
        if (!map.containsKey(key)) {
            result.add(key);
            memo.put(key, new ArrayList<>(result));
            return result;
        }

        result = findPath(map, memo, map.get(key));
        result.add(0, key);
        memo.put(key, new ArrayList<>(result));
        return result;
    }

    public static void main(String[] args) {
        String[][] trips = {
                {"NYC", "SFO"},
                {"SFO", "LAX"},
                {"LAX", "SEA"},
                {"PED", "SEA"},
                {"PIT", "PED"},
                {"ABC", "PIT"},
                {"DEK", "ABC"},
        };
        System.out.println(longestTrip(trips));
    }
}
