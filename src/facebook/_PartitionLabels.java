package facebook;

import java.util.*;

public class _PartitionLabels {
    public List<Integer> partitionLabels(String S) {
        List<Integer> result = new ArrayList<>();
        Map<Character, int[]> map = new HashMap<>();

        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            if (map.containsKey(c)) {
                map.get(c)[1] = i;
            } else {
                map.put(c, new int[2]);
                map.get(c)[0] = i;
            }
        }

        List<Node> nodes = new ArrayList<>();
        for (int[] c : map.values()) {
            nodes.add(new Node(c[0], true));
            nodes.add(new Node(c[1], false));
        }

        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos != o2.pos) return o1.pos - o2.pos;
                if (o1.isStart) return 1;
                return -1;
            }
        });

        int count = 0;
        Integer lastEnd = null, lastStart = null;

        for (Node n : nodes) {
            if (n.isStart) {
                if (count == 0 && lastEnd != null && lastEnd != n.pos) {
                    result.add(lastEnd - lastStart);
                }
                if (count == 0) {
                    lastStart = n.pos;
                }
                count++;
            } else {
                lastEnd = n.pos;
                count--;
            }
        }

        return result;
    }

    class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }
    }
}
