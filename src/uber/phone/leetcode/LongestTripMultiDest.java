package uber.phone.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongestTripMultiDest {

    public static List<String> longestTrip(String[][] trips) {
        Map<String, List<String>> map = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();
        for (String[] trip : trips) {
            String from = trip[0], to = trip[1];
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
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

    private static List<String> findPath(Map<String, List<String>> map, Map<String, List<String>> memo, String key) {
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        List<String> result = new ArrayList<>();
        if (!map.containsKey(key)) {
            result.add(key);
            memo.put(key, new ArrayList<>(result));
            return result;
        }
        int max = 0;
        for (String next : map.get(key)) {
            List<String> list = findPath(map, memo, next);
            if (list.size() > max) {
                max = list.size();
                result.clear();
                result = new ArrayList<>(list);
            }
        }
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
                {"XXX", "SEA"},
                {"PIT", "PED"},
                {"DEK", "PIT"},
                {"DEK", "NYC"},
        };
        System.out.println(longestTrip(trips));
    }
}
