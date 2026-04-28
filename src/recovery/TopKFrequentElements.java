package recovery;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class TopKFrequentElements {
    private static class Node {
        int val, count;
        public Node(int val, int count) {
            this.val = val;
            this.count = count;
        }
    }
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> counter = new HashMap<>();
        for (int num : nums) {
            counter.put(num, counter.getOrDefault(num, 0) + 1);
        }

        int[] result = new int[k];
        PriorityQueue<Node> maxFreq = new PriorityQueue<>(Comparator.comparingInt(a -> a.count));

        for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
            int key = entry.getKey(), count = entry.getValue();
            maxFreq.add(new Node(key, count));
            if (maxFreq.size() > k) maxFreq.poll();
        }

        int index = 0;
        while (!maxFreq.isEmpty()) {
            result[index++] = maxFreq.poll().val;
        }

        return result;
    }
}
