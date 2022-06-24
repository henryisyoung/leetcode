package apple;

import java.util.*;

public class CourseSchedule {
    public boolean canFinish(int n, int[][] prerequisites) {
        List<Integer> seq = new ArrayList<>();
        int[] indegree = new int[n];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] pair : prerequisites) {
            int to = pair[0], from = pair[1];
            indegree[to]++;
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                seq.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    indegree[next]--;
                    if (indegree[next] == 0) {
                        queue.add(next);
                        seq.add(next);
                    }
                }
            }
        }
        return seq.size() == n;
    }

    public static void main(String[] args) {

    }
}