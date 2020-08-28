package apple;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ReorganizeString {
    public String reorganizeString(String S) {
        if (S == null || S.length() == 0) return S;
        StringBuilder sb = new StringBuilder();
        Map<Character, Integer> map = new HashMap<>();
        for (char c : S.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (b.count - a.count));
        for (char c : map.keySet()) {
            pq.add(new Node(map.get(c), c));
        }

        Node prev = new Node(-1,'c');
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            sb.append(cur.c);
            cur.count--;
            if (prev.count > 0) pq.add(prev);
            prev = cur;
        }
        return sb.length() == S.length() ? sb.toString() : "";
    }

    class Node{
        int count;
        char c;
        public Node(int count, char c) {
            this.c = c;
            this.count = count;
        }
    }
}
