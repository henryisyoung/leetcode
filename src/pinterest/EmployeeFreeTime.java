package pinterest;

import java.util.*;

public class EmployeeFreeTime {
    class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }
    }

    public int[][] employeeFreeTime(int[][][] schedule) {
        List<int[]> result  = new ArrayList<>();
        List<Node> nodes =new ArrayList<>();
        for (int[][] list : schedule) {
            for (int[] spot : list) {
                Node n1 = new Node(spot[0], true);
                Node n2 = new Node(spot[1], false);
                nodes.add(n1);
                nodes.add(n2);
            }
        }
        Collections.sort(nodes, new Comparator<Node>() {
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
        int count = 0;
        Integer start = null;

        for (Node n : nodes) {
            if (n.isStart) {
                if (count == 0 && start != null && start != n.pos) {
                    result.add(new int[]{start, n.pos});
                    start = null;
                }
                count++;
            } else {
                count--;
                if (count == 0) {
                    start = n.pos;
                }
            }
        }
        int size = result.size();
        int[][] ans = new int[size][2];
        for (int i = 0; i < size; i++) {
            ans[i] = result.get(i);
        }
        return ans;
    }
}
