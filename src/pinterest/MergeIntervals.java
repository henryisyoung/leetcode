package pinterest;

import leetcode.linkedList.ListNode;

import java.util.*;

public class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        if(intervals == null || intervals.length == 0 || intervals[0] == null || intervals[0].length == 0) {
            return intervals;
        }
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] == o2[0]) return o1[1] - o2[1];
                return o1[0] - o2[0];
            }
        });
        List<int[]> list = new ArrayList<>();
        int[] prev = intervals[0];
        for (int i = 1; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if (prev[1] < cur[0]) {
                list.add(prev);
                prev = cur;
            } else {
                prev[1] = Math.max(prev[1], cur[1]);
            }
        }
        list.add(prev);
        int[][] result = new int[list.size()][2];
        return list.toArray(result);
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
