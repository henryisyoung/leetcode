package leetcode.graphAndSearch;

import java.util.*;

public class Skyline {
    private class Edge {
        public int height, pos;
        public boolean isStart;

        public Edge (int height, int pos, boolean isStart) {
            this.height = height;
            this.pos = pos;
            this.isStart = isStart;
        }
    }

    public List<int[]> getSkyline(int[][] buildings) {
        List<int[]> result = new ArrayList<>();
        if(buildings == null || buildings[0] == null ||
                buildings.length == 0 || buildings[0].length == 0){
            return result;
        }
        List<Edge> edges = new ArrayList<>();
        for (int[] building : buildings) {
            Edge e1 = new Edge(building[0], building[2], true);
            Edge e2 = new Edge(building[0], building[1], false);
            edges.add(e1);
            edges.add(e2);
        }
        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge arg1, Edge arg2) {
                Edge l1 = (Edge) arg1;
                Edge l2 = (Edge) arg2;
                if (l1.pos != l2.pos)
                    return compareInteger(l1.pos, l2.pos);
                else if (l1.isStart && l2.isStart) {
                    return compareInteger(l2.height, l1.height);
                }
                else if (!l1.isStart && !l2.isStart) {
                    return compareInteger(l1.height, l2.height);
                }
                return l1.isStart ? -1 : 1;
            }
            private int compareInteger(int a, int b) {
                return a <= b ? -1 : 1;
            }
        });

        PriorityQueue<Integer> heightMaxPQ = new PriorityQueue<>(edges.size(), Collections.reverseOrder());

        for (Edge edge : edges) {
            int height = edge.height;
            if (edge.isStart) {
                if (heightMaxPQ.isEmpty() || height > heightMaxPQ.peek()) {
                    int[] curLine = new int[2];
                    curLine[0] = edge.pos;
                    curLine[1] = height;
                    result.add(curLine);
                }
                heightMaxPQ.offer(height);
            } else {
                heightMaxPQ.remove(height);
                if (heightMaxPQ.isEmpty() || height > heightMaxPQ.peek()) {
                    int[] curLine = new int[2];
                    curLine[0] = edge.pos;
                    if(heightMaxPQ.isEmpty()) {
                        curLine[1] = 0;
                    } else {
                        curLine[1] = height;
                    }
                    result.add(curLine);
                }
            }
        }
        return result;
    }
}
