package apple;

import java.util.*;

public class PerfectRectangle {
    public boolean isRectangleCover(int[][] rectangles) {
        PriorityQueue<Point> pq = new PriorityQueue<>();
        int low = Integer.MAX_VALUE, high = Integer.MIN_VALUE;

        for (int[] rect : rectangles) {
            Point a = new Point(rect[0], rect), b = new Point(rect[2], rect);
            pq.add(a);
            pq.add(b);
            low = Math.min(rect[1], low);
            high = Math.max(rect[3], high);
        }

        TreeSet<int[]> set = new TreeSet<int[]> (new Comparator<int[]> () {
            @Override
            // if two y-intervals intersects, return 0
            public int compare (int[] rect1, int[] rect2) {
                if (rect1[3] <= rect2[1]) return -1;
                else if (rect2[3] <= rect1[1]) return 1;
                else return 0;
            }
        });
        int height = 0;
        while (!pq.isEmpty()) {
            int x = pq.peek().x;
            while (!pq.isEmpty() && pq.peek().x == x) {
                Point point = pq.peek();
                if(x == point.rect[0]) {
                    if (!set.add(point.rect)) return false;
                    height += point.rect[3] - point.rect[1];
                } else {
                    set.remove(point.rect);
                    height -= point.rect[3] - point.rect[1];
                }
            }
            if (!pq.isEmpty() && height != high - low) return false;
        }

        return true;
    }

    private class Point implements Comparable<Point>{
        int x;
        int[] rect;
        public Point(int x, int[] rect) {
            this.rect = rect;
            this.x = x;
        }

        @Override
        public int compareTo(Point o) {
            if (this.x != o.x) return this.x - o.x;
            return this.rect[0] - o.rect[0];
        }
    }

    public static void main(String[] args) {

    }
}
