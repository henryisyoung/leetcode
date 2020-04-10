package facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class _TaskScheduler {
    public int leastInterval(char[] tasks, int n) {
        int[] array = new int[26];
        for (char task : tasks) array[task - 'A']++;
        Arrays.sort(array);
        int max = array[25];
        int i = 25;
        while (i >= 0 && array[i] == max) i--;
        return Math.max((n + 1) * (max - 1) + (25 - i), tasks.length);
    }


    public int leastIntervalPQ(char[] tasks, int n) {
        int[] array = new int[26];
        for (char task : tasks) array[task - 'A']++;
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) ->(b.count - a.count));
        for (int i = 0; i < 26; i++) {
            if (array[i] > 0) {
                pq.add(new Node((char) ('A' + i), array[i]));
            }
        }
        int count = 0;
        while (!pq.isEmpty()) {
            List<Node> list = new ArrayList<>();
            int t = 0;
            for (int i = 0; i < n + 1; i++) {
                if (!pq.isEmpty()) {
                    Node cur = pq.poll();
                    list.add(cur);
                    t++;
                }
            }
            for (Node node : list) {
                node.count--;
                if (node.count > 0) pq.add(node);
            }
            count += pq.isEmpty() ? t : n + 1;
        }

        return count;
    }

    class Node{
        int count;
        char c;
        public Node(char c, int count) {
            this.c = c;
            this.count = count;
        }
    }
}