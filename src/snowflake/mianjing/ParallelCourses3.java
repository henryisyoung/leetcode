package snowflake.mianjing;

import java.util.*;

public class ParallelCourses3 {
    public int minimumTime(int n, int[][] relations, int[] time) {
        int[] inDegree = new int[n + 1];
        List<List<Integer>> graph = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] rela : relations) {
            int from = rela[0], to = rela[1];
            inDegree[to]++;
            graph.get(from).add(to);
        }
        Queue<Integer> queue = new LinkedList<>();
        int[] finish = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                finish[i] = time[i - 1];
            }
        }
        int answer = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            answer = Math.max(answer, finish[cur]);
            for (int next : graph.get(cur)) {
                finish[next] = Math.max(finish[next], finish[cur] + time[next - 1]);
                inDegree[next]--;
                if (inDegree[next] == 0) {
                    queue.add(next);
                }
            }
        }

        return answer;
    }
}
