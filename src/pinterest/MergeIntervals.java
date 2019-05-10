package pinterest;

import java.util.*;

public class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0 || intervals[0] == null || intervals[0].length == 0){
            return intervals;
        }
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        int[] prev = intervals[0];
        int n = intervals.length;
        List<int[]> list = new ArrayList<>();
        int max = 1;
        int count  = 1;
        for (int i = 1; i < n; i++) {
            int[] cur = intervals[i];
            if (cur[0] <= prev[1]) {
                count++;
                prev[1] = Math.max(cur[1], prev[1]);
            } else {
                max = Math.max(max, count);
                count = 0;
                list.add(prev);
                prev = cur;
            }
        }
        System.out.println(max);
        list.add(prev);

        int size = list.size();
        int[][] result = new int[size][2];
        for (int i = 0; i < size; i++) {
            result[i] = list.get(i);
            System.out.println(Arrays.toString(result[i]));
        }
        return result;
    }

    public int[] findMostBusy(int[][] intervals) {
        if (intervals == null || intervals.length == 0 || intervals[0] == null || intervals[0].length == 0){
            return new int[0];
        }
        List<Node> nodes = new ArrayList<>();
        for (int[] ints : intervals) {
            nodes.add(new Node(ints[0], true));
            nodes.add(new Node(ints[1], false));
        }
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos == o2.pos) {
                    if (!o1.isStart) {
                        return -1;
                    }
                    return 1;
                }else {
                    return o1.pos - o2.pos;
                }
            }
        });
        int max = 0, count = 0;
        int[] result = new int[2];
        Integer start = null;
        for (Node n : nodes) {
            if (n.isStart) {
                count++;
                if (count > max) {
                    start = n.pos;
                    max = count;
                }
            } else {
                if (count == max) {
                    if (start != null) {
                        result[0] = start;
                        result[1] = n.pos;
                        start = null;
                    }
                }
                count--;
            }
        }
        System.out.println(Arrays.toString(result));
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
    public static void main(String[] args) {
        int[][] nums = {{1,3},{2,18}};
        MergeIntervals solver = new MergeIntervals();
        solver.findMostBusy(nums);
    }
}
