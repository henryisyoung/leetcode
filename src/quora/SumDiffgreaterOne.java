package quora;

import java.util.*;

public class SumDiffgreaterOne {
    class Node {
        int val, count;
        public Node(int val, int count) {
            this.count = count;
            this.val = val;
        }
    }
    public int findSum(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int max = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            map.put(i, map.getOrDefault(i, 0) + 1);
        }
        List<Node> list = new ArrayList<>();
        for (int i : map.keySet()) {
            list.add(new Node(i, map.get(i)));
        }
        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.val - o2.val;
            }
        });
        int[] dp = new int[list.size()];
        dp[0] = list.get(0).val * list.get(0).count;
        max = dp[0];
        for (int i = 1; i < list.size(); i++) {
            dp[i] = list.get(i).val * list.get(i).count;
            for (int j = 0; j < i; j++) {
                if (list.get(i).val > list.get(j).val + 1) {
                    dp[i] = Math.max(dp[i], dp[j] + list.get(i).val * list.get(i).count);
                }
                max = Math.max(max, dp[i]);
            }
        }
        return max;
    }
}
