package google.vo.mianjing;

import java.util.*;

public class FindRing {
    /*
    graph traversal. Graph is stored in dictionary with keys as nodes and values as neighboring nodes.
    Guaranteed to be a ring, return the path either counterclockwise or clockwise following the ring
    until you get all nodes in the ring.
     */

    public static List<Integer> findRing(Map<Integer, List<Integer>> map) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> set = new LinkedHashSet<>();
        dfsFindRing(result, 0, set, -1, map);
        return result;
    }

    private static boolean dfsFindRing(List<Integer> result, int cur, Set<Integer> set, int parent, Map<Integer, List<Integer>> map) {
        if (set.contains(cur)) {
            result.addAll(new LinkedHashSet<>(set));
            return true;
        }
        set.add(cur);
        if (map.containsKey(cur)) {
            for (int next : map.get(cur)) {
                if (next != parent && dfsFindRing(result, next, set, cur, map)) {
                    return true;
                }
            }
        }
        set.remove(cur);
        return false;
    }

    public static void main(String[] args) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(0, Arrays.asList(1));
        map.put(1, Arrays.asList(0,4,2));
        map.put(2, Arrays.asList(0,3,1));
        map.put(3, Arrays.asList(2,4));
        map.put(4, Arrays.asList(1,5,6, 2));
        map.put(5, Arrays.asList(4));
        map.put(6, Arrays.asList(4));

        System.out.println(findRing(map));
    }
}
