package waymo;
/*
Maze Universal Sequence  (a.k.a. "synchronizing word for a maze").

Input
  A 2-D grid: '#' = wall, '.' = empty, 'E' = the single exit (E is empty too).
  E is reachable from every empty cell.

Robot model
  One command per step: U / D / L / R. If the target cell is a wall or off the
  grid, the robot STAYS; otherwise it moves one cell.

Output
  One fixed command sequence such that, starting from ANY empty cell and
  executing the whole sequence, the robot ends at E. The robot can't see the
  maze and doesn't know its start; the same string must work for all starts.

------------------------------------------------------------------------------
Why this is a "synchronizing word" problem
  Model the maze as a deterministic automaton: states = empty cells, alphabet =
  {U,D,L,R}, each letter is a TOTAL function on states (move-or-stay). We want a
  word w that maps the set of ALL states down to the single state E.

  Two key facts about applying a letter to a SET of positions:
   - The image size never grows (it's a function: |f(S)| <= |S|).
   - Once two robots occupy the same cell, they move identically forever — they
     stay merged. So "distinct occupied cells" is a non-increasing counter we
     can drive down to 1.

Two-phase construction
  PHASE 1 — synchronize to ONE cell.
    While more than one cell is occupied:
      - take any two occupied cells a, b,
      - find (BFS on the PAIR automaton) the shortest word that maps {a,b} to a
        common cell,
      - append it and apply it to the WHOLE occupied set.
    a and b merge, so the set strictly shrinks; merged robots never split, so
    we terminate in <= (#empty - 1) merges.

  PHASE 2 — funnel that single cell to E.
    Once every robot is on the same cell x, they move as one. Append the
    shortest path x -> E (follow the BFS-from-E distance gradient). Done.

Why NOT the naive "repeatedly walk the farthest robot to E"
  E is not absorbing (a robot at E leaves E on the next command). And moving the
  globally-chosen direction can push some OTHER robot to a strictly larger
  distance, so "max distance to E" is not monotone. Pairwise merging sidesteps
  both traps: merged-stays-merged is the only invariant we rely on.

Pair automaton
  State = an unordered pair of cells {p, q} (packed into a long). A letter maps
  {p,q} -> {move(p), move(q)}. Goal = any state with p == q. BFS gives the
  shortest merging word. States: O((HW)^2), edges x4.

Complexity (H x W grid, n = #empty cells)
  Phase 1: <= n-1 merges; each pair-BFS is O(n^2). Applying each merge word to
           the set is bounded by word length x set size. Polynomial overall.
  Phase 2: O(path length).
  Output length: worst case O(n^3)-ish; fine for interview-sized mazes.

We also include a verifier that simulates EVERY empty start through the produced
sequence and asserts all land on E, plus a randomized maze stress test.
------------------------------------------------------------------------------
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MazeUniversalSequence {

    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};
    private static final char[] CMD = {'U', 'D', 'L', 'R'};

    private final char[][] grid;
    private final int R, C;
    private final int exit;                 // packed exit cell
    private final List<Integer> emptyCells = new ArrayList<>();
    private final int[] dist;                // BFS distance from E over empty cells

    public MazeUniversalSequence(String[] rows) {
        R = rows.length;
        C = rows[0].length();
        grid = new char[R][];
        int e = -1;
        for (int i = 0; i < R; i++) {
            grid[i] = rows[i].toCharArray();
            for (int j = 0; j < C; j++) {
                if (grid[i][j] != '#') emptyCells.add(i * C + j);
                if (grid[i][j] == 'E') e = i * C + j;
            }
        }
        exit = e;
        dist = bfsFromExit();
    }

    /* ----------------------------- core ----------------------------- */

    public String solve() {
        Set<Integer> occupied = new HashSet<>(emptyCells);
        StringBuilder out = new StringBuilder();

        // Phase 1: merge everything into a single occupied cell.
        while (occupied.size() > 1) {
            var it = occupied.iterator();
            int a = it.next(), b = it.next();
            String word = mergeWord(a, b);
            if (word == null) {
                throw new IllegalStateException("maze is not synchronizable for pair " + a + "," + b);
            }
            out.append(word);
            occupied = applyWord(occupied, word);
        }

        // Phase 2: funnel the single remaining cell to E.
        int x = occupied.iterator().next();
        out.append(pathToExit(x));
        return out.toString();
    }

    /** Shortest command word that drives robots at a and b onto a common cell. */
    private String mergeWord(int a, int b) {
        if (a == b) return "";
        long start = pairKey(a, b);
        Map<Long, long[]> parent = new HashMap<>();   // key -> {prevKey, dir}
        Deque<Long> q = new ArrayDeque<>();
        q.add(start);
        parent.put(start, new long[]{Long.MIN_VALUE, -1});

        while (!q.isEmpty()) {
            long cur = q.poll();
            int p = (int) (cur >>> 32);
            int r = (int) (cur & 0xffffffffL);
            if (p == r) return reconstruct(parent, cur);

            for (int d = 0; d < 4; d++) {
                long nk = pairKey(move(p, d), move(r, d));
                if (!parent.containsKey(nk)) {
                    parent.put(nk, new long[]{cur, d});
                    q.add(nk);
                }
            }
        }
        return null;   // unreachable for a synchronizable maze
    }

    private String reconstruct(Map<Long, long[]> parent, long goal) {
        StringBuilder sb = new StringBuilder();
        long cur = goal;
        long[] pr = parent.get(cur);
        while (pr[0] != Long.MIN_VALUE) {
            sb.append(CMD[(int) pr[1]]);
            cur = pr[0];
            pr = parent.get(cur);
        }
        return sb.reverse().toString();
    }

    private Set<Integer> applyWord(Set<Integer> occupied, String word) {
        Set<Integer> cur = occupied;
        for (int i = 0; i < word.length(); i++) {
            int d = dirOf(word.charAt(i));
            Set<Integer> next = new HashSet<>();
            for (int cell : cur) next.add(move(cell, d));
            cur = next;
        }
        return cur;
    }

    /** Single robot at x: follow the distance gradient down to E. */
    private String pathToExit(int x) {
        StringBuilder sb = new StringBuilder();
        int cur = x;
        while (cur != exit) {
            int r = cur / C, c = cur % C;
            for (int d = 0; d < 4; d++) {
                int nr = r + DR[d], nc = c + DC[d];
                if (empty(nr, nc) && dist[nr * C + nc] == dist[cur] - 1) {
                    sb.append(CMD[d]);
                    cur = nr * C + nc;
                    break;
                }
            }
        }
        return sb.toString();
    }

    /* ----------------------------- helpers ----------------------------- */

    private boolean empty(int r, int c) {
        return r >= 0 && r < R && c >= 0 && c < C && grid[r][c] != '#';
    }

    /** Move-or-stay: returns the resulting packed cell. */
    private int move(int cell, int dir) {
        int r = cell / C, c = cell % C;
        int nr = r + DR[dir], nc = c + DC[dir];
        return empty(nr, nc) ? nr * C + nc : cell;
    }

    private static int dirOf(char ch) {
        switch (ch) {
            case 'U': return 0;
            case 'D': return 1;
            case 'L': return 2;
            default:  return 3;   // 'R'
        }
    }

    /** Pack an unordered pair (lo,hi) into a long; lo==hi means merged. */
    private static long pairKey(int a, int b) {
        int lo = Math.min(a, b), hi = Math.max(a, b);
        return ((long) lo << 32) | (hi & 0xffffffffL);
    }

    private int[] bfsFromExit() {
        int[] d = new int[R * C];
        java.util.Arrays.fill(d, -1);
        Deque<Integer> q = new ArrayDeque<>();
        d[exit] = 0;
        q.add(exit);
        while (!q.isEmpty()) {
            int cell = q.poll();
            int r = cell / C, c = cell % C;
            for (int k = 0; k < 4; k++) {
                int nr = r + DR[k], nc = c + DC[k];
                if (empty(nr, nc) && d[nr * C + nc] == -1) {
                    d[nr * C + nc] = d[cell] + 1;
                    q.add(nr * C + nc);
                }
            }
        }
        return d;
    }

    /** Simulate every empty start through `cmds`; true iff all land on E. */
    public boolean verify(String cmds) {
        for (int start : emptyCells) {
            int cur = start;
            for (int i = 0; i < cmds.length(); i++) cur = move(cur, dirOf(cmds.charAt(i)));
            if (cur != exit) return false;
        }
        return true;
    }

    /* ----------------------------- tests ----------------------------- */

    public static void main(String[] args) {
        check("single cell", new String[]{"E"});
        check("horizontal corridor", new String[]{"E...."});
        check("vertical corridor", new String[]{"E", ".", ".", "."});
        check("open 2x2", new String[]{"E.", ".."});
        check("E in the middle", new String[]{".....", "..E..", "....."});
        check("walls / rooms", new String[]{
                "E.#..",
                "..#..",
                "...#.",
                "#....",
                "....."
        });
        check("L-shape", new String[]{
                "E####",
                ".####",
                "....#",
                "###..",
                "####."
        });
        check("plus / cross", new String[]{
                "#.#",
                ".E.",
                "#.#"
        });

        // Randomized stress: carve a connected component around E, verify.
        Random rng = new Random(2026);
        int trials = 300, fails = 0;
        for (int t = 0; t < trials; t++) {
            String[] maze = randomConnectedMaze(rng);
            if (maze == null) continue;                  // no empty cell, skip
            MazeUniversalSequence solver = new MazeUniversalSequence(maze);
            String seq = solver.solve();
            if (!solver.verify(seq)) {
                fails++;
                if (fails <= 3) {
                    System.out.println("MISMATCH maze:");
                    for (String row : maze) System.out.println("  " + row);
                    System.out.println("  seq=" + seq);
                }
            }
        }
        System.out.println((fails == 0 ? "OK    " : "FAIL  ")
                + "random stress (" + trials + " mazes, fails=" + fails + ")");
    }

    private static void check(String label, String[] maze) {
        MazeUniversalSequence solver = new MazeUniversalSequence(maze);
        String seq = solver.solve();
        boolean ok = solver.verify(seq);
        System.out.println((ok ? "OK    " : "FAIL  ") + label
                + "  len=" + seq.length()
                + (ok ? "" : "  seq=" + seq));
    }

    /** Random maze whose empty cells are exactly the component reachable from E. */
    private static String[] randomConnectedMaze(Random rng) {
        int R = 2 + rng.nextInt(5), C = 2 + rng.nextInt(5);
        char[][] g = new char[R][C];
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++)
                g[i][j] = rng.nextInt(100) < 65 ? '.' : '#';

        // Pick an open cell as E; if none, bail.
        List<int[]> open = new ArrayList<>();
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++)
                if (g[i][j] == '.') open.add(new int[]{i, j});
        if (open.isEmpty()) return null;
        int[] e = open.get(rng.nextInt(open.size()));
        g[e[0]][e[1]] = 'E';

        // Keep only the component reachable from E; wall off the rest so the
        // precondition (E reachable from every empty cell) holds.
        boolean[][] seen = new boolean[R][C];
        Deque<int[]> q = new ArrayDeque<>();
        q.add(e);
        seen[e[0]][e[1]] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            for (int k = 0; k < 4; k++) {
                int nr = cur[0] + DR[k], nc = cur[1] + DC[k];
                if (nr >= 0 && nr < R && nc >= 0 && nc < C && !seen[nr][nc] && g[nr][nc] != '#') {
                    seen[nr][nc] = true;
                    q.add(new int[]{nr, nc});
                }
            }
        }
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++)
                if (!seen[i][j]) g[i][j] = '#';

        String[] rows = new String[R];
        for (int i = 0; i < R; i++) rows[i] = new String(g[i]);
        return rows;
    }
}
