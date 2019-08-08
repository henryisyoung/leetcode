package snap;

import java.util.*;

public class MergeIntervalThreeD {
    class Node{
        int pos, height;
        boolean isStart;
        public Node(int pos, int h, boolean isStart) {
            this.height = h;
            this.pos = pos;
            this.isStart = isStart;
        }
    }
    public List<int[]> findOutlines(int[][] intervals) {
        List<Node> list = new ArrayList<>();
        for (int[] in : intervals) {
            list.add(new Node(in[0], in[2], true));
            list.add(new Node(in[1], in[2], false));
        }
        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node l1, Node l2) {
                if (l1.pos != l2.pos)
                    return compareInteger(l1.pos, l2.pos);
                if (l1.isStart && l2.isStart) {
                    return compareInteger(l2.height, l1.height);
                }
                if (!l1.isStart && !l2.isStart) {
                    return compareInteger(l1.height, l2.height);
                }
                return l1.isStart ? -1 : 1;
            }

            int compareInteger(int a, int b) {
                return a <= b ? -1 : 1;
            }
        });
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        List<int[]> result = new ArrayList<>();
        for (Node n : list) {
            if (n.isStart) {
                if (pq.isEmpty() || pq.peek() < n.height) {
                    result.add(new int[]{n.pos, n.height});
                }
                pq.add(n.height);
            } else {
                pq.remove(n.height);
                if (pq.isEmpty() || pq.peek() < n.height) {
                    if (pq.isEmpty()) {
                        result.add(new int[]{n.pos, n.height});
                    } else {
                        result.add(new int[]{n.pos, pq.peek()});
                    }
                }
            }
        }
        for (int[] ints : result) {
            System.out.println(ints[0] + " " + ints[1]);
        }
        return result;
    }

    public static void main(String[] args) {
        MergeIntervalThreeD solver = new MergeIntervalThreeD();
        int[][] intervals = {{1,3,5}, {2,4,9}, {7,8,1}};
        List<int[]> result = solver.findOutlines(intervals);
        for (int[] ints : result) {
            Arrays.toString(ints);
        }
    }
}
