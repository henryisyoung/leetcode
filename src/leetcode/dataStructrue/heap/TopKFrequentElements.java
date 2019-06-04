package leetcode.dataStructrue.heap;

import java.util.*;

public class TopKFrequentElements {
    public static List<Integer> topKFrequent(int[] nums, int k) {
        List<Integer> result = new ArrayList<>();
        if (nums == null || nums.length < k) {
            return result;
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<int[]> pq = new PriorityQueue<>(map.size(), (a, b) -> (b[1] - a[1]));
        for (int key : map.keySet()) {
            pq.add(new int[]{key, map.get(key)});
        }
        while (k > 0) {
            k--;
            result.add(pq.poll()[0]);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1,1,1,2,2,3};
        int k = 2;
        List<Integer> result = topKFrequent(nums, k);
        System.out.println(result.toString());
    }
}
