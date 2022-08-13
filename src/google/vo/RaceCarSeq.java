package google.vo;

import java.util.*;

public class RaceCarSeq {
    public static List<String> racecar(int target) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(0 ,1, "A"));
        int step = 0;
        Set<String> set = new HashSet<>();
        set.add("0-1");

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node cur = queue.poll();
                int pos = cur.pos, speed = cur.speed;
                if (pos == target) {
                    return buildSeq(cur);
                }
                {
                    // A
                    int nextPos = pos + speed;
                    int nextSpeed = speed * 2;
                    if (set.contains(nextPos + "-" + nextSpeed)) {
                        continue;
                    }
                    set.add(nextPos + "-" + nextSpeed);
                    Node next = new Node(nextPos, nextSpeed, "A");
                    next.parent = cur;
                    queue.add(next);

                }
                {
                    int nextPos = pos;
                    int nextSpeed = speed > 0 ? -1 : 1;
                    if (set.contains(nextPos + "-" + nextSpeed)) {
                        continue;
                    }
                    Node next = new Node(nextPos, nextSpeed, "R");
                    next.parent = cur;
                    queue.add(next);
                }
            }
            step++;
        }
        return new ArrayList<>();
    }

    private static List<String> buildSeq(Node cur) {
        List<String> result = new ArrayList<>();
        while (cur.parent != null) {
            result.add(0, String.valueOf(cur.pos));
            cur = cur.parent;
        }
        return result;
    }

    static class Node{
        int pos, speed;
        Node parent;
        String seq;
        public Node(int pos, int speed, String seq) {
            this.pos = pos;
            this.speed = speed;
            this.seq = seq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return pos == node.pos && speed == node.speed;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, speed);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "pos=" + pos +
                    ", speed=" + speed +
                    '}';
        }
    }

    public static void main(String[] args) {
        int target = 6;

        System.out.println(racecar(target));
    }
}
