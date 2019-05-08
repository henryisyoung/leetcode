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
        List<int[]> reult  = new ArrayList<>();
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

        
    }
}
