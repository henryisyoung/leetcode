package roblox.karat;

import java.util.*;

public class LCA {
    public static List<Integer> findNodes(int[][] pairs) {
        Map<Integer, Integer> indegree = new HashMap<>();
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (int[] pair : pairs) {
            int parent = pair[0], child = pair[1];
            indegree.putIfAbsent(parent, 0);
            indegree.put(child, indegree.getOrDefault(child, 0) + 1);
            map.putIfAbsent(parent, new HashSet<>());
            map.get(parent).add(child);
        }

        List<Integer> result = new ArrayList<>();
        for (int key : indegree.keySet()) {
            if (indegree.get(key) == 0 || indegree.get(key) == 1) {
                result.add(key);
            }
        }

        return result;
    }

    public static boolean hasLCA(int a, int b, int[][] pairs) {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (int[] pair : pairs) {
            int parent = pair[0], child = pair[1];
            map.putIfAbsent(child, new HashSet<>());
            map.get(child).add(parent);
        }

        Set<Integer> aParents = new HashSet<>();
        dfsFindAllParents(a, map, aParents);
        System.out.println(aParents);
        Set<Integer> bParents = new HashSet<>();
        dfsFindAllParents(b, map, bParents);
        System.out.println(bParents);

        aParents.retainAll(bParents);
        return aParents.size() > 0;
    }

    private static void dfsFindAllParents(int node, Map<Integer, Set<Integer>> map, Set<Integer> aParents) {
        aParents.add(node);
        if (map.containsKey(node)) {
            for (int child : map.get(node)) {
                if (aParents.contains(child)) continue;
                dfsFindAllParents(child, map, aParents);
            }
        }
    }

    public static void main(String[] args) {
        int[][] pairs =  {{1,4}, {1,5}, {2,5}, {3,6}, {6,7}};
//        System.out.println(findNodes(pairs));

        System.out.println(hasLCA(4,2,pairs));
    }
}
