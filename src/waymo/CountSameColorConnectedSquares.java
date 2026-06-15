package waymo;
/*
Count Same-Color Squares in an Unbounded Grid  (phone screen)

Input
  A set of colored cells on a 2-D grid. Each cell is (x, y) -> color, where color
  is one of a small fixed set (e.g. A / B / C / D). There is NO fixed grid size,
  so the grid is represented as a sparse map of occupied cells, not a dense matrix.

Output
  The number of axis-aligned squares formed by connected same-color cells.
  A "square" here = a connected region (4-directional adjacency, same color) whose
  cells exactly fill a k x k bounding box (k >= 1).

Approach -- connected components, then square test
  1. Group cells into connected components using 4-dir BFS over the sparse map,
     only walking between neighbors that share the same color.
  2. For each component, compute the bounding box (minX..maxX, minY..maxY) and:
       - width  == height                  (bounding box is a square)
       - cellCount == width * height       (no holes / not an L-shape / rectangle)
     Both must hold for the component to be a solid square.

  Storing cells in a HashMap<(x,y), color> keeps everything O(N) in the number of
  occupied cells regardless of how far apart they are -- a dense matrix would be
  impossible for an unbounded grid.

Why the two checks
  Connectivity alone is not enough: an L-shape or a rectangle is connected and
  single-color but is not a square. width==height rejects rectangles; the
  cellCount==area check rejects shapes with holes or notches that still fit in a
  square bounding box.

Edge cases
  single cell (1x1 square), duplicate coords, disconnected same-color clusters
  (counted separately), components that touch diagonally only (NOT connected).

Complexity
  O(N) time and space in the number of occupied cells (each visited once in BFS).

Dynamic / online version
  Use DynamicSquareCounter below. It is a union-find that supports adding cells
  incrementally. Each component stores its color, size, and bounding box, so after
  every union it can re-check width == height && count == area.

Alternative (not used): enumerate (x, y, side) candidate squares and verify every
  interior cell exists with the right color -- O(N * maxSide), only competitive
  when squares are small.
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CountSameColorConnectedSquares {

    /** A colored cell on the sparse grid. */
    public static final class Cell {
        public final long x;
        public final long y;
        public final char color;

        public Cell(long x, long y, char color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    private static long key(long x, long y) {
        // pack two ints into a long; assumes coordinates fit in 32 bits
        return (x << 32) ^ (y & 0xffffffffL);
    }

    private static final long[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static int countSquares(List<Cell> cells) {
        if (cells == null || cells.isEmpty()) {
            return 0;
        }

        Map<Long, Character> grid = new HashMap<>();
        for (Cell c : cells) {
            grid.put(key(c.x, c.y), c.color); // last write wins on duplicate coords
        }

        // recover canonical coordinates per key for BFS bookkeeping
        Map<Long, long[]> coords = new HashMap<>();
        for (Cell c : cells) {
            coords.put(key(c.x, c.y), new long[] {c.x, c.y});
        }

        Set<Long> visited = new HashSet<>();
        int squares = 0;

        for (Map.Entry<Long, Character> e : grid.entrySet()) {
            long start = e.getKey();
            if (visited.contains(start)) {
                continue;
            }
            char color = e.getValue();

            long minX = Long.MAX_VALUE, maxX = Long.MIN_VALUE;
            long minY = Long.MAX_VALUE, maxY = Long.MIN_VALUE;
            int count = 0;

            Queue<Long> q = new ArrayDeque<>();
            q.add(start);
            visited.add(start);

            while (!q.isEmpty()) {
                long cur = q.poll();
                long[] xy = coords.get(cur);
                long x = xy[0], y = xy[1];
                count++;
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);

                for (long[] d : DIRS) {
                    long nx = x + d[0], ny = y + d[1];
                    long nk = key(nx, ny);
                    if (!visited.contains(nk)) {
                        Character nc = grid.get(nk);
                        if (nc != null && nc == color) {
                            visited.add(nk);
                            q.add(nk);
                        }
                    }
                }
            }

            long width = maxX - minX + 1;
            long height = maxY - minY + 1;
            if (width == height && (long) count == width * height) {
                squares++;
            }
        }

        return squares;
    }

    public static int countSquaresUnionFind(List<Cell> cells) {
        if (cells == null || cells.isEmpty()) {
            return 0;
        }

        // Match the batch BFS behavior: if input has duplicate coordinates,
        // the last occurrence wins before we build components.
        Map<Long, Cell> deduped = new HashMap<>();
        for (Cell c : cells) {
            deduped.put(key(c.x, c.y), c);
        }

        DynamicSquareCounter counter = new DynamicSquareCounter();
        for (Cell c : deduped.values()) {
            counter.addCell(c.x, c.y, c.color);
        }
        return counter.getSquareCount();
    }

    /**
     * Online union-find version for append-only sparse grids.
     *
     * addCell(x, y, color) updates only the new cell and its four neighbors. If
     * the new cell completes a previous L-shape into a solid square, the merged
     * component's bounding box/count are re-evaluated immediately.
     */
    public static final class DynamicSquareCounter {
        private final Map<Long, Long> parent = new HashMap<>();
        private final Map<Long, Integer> size = new HashMap<>();
        private final Map<Long, Character> color = new HashMap<>();
        private final Map<Long, Long> minX = new HashMap<>();
        private final Map<Long, Long> maxX = new HashMap<>();
        private final Map<Long, Long> minY = new HashMap<>();
        private final Map<Long, Long> maxY = new HashMap<>();
        private int squareCount = 0;

        public int addCell(long x, long y, char c) {
            long k = key(x, y);
            if (parent.containsKey(k)) {
                // Online version is append-only. Existing cells are ignored.
                return squareCount;
            }

            parent.put(k, k);
            size.put(k, 1);
            color.put(k, c);
            minX.put(k, x);
            maxX.put(k, x);
            minY.put(k, y);
            maxY.put(k, y);
            squareCount++; // every isolated cell is a 1x1 square

            for (long[] d : DIRS) {
                long nk = key(x + d[0], y + d[1]);
                if (parent.containsKey(nk) && color.get(find(nk)) == c) {
                    union(k, nk);
                }
            }

            return squareCount;
        }

        public int getSquareCount() {
            return squareCount;
        }

        private long find(long k) {
            long p = parent.get(k);
            if (p != k) {
                p = find(p);
                parent.put(k, p);
            }
            return p;
        }

        private void union(long a, long b) {
            long ra = find(a);
            long rb = find(b);
            if (ra == rb) {
                return;
            }

            if (isSquareRoot(ra)) {
                squareCount--;
            }
            if (isSquareRoot(rb)) {
                squareCount--;
            }

            if (size.get(ra) < size.get(rb)) {
                long tmp = ra;
                ra = rb;
                rb = tmp;
            }

            parent.put(rb, ra);
            size.put(ra, size.get(ra) + size.get(rb));
            minX.put(ra, Math.min(minX.get(ra), minX.get(rb)));
            maxX.put(ra, Math.max(maxX.get(ra), maxX.get(rb)));
            minY.put(ra, Math.min(minY.get(ra), minY.get(rb)));
            maxY.put(ra, Math.max(maxY.get(ra), maxY.get(rb)));

            if (isSquareRoot(ra)) {
                squareCount++;
            }
        }

        private boolean isSquareRoot(long root) {
            long width = maxX.get(root) - minX.get(root) + 1;
            long height = maxY.get(root) - minY.get(root) + 1;
            return width == height && (long) size.get(root) == width * height;
        }
    }

    public static void main(String[] args) {
        // 2x2 A square + a single B + an L-shape of C (not a square)
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(0, 0, 'A'));
        cells.add(new Cell(1, 0, 'A'));
        cells.add(new Cell(0, 1, 'A'));
        cells.add(new Cell(1, 1, 'A'));

        cells.add(new Cell(5, 5, 'B'));

        cells.add(new Cell(10, 10, 'C'));
        cells.add(new Cell(11, 10, 'C'));
        cells.add(new Cell(10, 11, 'C')); // L-shape, not a square

        System.out.println(countSquares(cells)); // expected 2 (A square + B single)
        System.out.println(countSquaresUnionFind(cells)); // expected 2

        DynamicSquareCounter counter = new DynamicSquareCounter();
        System.out.println(counter.addCell(0, 0, 'A')); // 1: one 1x1 square
        System.out.println(counter.addCell(1, 0, 'A')); // 0: 1x2 rectangle
        System.out.println(counter.addCell(0, 1, 'A')); // 0: L-shape
        System.out.println(counter.addCell(1, 1, 'A')); // 1: completed 2x2 square
    }
}
