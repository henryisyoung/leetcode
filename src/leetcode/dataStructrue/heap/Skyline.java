package leetcode.dataStructrue.heap;

import java.util.*;

public class Skyline {
    class Edge{
        int pos, h;
        boolean isStart;
        public Edge(int pos, int h, boolean isStart) {
            this.pos = pos;
            this.h = h;
            this.isStart = isStart;
        }
    }

    public List<List<Integer>> getSkyline(int[][] buildings) {
        List<List<Integer>> result = new ArrayList<>();
        if (buildings == null || buildings.length == 0 || buildings[0] == null || buildings[0].length == 0) {
            return result;
        }
        List<Edge> nodes = new ArrayList<>();
        for (int[] b : buildings) {
            nodes.add(new Edge(b[0], b[2], true));
            nodes.add(new Edge(b[1], b[2], false));
        }
        Collections.sort(nodes, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                if (o1.pos != o2.pos) return o1.pos - o2.pos;
                if (o1.isStart == o2.isStart) {
                    if (o1.isStart) {
                        return o2.h - o1.h;
                    } else {
                        return o1.h - o2.h;
                    }
                } else {
                    if (o1.isStart) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> (b -a));
        for (Edge e : nodes) {
            if (e.isStart) {
                if (pq.isEmpty() || e.h > pq.peek()) {
                    result.add(Arrays.asList(e.pos, e.h));
                }
                pq.add(e.h);
            } else {
                pq.remove(e.h);
                if (pq.isEmpty() || e.h > pq.peek()) {
                    if (pq.isEmpty()) {
                        result.add(Arrays.asList(e.pos, 0));
                    } else {
                        result.add(Arrays.asList(e.pos, pq.peek()));
                    }
                }
            }
        }
        return result;
    }
}
