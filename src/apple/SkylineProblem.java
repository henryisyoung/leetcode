package apple;

import java.util.*;

public class SkylineProblem {
    public List<List<Integer>> getSkyline(int[][] buildings) {
        List<List<Integer>> result = new ArrayList<>();
        if (buildings == null || buildings.length == 0|| buildings[0] == null || buildings[0].length == 0 ) return result;
        List<Point> points = new ArrayList<>();
        for (int[] build : buildings) {
            points.add(new Point(build[0], build[2], true));
            points.add(new Point(build[1], build[2], false));
        }
        Collections.sort(points, (a, b) ->{
            if (a.pos != b.pos) return a.pos -b.pos;
            if (a.isS == b.isS) {
                if (!a.isS) return a.h - b.h;
                return b.h - a.h;
            }
            if (a.isS) return -1;
            return 1;
        });
        PriorityQueue<Integer> pq = new PriorityQueue<>((a,b) -> (b-a));
        for (Point point : points) {
            if (point.isS) {
                if (pq.isEmpty() || point.h > pq.peek()) {
                    result.add(Arrays.asList(point.pos, point.h));
                }
                pq.add(point.h);
            } else {
                pq.remove(point.h);
                if (pq.isEmpty() || pq.peek() < point.h) {
                    if (pq.isEmpty()) result.add(Arrays.asList(point.pos, 0));
                    else result.add(Arrays.asList(point.pos, pq.peek()));
                }
            }
        }
        return result;
    }

    class Point{
        int pos, h;
        boolean isS;
        public Point(int pos, int h, boolean isS) {
            this.h = h;
            this.pos = pos;
            this.isS = isS;
        }
    }
}
