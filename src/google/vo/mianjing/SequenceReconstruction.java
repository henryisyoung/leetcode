package google.vo.mianjing;

import java.util.*;

public class SequenceReconstruction {
    // https://leetcode.com/problems/sequence-reconstruction/discuss/1209706/Java-Clean-Topological-Sort
    public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
        int count = 0;
        Map<Integer, Integer> indegree = new HashMap<>();
        Map<Integer, Set<Integer>> map = new HashMap<>();

        for (List<Integer> list : sequences) {
            for (int i = 0; i < list.size() - 1; i++) {
                int from = list.get(i), to = list.get(i + 1);
                indegree.put(to, indegree.getOrDefault(to, 0) + 1);
                indegree.putIfAbsent(from, 0);

                map.putIfAbsent(from, new HashSet<>());
                map.get(from).add(to);
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int key : indegree.keySet()) {
            if (indegree.get(key) == 0) {
                queue.add(key);
            }
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            if (size != 1) {
                return false;
            }
            int cur = queue.poll();

        }
        return true;
    }
}
