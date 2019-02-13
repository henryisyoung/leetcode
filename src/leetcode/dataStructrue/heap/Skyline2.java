package leetcode.dataStructrue.heap;

import java.util.*;

public class Skyline2 {
    private class Edge{
        int height, pos;
        boolean isStart;

        public Edge(int pos, int height, boolean isStart){
            this.pos = pos;
            this.height = height;
            this.isStart = isStart;
        }
    }

    private class EdgeComparator implements Comparator<Edge> {
        @Override
        public int compare(Edge arg1, Edge arg2) {
            Edge l1 = (Edge) arg1;
            Edge l2 = (Edge) arg2;
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
            return a - b;
        }
    }

    public List<int[]> getSkyline(int[][] buildings) {
        List<int[]> result = new ArrayList<int[]>();

        if(buildings == null || buildings.length == 0 || buildings[0].length == 0){
            return result;
        }
        List<Edge> edges = new ArrayList<>();
        for (int[] build : buildings) {
            Edge e1 = new Edge(build[0], build[2], true);
            Edge e2 = new Edge(build[1], build[2], false);
            edges.add(e1);
            edges.add(e2);
        }
        Collections.sort(edges, new EdgeComparator());
        PriorityQueue<Integer> pq = new PriorityQueue<>(edges.size(), Collections.reverseOrder());
        for (Edge edge : edges) {
            int height = edge.height;
            int[] arr = new int[2];
            if (edge.isStart) {
                if (pq.isEmpty() || height > pq.peek()) {
                    arr[0] = edge.pos;
                    arr[1] = edge.height;
                }
                pq.offer(height);
            } else {
                pq.remove(height);
                if (pq.isEmpty()) {
                    arr[0] = edge.pos;
                    arr[1] = 0;
                } else if (height > pq.peek()) {
                    arr[0] = edge.pos;
                    arr[1] = pq.peek();
                }
            }
            result.add(arr);
        }
        return result;
    }
}
