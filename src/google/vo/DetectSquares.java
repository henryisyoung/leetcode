package google.vo;

import java.util.*;

public class DetectSquares {
    class Point{
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;
            Point pair = (Point) o;
            return x == pair.x && y == pair.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private Map<Point, Integer> freqMap;
    private Map<Integer, Set<Integer>> rowMap;

    public DetectSquares() {
        this.freqMap = new HashMap<>();
        this.rowMap = new HashMap<>();
    }

    public void add(int[] point) {
        Point p = new Point(point[0], point[1]);
        freqMap.put(p, freqMap.getOrDefault(p, 0) + 1);
        rowMap.computeIfAbsent(p.x, k -> new HashSet<>()).add(p.y);
    }

    public int count(int[] point) {
        int x = point[0], y = point[1];
        int total = 0;

        if (rowMap.containsKey(x)) {
            for (int candidateY : rowMap.get(x)) {
                if (candidateY == y) continue;
                int size = Math.abs(y - candidateY);
                Point p1 = new Point(x, candidateY);

                for (int i = -1; i <= 1; i += 2) {
                    int candidateX = x + (size * i);
                    Point p2 = new Point(candidateX, y);
                    Point p3 = new Point(candidateX, candidateY);

                    if (freqMap.containsKey(p2) && freqMap.containsKey(p3)) {
                        total += freqMap.get(p1) * freqMap.get(p2) * freqMap.get(p3);
                    }
                }
            }
        }
        return total;
    }
}
