package facebook;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ReorganizeString {
    public String reorganizeString(String S) {
        StringBuilder sb = new StringBuilder();

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (b.count - a.count));
        Map<Character, Integer> map = new HashMap<>();
        for (char c : S.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        for (Character c : map.keySet()) {
            pq.add(new Node(c, map.get(c)));
        }
        Node prev = new Node('*', -1);
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            sb.append(cur.character);
            cur.count--;
            if (prev.count > 0) pq.add(prev);
            prev = cur;
        }
        return sb.length() == S.length() ? sb.toString() : "";
    }

    class Node{
        int count;
        char character;
        public Node(char c, int count) {
            this.character = c;
            this.count = count;
        }
    }
}
