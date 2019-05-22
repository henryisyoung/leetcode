package quora;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ReorganizeString {
    //https://www.geeksforgeeks.org/rearrange-characters-string-no-two-adjacent/
    class Node {
        char c;
        int count;
        public Node(char c, int count) {
            this.c = c;
            this.count = count;
        }
    }

    public String reorganizeString(String S) {
        if (S == null || S.length() == 0) {
            return "";
        }
        int n = S.length();
        int[] count = new int[26];
        for (char c : S.toCharArray()) {
            count[c - 'a']++;
        }
        PriorityQueue<Node> pq = new PriorityQueue<>(n, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.count - o1.count;
            }
        });
        for (char c = 'a'; c <= 'z'; c++) {
            if (count[c - 'a'] != 0) {
                pq.add(new Node(c, count[c - 'a']));
            }
        }
        Node prev = new Node('#', -1);
        StringBuilder sb = new StringBuilder();
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            sb.append(cur.c);
            cur.count--;
            if (prev.count > 0) {
                pq.add(prev);
            }
            prev = cur;
        }
        return sb.toString().length() == n ? sb.toString() : "";
    }
}
