package waymo;
/*
LeetCode 499: The Maze III.

A ball sits in an m x n maze (0 = empty, 1 = wall). It can roll up, down,
left, or right but does NOT stop until it either hits a wall or falls into
the hole. There is exactly one hole. Given the ball's start position and
the hole's position:

  - Return the shortest *distance* (number of empty cells traveled, NOT
    counting the start, INCLUDING the cell with the hole) the ball must
    roll to fall into the hole, expressed as the move string ("dlru").
  - If multiple shortest paths exist, return the lexicographically
    smallest move string. Direction order: 'd' < 'l' < 'r' < 'u'.
  - Return "impossible" if no path exists.

Examples
  maze=[[0,0,0,0,0],
        [1,1,0,0,1],
        [0,0,0,0,0],
        [0,1,0,0,1],
        [0,1,0,0,0]],  ball=[4,3],  hole=[0,1]   -> "lul"
  maze=[[0,0,0,0,0],
        [1,1,0,0,1],
        [0,0,0,0,0],
        [0,1,0,0,1],
        [0,1,0,0,0]],  ball=[4,3],  hole=[3,0]   -> "impossible"

Constraints
  1 <= m, n <= 100
  ball.length == hole.length == 2
  0 <= ballrow, holerow < m
  0 <= ballcol, holecol < n
  Start and hole are empty cells; ball != hole; the maze border may be
  walls or empty.
*/

import java.util.Arrays;
import java.util.PriorityQueue;

/*
Algorithm: Dijkstra over the implicit graph of "rolling stops".

  State per node: (row, col).  An edge from (r, c) goes in direction d
  and ends at the cell where the ball stops — either the cell adjacent
  to a wall, or the hole itself if the ball passes over it while rolling.

  Edge weight: number of cells rolled (each step = 1).  Path label: the
  single direction character 'd' / 'l' / 'r' / 'u'.

  We run Dijkstra from the ball's start with a PQ ordered by:
    (1) total distance ascending
    (2) move string lexicographic ascending
  This guarantees the first time we pop the hole we have the shortest,
  lexicographically-smallest path.

  Direction order is chosen so 'd', 'l', 'r', 'u' is *also* the
  lexicographic order of those characters. That keeps tie-breaking
  consistent with the priority queue comparator.

  Why a 2D dist[][] is enough:
    Dijkstra's invariant: the first finalized cost to a node is optimal.
    For ties on cost, we use the path string in the PQ comparator, so
    the first pop of (r, c) is also the lex-smallest path to (r, c).
    Any later PQ entry for the same (r, c) is either strictly worse on
    distance or worse-or-equal on path; the `d > dist[r][c]` check skips
    strictly-worse, and an `==` with a worse path can't dethrone the
    already-popped state — so we additionally skip if a better path is
    already recorded (path.length() check below).

  Rolling helper:
    Roll from (r, c) in direction d. At each step, if the *next* cell
    is the hole, return immediately with the in-hole position. Else
    if the next cell is a wall or out of bounds, stop at the current
    cell. Otherwise advance.

  "impossible":
    PQ drains without popping the hole.

Complexity
  Let V = m * n. Each cell yields up to 4 rolling edges. Rolling itself
  is bounded by max(m, n) per edge, so building edges is amortized O(V).
  PQ operations dominate: O(V log V) comparisons × O(V) string-compare
  cost in the worst case. Plenty fast for m, n <= 100.
*/
public class TheMazeIII {

    // Order matters: d < l < r < u lex, AND we use this same index order
    // when pushing rolls so equal-cost ties pick the smaller letter first.
    private static final int[][] DIRS = {
            { 1,  0},  // d
            { 0, -1},  // l
            { 0,  1},  // r
            {-1,  0},  // u
    };
    private static final char[] LABELS = {'d', 'l', 'r', 'u'};

    public String findShortestWay(int[][] maze, int[] ball, int[] hole) {
        if (maze == null || maze.length == 0 || maze[0].length == 0) return "impossible";
        int m = maze.length, n = maze[0].length;

        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        String[][] best = new String[m][n];
        dist[ball[0]][ball[1]] = 0;
        best[ball[0]][ball[1]] = "";

        // Entry: {row, col, distance, path}.  Compare by distance, then path string.
        PriorityQueue<Object[]> pq = new PriorityQueue<>((a, b) -> {
            int cmp = Integer.compare((int) a[2], (int) b[2]);
            if (cmp != 0) return cmp;
            return ((String) a[3]).compareTo((String) b[3]);
        });
        pq.offer(new Object[]{ball[0], ball[1], 0, ""});

        while (!pq.isEmpty()) {
            Object[] cur = pq.poll();
            int r = (int) cur[0], c = (int) cur[1], d = (int) cur[2];
            String path = (String) cur[3];

            if (r == hole[0] && c == hole[1]) return path;

            // Stale skip: a cheaper-or-lex-better state for (r, c) was already finalized.
            if (d > dist[r][c]) continue;
            if (d == dist[r][c] && best[r][c] != null && best[r][c].compareTo(path) < 0) continue;

            for (int k = 0; k < 4; k++) {
                int[] stop = roll(maze, r, c, DIRS[k][0], DIRS[k][1], hole, m, n);
                int nr = stop[0], nc = stop[1], steps = stop[2];
                if (steps == 0) continue;                       // already against a wall in that direction
                int nd = d + steps;
                String np = path + LABELS[k];

                // Relax: cheaper, or same cost with a lex-smaller path.
                if (nd < dist[nr][nc]
                        || (nd == dist[nr][nc] && (best[nr][nc] == null || np.compareTo(best[nr][nc]) < 0))) {
                    dist[nr][nc] = nd;
                    best[nr][nc] = np;
                    pq.offer(new Object[]{nr, nc, nd, np});
                }
            }
        }
        return "impossible";
    }

    /**
     * Roll from (r, c) in direction (dr, dc).  Stops at the hole if encountered,
     * else at the last empty cell before a wall / boundary.
     * Returns {endRow, endCol, stepsTraveled}.
     */
    private int[] roll(int[][] maze, int r, int c, int dr, int dc, int[] hole, int m, int n) {
        int steps = 0;
        while (true) {
            int nr = r + dr, nc = c + dc;
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || maze[nr][nc] == 1) break;
            r = nr; c = nc; steps++;
            if (r == hole[0] && c == hole[1]) break;            // fell in mid-roll
        }
        return new int[]{r, c, steps};
    }

    /* --------------------------- demo --------------------------- */

    public static void main(String[] args) {
        TheMazeIII solver = new TheMazeIII();

        int[][] maze1 = {
                {0,0,0,0,0},
                {1,1,0,0,1},
                {0,0,0,0,0},
                {0,1,0,0,1},
                {0,1,0,0,0},
        };
        check(solver, maze1, new int[]{4,3}, new int[]{0,1}, "lul");
        check(solver, maze1, new int[]{4,3}, new int[]{3,0}, "impossible");

        int[][] maze2 = {
                {0,0,0,0,0,0,0},
                {0,0,1,0,0,1,0},
                {0,0,0,0,1,0,0},
                {0,0,0,0,0,0,1},
        };
        // Sanity: ball at start, hole reachable. Just ensures it terminates and is non-empty.
        String s = solver.findShortestWay(maze2, new int[]{0,0}, new int[]{3,5});
        System.out.println("maze2 path=" + s);

        // Hole directly to the right of the ball, no walls.
        check(solver, new int[][]{{0,0,0,0}}, new int[]{0,0}, new int[]{0,3}, "r");

        // Tiny 2x2 open pocket; "dr" and "rd" both cost 2, lex tie -> "dr".
        int[][] maze3 = {
                {1,1,1},
                {1,0,0},
                {1,0,0},
        };
        check(solver, maze3, new int[]{1,1}, new int[]{2,2}, "dr");

        // Lex-tie test: two equal-length paths, expect the lexicographically smaller.
        // 3x3 open maze, ball top-left, hole bottom-right.
        // Paths of equal cost: "rd" and "dr" — expect "dr".
        check(solver, new int[][]{
                {0,0,0},
                {0,0,0},
                {0,0,0},
        }, new int[]{0,0}, new int[]{2,2}, "dr");
    }

    private static void check(TheMazeIII solver, int[][] maze, int[] ball, int[] hole, String expected) {
        String got = solver.findShortestWay(maze, ball, hole);
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ")
                + "ball=" + Arrays.toString(ball) + " hole=" + Arrays.toString(hole)
                + " expected=\"" + expected + "\" got=\"" + got + "\"");
    }
}
