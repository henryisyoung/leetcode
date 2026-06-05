package airbnb.New2026;
/*
Maze - Minimum Moves with Jump Distance k.

Grid n*m with 0 = empty, 1 = obstacle.  Start at (0,0), target (n-1,m-1).
In one move HackerMan can travel up to k cells in any one of the four
cardinal directions, but the entire path between start and end of the
jump must be empty (no obstacles).  Return the minimum number of moves
to reach (n-1, m-1), or -1 if unreachable.

Examples
  maze = [[0,0],[1,0]],            k = 2  ->  2
  maze = [[0,0,0],[0,0,0],[0,0,0]], k = 100 -> 1   (one big jump if same row/col)
  maze = [[0,1,0],[0,1,0],[0,1,0]], k = 1   -> -1  (column of obstacles)
  maze[0][0] == 1                  ->  -1
  start == target (1x1 with 0)     ->  0

Constraints
  1 <= n, m, k <= 100
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
Algorithm: plain BFS — every legal move has weight 1.

  Each cell has up to 4 * k "neighbors" (1..k steps in each direction
  before hitting a wall or obstacle).  We mark visited on enqueue, not
  on dequeue, so each cell is queued at most once.

  Walking a direction:
    Start from (r, c), step one cell at a time.  As soon as we hit a
    grid boundary or an obstacle, stop the WHOLE direction — every
    further x is also unreachable from this jump (path is blocked).
    Each empty cell visited in the walk is a valid landing spot that
    counts as a single move from (r, c); enqueue it if unvisited.

  Worst-case work per BFS pop: O(4 * k).  Total: O(n * m * k).
  At n, m, k <= 100 that's <= 4 * 10^6 cell touches.  Plenty fast.

  An optimization we DON'T need at these constraints (mentioned for
  completeness): once you walk past a cell already visited in the
  same direction, the rest of that direction was already reachable
  from that cell with the same or fewer moves, so you can stop.
  That turns the per-cell work amortized O(4), total O(n * m).

Why standard BFS suffices (not 0-1 BFS or Dijkstra):
  All edges have unit weight.  Length-of-jump doesn't change the cost.

Correctness of "mark visited on enqueue":
  BFS visits cells in non-decreasing distance order, so the first time
  a cell is reached is via a shortest path.  Re-queueing later costs
  more, never less, so we skip it.
*/
public class MazeMinimumJumpMoves {

    public int getMinimumMoves(int[][] maze, int k) {
        int rows = maze.length, cols = maze[0].length;
        if (maze[0][0] == 1 || maze[rows - 1][cols - 1] == 1) return -1;
        if (rows == 1 && cols == 1) return 0;             // start == target

        int[][] dist = new int[rows][cols];
        for (int[] row : dist) Arrays.fill(row, -1);
        dist[0][0] = 0;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});

        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            int d = dist[r][c];
            for (int[] dir : dirs) {
                int nr = r, nc = c;
                for (int step = 1; step <= k; step++) {
                    nr += dir[0];
                    nc += dir[1];

                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || maze[nr][nc] == 1) {
                        break;                            // boundary / obstacle blocks the rest of this jump
                    }
                    if (dist[nr][nc] != -1) {
                        continue;                         // already reached (>= shorter); keep walking the clear path
                    }
                    if (nr == rows - 1 && nc == cols - 1) {
                        return d + 1;
                    }
                    dist[nr][nc] = d + 1;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        return -1;
    }
}
