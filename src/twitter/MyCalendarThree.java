package twitter;

import java.util.*;

public class MyCalendarThree {
    List<Node> nodes;
    public MyCalendarThree() {
        this.nodes = new ArrayList<>();
    }

    public int book(int start, int end) {
        int count = 0;
        List<Node> copy = new ArrayList<>(nodes);
        copy.add(new Node(start, true));
        copy.add(new Node(end, false));
        Collections.sort(copy, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos == o2.pos) {
                    if (!o1.isStart) {
                        return -1;
                    }
                    return 1;
                } else {
                    return o1.pos - o2.pos;
                }
            }
        });
        int k = 0;
        for (Node n : copy) {
            if (n.isStart) {
                count++;
                k = Math.max(k, count);
            } else {
                count--;
            }
        }
        nodes = new ArrayList<>(copy);
        return k;
    }

    class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }

        @Override
        public String toString() {
            return "pos " + pos + " isstart " + isStart;
        }
    }
}
