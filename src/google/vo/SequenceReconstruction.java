package google.vo;

import java.util.*;

public class SequenceReconstruction {
    public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
        Map<Integer, Integer> indegree = new HashMap<>();
        Map<Integer, Set<Integer>> map = new HashMap<>();

        for (List<Integer> list : sequences) {
            for (int i = 0; i < list.size() - 1;i++) {
                int from = list.get(i), to = list.get(i + 1);
                indegree.putIfAbsent(from, 0);
                indegree.put(to, indegree.getOrDefault(to, 0) + 1);
                map.putIfAbsent(from, new HashSet<>());
                map.get(from).add(to);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for(Map.Entry<Integer, Integer> entry: indegree.entrySet()) {
            if(entry.getValue() == 0) queue.offer(entry.getKey());
        }

        int index = 0;
        while(!queue.isEmpty()) {
            int size = queue.size();
            if(size > 1) return false;
            int curr = queue.poll();
            if(index == nums.length || curr != nums[index++]) return false;
            if (map.containsKey(curr)) {
                for(int next: map.get(curr)) {
                    indegree.put(next, indegree.get(next) - 1);
                    if(indegree.get(next) == 0) queue.offer(next);
                }
            }
        }
        return index == nums.length && index == map.size();
    }
}
