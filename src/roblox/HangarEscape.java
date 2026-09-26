package roblox;

import java.util.*;

/*
================================================================================
  HangarEscape — rooms from which you can never leave the rectangle
================================================================================

  GIVEN
  A rectangular grid of rooms. Each room has ONE exit direction, 'U' 'D' 'L'
  'R'. You escape by stepping off the rectangle. Count the rooms from which
  escape is impossible.

      hangar = [['U','L'],          (0,0) U -> off the top          escapes
                ['R','L']]          (0,1) L -> (0,0) -> off the top escapes
                                    (1,0) R -> (1,1) L -> (1,0)     trapped
      solution(hangar) = 2          (1,1) L -> (1,0) R -> (1,1)     trapped

  --------------------------------------------------------------------------
  THE ONE OBSERVATION THAT MAKES THIS EASY
  --------------------------------------------------------------------------
    Every room has EXACTLY ONE outgoing edge. That makes the grid a FUNCTIONAL
    GRAPH, not a maze, and it removes the entire search: from any room the
    walk is forced, so it can only do one of two things — step off the edge,
    or revisit a room and loop forever. There is no branching, no choice, no
    backtracking, and therefore no DFS over neighbours.

    So "cannot escape" is exactly "the forced walk enters a cycle", and the
    answer is the number of rooms whose walk does. Say this out loud before
    writing anything; it is the difference between 6 lines and 60.

    ⚠ EVERY ROOM ON A WALK THAT REACHES A CYCLE IS TRAPPED, not just the rooms
      IN the cycle. The tail leading into the loop is stuck too. Counting only
      the cycle is the most common wrong answer: on a random 60x60 hangar it
      reports 1,030 where the answer is 3,431, because the other 2,401 rooms
      are tails feeding into those loops.

  --------------------------------------------------------------------------
  Route A — one memoised iterative walk                     (countMemoised)
  --------------------------------------------------------------------------
    Walk from each unsettled room, pushing rooms onto a path as you go. Stop
    on one of three things: off the grid (the whole path escapes), a room
    already settled (the path inherits its answer), or a room on the CURRENT
    path (a cycle — the whole path is trapped). Then write the verdict back
    to every room on the path.

    Each room is pushed exactly once over the whole run, so this is O(m*n).

    ⚠ THREE STATES, NOT TWO. "On the current path" (VISITING) and "settled"
      (ESCAPES / TRAPPED) cannot be merged into one visited flag. Collapse
      them and you either stop at a room you are still resolving and call it
      escaped, or you re-walk settled rooms and go quadratic. This is the same
      distinction as `seen` vs `safe` in ReportChain.validateForest — the grey
      and black of a three-colour traversal, on a graph where every node has
      out-degree one.

    ⚠ ITERATIVE, NOT RECURSIVE. A snake-shaped hangar puts every room on one
      chain, so the recursion depth is m*n. Measured on snake grids with the
      default stack: recursion survives 100x100 (10,000 deep) and dies with
      StackOverflowError at 200x200; the iterative version handles 2000x2000
      (4,000,000 deep) without noticing. LeetCode-sized inputs hide this.

    ⚠ DO NOT MARK VISITED ROOMS BY OVERWRITING THE GRID. It is the usual
      trick, and here the characters ARE the edges — you would be deleting the
      graph while still walking it, and returning the caller's input corrupted.

  --------------------------------------------------------------------------
  Route B — walk from every room independently             (countPerRoom)
  --------------------------------------------------------------------------
    No memo: from each room, walk until you leave or repeat. Correct, trivial
    to justify, and Θ((m*n)²) on one long chain. Worth writing first only if
    you say the bound out loud. Measured on a snake hangar:

        n      rooms      Route A steps   Route B steps    ratio
        40      1,600          1,600        1,280,800        801x
        80      6,400          6,400       20,483,200       3201x
        160    25,600         25,600      327,692,800      12801x

  --------------------------------------------------------------------------
  Route C — reverse BFS from the exits                    (countReverseBfs)
  --------------------------------------------------------------------------
    Turn it around: seed a queue with every room whose single step already
    leaves the rectangle, then walk edges BACKWARD — a room is added when it
    points at a room already known to escape. Everything unreached is trapped.

    Also O(m*n), no cycle detection anywhere (cycles simply never get
    enqueued), and it needs no path stack. The reverse edges do not have to be
    built: to find the predecessors of (r,c), look at its four neighbours and
    keep the ones pointing back at it.

    This is the better whiteboard answer if you are nervous about the three
    colours, and it is a genuinely independent implementation — which is why
    main() uses it to cross-check Route A rather than trusting one method.

  --------------------------------------------------------------------------
  Questions worth asking
  --------------------------------------------------------------------------
    1. Can the grid be empty or null? Both return 0 here.
    2. Ragged rows? Not a rectangle; this throws rather than guessing.
    3. Any character other than UDLR? Throws. The alternative — treat it as a
       wall and therefore trapped — is equally defensible, but silently
       mapping it to a direction is not.
    4. Is the grid mine to modify? Assume not; none of these routes write to
       it, and main() asserts the input is unchanged.

  --------------------------------------------------------------------------
  Complexity   (R rows, C columns, N = R*C)
  --------------------------------------------------------------------------
    Route A   O(N) time, O(N) state + O(N) worst-case path stack
    Route B   O(N²) time, O(N) per walk
    Route C   O(N) time, O(N) visited + queue
================================================================================
*/
public class HangarEscape {

    private static final int UNKNOWN = 0, VISITING = 1, ESCAPES = 2, TRAPPED = 3;

    /** Step counter, so the routes can be compared by work done and not only by clock. */
    static long steps;

    /** The answer. */
    public static int solution(char[][] hangar) {
        return countMemoised(hangar);
    }

    /* ===================== Route A: memoised iterative walk ===================== */

    public static int countMemoised(char[][] hangar) {
        if (isEmpty(hangar)) return 0;
        int rows = hangar.length, cols = hangar[0].length;
        validate(hangar);

        int[] state = new int[rows * cols];
        int[] path = new int[rows * cols];               // reused; each room is pushed once
        int trapped = 0;

        for (int start = 0; start < rows * cols; start++) {
            if (state[start] != UNKNOWN) continue;

            int depth = 0, at = start, verdict;
            while (true) {
                steps++;
                int r = at / cols, c = at % cols;
                int nr = r + dr(hangar[r][c]), nc = c + dc(hangar[r][c]);

                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) { verdict = ESCAPES; break; }

                int next = nr * cols + nc;
                state[at] = VISITING;
                path[depth++] = at;

                if (state[next] == VISITING) { verdict = TRAPPED; break; }   // closed a loop
                if (state[next] != UNKNOWN)  { verdict = state[next]; break; } // settled already
                at = next;
            }
            if (verdict == ESCAPES) path[depth++] = at;

            for (int i = 0; i < depth; i++) state[path[i]] = verdict;
            if (verdict == TRAPPED) trapped += depth;
        }
        return trapped;
    }

    /* ================== Route B: independent walk from every room ================== */

    public static int countPerRoom(char[][] hangar) {
        if (isEmpty(hangar)) return 0;
        int rows = hangar.length, cols = hangar[0].length;
        validate(hangar);

        int trapped = 0;
        boolean[] seen = new boolean[rows * cols];
        for (int start = 0; start < rows * cols; start++) {
            Arrays.fill(seen, false);
            int r = start / cols, c = start % cols;
            while (true) {
                steps++;
                if (seen[r * cols + c]) { trapped++; break; }
                seen[r * cols + c] = true;
                int nr = r + dr(hangar[r][c]), nc = c + dc(hangar[r][c]);
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) break;
                r = nr; c = nc;
            }
        }
        return trapped;
    }

    /* ==================== Route C: reverse BFS from the exits ==================== */

    public static int countReverseBfs(char[][] hangar) {
        if (isEmpty(hangar)) return 0;
        int rows = hangar.length, cols = hangar[0].length;
        validate(hangar);

        boolean[] escapes = new boolean[rows * cols];
        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int nr = r + dr(hangar[r][c]), nc = c + dc(hangar[r][c]);
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                    escapes[r * cols + c] = true;
                    queue.add(r * cols + c);
                }
            }
        }

        int escaped = queue.size();
        int[][] around = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            int r = cur / cols, c = cur % cols;
            for (int[] d : around) {                     // predecessors, found without building them
                int pr = r + d[0], pc = c + d[1];
                if (pr < 0 || pr >= rows || pc < 0 || pc >= cols) continue;
                if (escapes[pr * cols + pc]) continue;
                steps++;
                if (pr + dr(hangar[pr][pc]) != r || pc + dc(hangar[pr][pc]) != c) continue;
                escapes[pr * cols + pc] = true;
                escaped++;
                queue.add(pr * cols + pc);
            }
        }
        return rows * cols - escaped;
    }

    /* ============================== shared helpers ============================== */

    private static boolean isEmpty(char[][] h) { return h == null || h.length == 0 || h[0].length == 0; }

    private static void validate(char[][] h) {
        for (char[] row : h)
            if (row.length != h[0].length)
                throw new IllegalArgumentException("hangar is not rectangular");
    }

    private static int dr(char d) {
        switch (d) {
            case 'U': return -1;
            case 'D': return 1;
            case 'L': case 'R': return 0;
            default: throw new IllegalArgumentException("not a direction: '" + d + "'");
        }
    }

    private static int dc(char d) {
        switch (d) {
            case 'L': return -1;
            case 'R': return 1;
            case 'U': case 'D': return 0;
            default: throw new IllegalArgumentException("not a direction: '" + d + "'");
        }
    }

    /* ================================== tests ================================== */

    public static void main(String[] args) {

        /* ------------------------------ the brief ------------------------------ */
        expect("the example", solution(grid("UL", "RL")), 2);
        expect("single room pointing out", solution(grid("U")), 0);
        expect("two rooms pointing at each other", solution(grid("RL")), 2);
        expect("a row that funnels off the left edge", solution(grid("LLLL")), 0);
        expect("a row that funnels off the right edge", solution(grid("RRRR")), 0);
        expect("every room points up", solution(grid("UU", "UU", "UU")), 0);
        expect("null hangar", solution(null), 0);
        expect("empty hangar", solution(new char[0][0]), 0);

        // A 2x2 rotating cycle: R D / U L, nobody leaves.
        expect("a 4-room cycle traps all four", solution(grid("RD", "UL")), 4);
        // The same cycle with a tail hanging off it: the tail is trapped too.
        expect("the tail into a cycle is trapped as well",
                solution(grid("RDL", "URL", "UUU")), 9);

        /* ------------------------ contract and immutability ------------------------ */
        expectThrows("ragged rows are not a rectangle",
                () -> solution(new char[][]{{'U', 'L'}, {'R'}}));
        expectThrows("an unknown character", () -> solution(grid("UX")));
        char[][] original = grid("UL", "RL");
        char[][] copy = grid("UL", "RL");
        solution(original);
        countReverseBfs(original);
        countPerRoom(original);
        expect("no route modifies the caller's grid", Arrays.deepEquals(original, copy), true);

        /* ------------- the three routes have to agree, on random input ------------- */
        agreementOverRandomGrids();

        /* ------------- the claims in the header, made runnable ------------- */
        cycleRoomsAreNotAllTrappedRooms();
        recursionDepthCeiling();
        routeBIsQuadratic();
    }

    /* ---------------------- the claims, measured ---------------------- */

    /**
     * Route A, Route C and a third method that shares nothing with either: simulate
     * each room for N+1 steps and call it trapped if it is still inside.
     */
    private static void agreementOverRandomGrids() {
        Random rnd = new Random(7);
        int trials = 200000, disagreeC = 0, disagreeBrute = 0, disagreeB = 0;
        long totalTrapped = 0;
        int allTrapped = 0, noneTrapped = 0;

        for (int t = 0; t < trials; t++) {
            int rows = 1 + rnd.nextInt(5), cols = 1 + rnd.nextInt(5);
            char[][] h = new char[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    h[r][c] = "UDLR".charAt(rnd.nextInt(4));

            int a = countMemoised(h);
            if (a != countReverseBfs(h)) disagreeC++;
            if (a != countPerRoom(h)) disagreeB++;
            if (a != bruteForce(h)) disagreeBrute++;

            totalTrapped += a;
            if (a == rows * cols) allTrapped++;
            if (a == 0) noneTrapped++;
        }
        System.out.printf("%n%d random hangars up to 5x5:%n", trials);
        System.out.printf("  Route A vs Route C (reverse BFS) : %d disagreements%n", disagreeC);
        System.out.printf("  Route A vs Route B (per room)    : %d disagreements%n", disagreeB);
        System.out.printf("  Route A vs step-cap brute force  : %d disagreements%n", disagreeBrute);
        System.out.printf("  coverage: %d fully trapped, %d fully escaping, %d trapped rooms total%n",
                allTrapped, noneTrapped, totalTrapped);
    }

    /** Fourth method: no memo, no cycle detection, just a step budget. */
    private static int bruteForce(char[][] h) {
        int rows = h.length, cols = h[0].length, budget = rows * cols + 1, trapped = 0;
        for (int start = 0; start < rows * cols; start++) {
            int r = start / cols, c = start % cols, left = budget;
            while (left-- > 0) {
                int nr = r + dr(h[r][c]), nc = c + dc(h[r][c]);
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) break;
                r = nr; c = nc;
            }
            if (left < 0) trapped++;                  // still inside after N+1 steps: looping
        }
        return trapped;
    }

    /** Counting only the rooms inside a cycle is a different, smaller number. */
    private static void cycleRoomsAreNotAllTrappedRooms() {
        Random rnd = new Random(11);
        int n = 60;
        char[][] h = new char[n][n];
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                h[r][c] = "UDLR".charAt(rnd.nextInt(4));

        int trapped = countMemoised(h);
        int inCycle = roomsInsideACycle(h);
        System.out.printf("%non a random %dx%d hangar:%n", n, n);
        System.out.printf("  rooms that cannot escape       %d%n", trapped);
        System.out.printf("  rooms lying ON a cycle         %d%n", inCycle);
        System.out.printf("  the other %d are the tails feeding into those loops (%.1fx)%n",
                trapped - inCycle, trapped / (double) inCycle);
    }

    private static int roomsInsideACycle(char[][] h) {
        int rows = h.length, cols = h[0].length;
        Set<Integer> onACycle = new HashSet<>();
        for (int start = 0; start < rows * cols; start++) {
            LinkedHashMap<Integer, Integer> order = new LinkedHashMap<>();
            int r = start / cols, c = start % cols;
            while (true) {
                int at = r * cols + c;
                Integer seenAt = order.get(at);
                if (seenAt != null) {                       // the loop is everything from seenAt on
                    int i = 0;
                    for (int node : order.keySet()) if (i++ >= seenAt) onACycle.add(node);
                    break;
                }
                order.put(at, order.size());
                int nr = r + dr(h[r][c]), nc = c + dc(h[r][c]);
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) break;
                r = nr; c = nc;
            }
        }
        return onACycle.size();
    }

    /** A snake hangar is one chain through every room, so recursion depth is R*C. */
    private static void recursionDepthCeiling() {
        System.out.println("\nsnake hangar (every room on one chain), recursive vs iterative:");
        for (int n : new int[]{100, 200, 500, 2000}) {
            char[][] h = snake(n, n, false);
            String recursive;
            try {
                recursive = "ok, answer " + countRecursive(h);
            } catch (StackOverflowError e) {
                recursive = "StackOverflowError";
            }
            System.out.printf("  %4dx%-4d  %,10d rooms   recursive: %-20s iterative: ok, answer %d%n",
                    n, n, n * n, recursive, countMemoised(h));
        }
    }

    /** The natural recursive phrasing of Route A, present only to show where it dies. */
    private static int countRecursive(char[][] h) {
        int rows = h.length, cols = h[0].length;
        int[] state = new int[rows * cols];
        int trapped = 0;
        for (int i = 0; i < rows * cols; i++) if (resolve(h, state, i / cols, i % cols)) trapped++;
        return trapped;
    }

    private static boolean resolve(char[][] h, int[] state, int r, int c) {
        int rows = h.length, cols = h[0].length, at = r * cols + c;
        if (state[at] == VISITING) return true;                       // back on the current chain
        if (state[at] != UNKNOWN) return state[at] == TRAPPED;
        state[at] = VISITING;
        int nr = r + dr(h[r][c]), nc = c + dc(h[r][c]);
        boolean stuck = !(nr < 0 || nr >= rows || nc < 0 || nc >= cols) && resolve(h, state, nr, nc);
        state[at] = stuck ? TRAPPED : ESCAPES;
        return stuck;
    }

    /** Route B re-walks the whole chain from every room. */
    private static void routeBIsQuadratic() {
        System.out.println("\nsteps taken on a snake hangar (one chain through every room):");
        System.out.printf("  %-6s %-10s %-16s %-18s %s%n", "n", "rooms", "Route A steps", "Route B steps", "ratio");
        for (int n : new int[]{40, 80, 160}) {
            char[][] h = snake(n, n, false);
            steps = 0; countMemoised(h);  long a = steps;
            steps = 0; countPerRoom(h);   long b = steps;
            System.out.printf("  %-6d %,-10d %,-16d %,-18d %.0fx%n", n, n * n, a, b, b / (double) a);
        }

        char[][] looped = snake(200, 200, true);
        System.out.printf("  a snake whose last room points back into the chain: %,d of %,d trapped%n",
                countMemoised(looped), 200 * 200);
    }

    /**
     * Rows alternate direction and drop down at the end, so every room sits on one
     * chain. loopBack makes the final room point back into it, trapping everything.
     */
    private static char[][] snake(int rows, int cols, boolean loopBack) {
        char[][] h = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            boolean rightward = (r % 2 == 0);
            Arrays.fill(h[r], rightward ? 'R' : 'L');
            if (r < rows - 1) h[r][rightward ? cols - 1 : 0] = 'D';
        }
        if (loopBack) {
            boolean rightward = ((rows - 1) % 2 == 0);
            h[rows - 1][rightward ? cols - 1 : 0] = 'U';       // back into the row above
        }
        return h;
    }

    /* ------------------------------- helpers ------------------------------- */

    private static char[][] grid(String... rows) {
        char[][] h = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) h[i] = rows[i].toCharArray();
        return h;
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }

    private static void expectThrows(String label, Runnable r) {
        try {
            r.run();
            System.out.println("FAIL " + label + "\n  expected an exception, none thrown");
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("OK   " + label + " (" + e.getClass().getSimpleName() + ")");
        }
    }
}
