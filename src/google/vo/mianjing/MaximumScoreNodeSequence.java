package google.vo.mianjing;

import java.util.*;

public class MaximumScoreNodeSequence {
    // https://leetcode.com/problems/maximum-score-of-a-node-sequence/
    public int maximumScore(int[] scores, int[][] edges) {
        Map<Integer, PriorityQueue<int[]>> map = new HashMap<>();
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            map.putIfAbsent(a, new PriorityQueue<>((x, y) -> (x[0] - y[0])));
            map.putIfAbsent(b, new PriorityQueue<>((x, y) -> (x[0] - y[0])));
            map.get(a).add(new int[]{scores[b], b});
            if (map.get(a).size() > 3) {
                map.get(a).poll();
            }
            map.get(b).add(new int[]{scores[a], a});
            if (map.get(b).size() > 3) {
                map.get(b).poll();
            }
        }
        int result = -1;
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            for (int[] cNums : map.get(a)) {
                for (int[] dNums : map.get(b)) {
                    int c = cNums[1], d = dNums[1];
                    Set<Integer> set = new HashSet<>(Arrays.asList(a, b ,c,d));
                    if (set.size() == 4 && scores[a] + scores[b] + scores[c] + scores[d] > result) {
                        result = scores[a] + scores[b] + scores[c] + scores[d];
                    }
                }
            }
        }
        return result;
    }
}
