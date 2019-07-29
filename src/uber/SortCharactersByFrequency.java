package uber;

import leetcode.dataStructrue.segmentTree.Interval;

import java.util.*;

public class SortCharactersByFrequency {
    class Node{
        int count;
        char val;
        public Node(int count, char val) {
            this.count = count;
            this.val = val;
        }
    }

    public String frequencySort(String s) {
        if (s == null || s.length() == 0) return s;
        int n = s.length();
        PriorityQueue<Node> pq = new PriorityQueue<>(n, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.count == o2.count) {
                    return o1.val - o2.val;
                } else {
                    return o2.count - o1.count;
                }
            }
        }) ;
        Map<Character, Integer> map = new HashMap<>();
        for (char c : s.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            pq.add(new Node(entry.getValue(), entry.getKey()));
        }
        StringBuilder sb = new StringBuilder();
        while (!pq.isEmpty()) {
            Node node = pq.poll();
            char c = node.val;
            int count = node.count;
            while (count > 0) {
                count--;
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "tree";
        SortCharactersByFrequency solution = new SortCharactersByFrequency();
        System.out.println(solution.frequencySort(s));
    }
}
