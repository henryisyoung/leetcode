package leetcode.dataStructrue.heap;

import java.util.*;

public class TopKFrequentWords {
    public static List<String> topKFrequent(String[] words, int k) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (String word : words) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
        int n = map.size();
        PriorityQueue<Node> pq = new PriorityQueue<Node>(n, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.count != o2.count) {
                    return o2.count - o1.count;
                } else {
                    return compareStr(o1.value, o2.value);
                }
            }

            private int compareStr(String s1, String s2) {
                int n = Math.min(s1.length(), s2.length());
                for (int i = 0; i < n; i++) {
                    if (s1.charAt(i) < s2.charAt(i)) return -1;
                    if (s1.charAt(i) > s2.charAt(i)) return 1;
                }
                if (n == s1.length()) return -1;
                return 1;
            }
        });
        for (String key : map.keySet()) {
            pq.add(new Node(key, map.get(key)));
        }
        while (!pq.isEmpty() && k > 0) {
            result.add(pq.poll().value);
            k--;
        }
        return result;
    }

    static class Node {
        String value;
        int count;
        public Node(String value, int count) {
            this.value = value;
            this.count = count;
        }
    }

    public static void main(String[] args) {
        String[] words = {"i", "love", "leetcode", "i", "love", "coding"};
        int k = 2;
        List<String> result = topKFrequent(words, k);
        System.out.println(result.toString());
    }
}
