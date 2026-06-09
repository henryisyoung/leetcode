package airbnb.New2026;
/*
LeetCode 864: Shortest Path to Get All Keys.

Grid cells:
  '.'        empty cell, walkable
  '#'        wall, never walkable
  '@'        unique starting cell
  'a'..'f'   keys (1..6 of them, contiguous from 'a')
  'A'..'F'   locks; walkable only if you've already picked up the matching key

Move 4-directionally.  Return the minimum number of moves to collect EVERY
key on the board, or -1 if no order achieves that.

Why this isn't plain BFS over (row, col)
  The walkability of a lock cell DEPENDS on the set of keys you've picked up.
  Two visits to the same (row, col) with different key sets are NOT
  equivalent -- one might be able to walk through a lock that the other
  cannot.  The state must include WHICH keys you carry.

State
  (row, col, keys) where `keys` is a TreeSet<Character> of the lowercase
  key letters you currently hold.  TreeSet keeps a deterministic
  iteration order, which gives every distinct keychain a unique
  fingerprint string for the visited check.

  The classic LC encoding for this problem is a 6-bit bitmask; that's
  faster but less obvious to read.  We use a Set here for clarity --
  same algorithm, same complexity class.

Algorithm -- BFS over (row, col, keys)
  Edges all cost 1, so unweighted BFS gives the shortest path.

  1. Scan the grid: find the '@' start and count distinct keys k.
  2. Push (start, empty set) into the queue with dist 0.
  3. Pop and expand:
       For each of 4 neighbors:
         - If wall -> skip.
         - If lock 'A'..'F' and the matching lowercase key is NOT in the
           keychain -> skip.
         - Otherwise compute newKeys:
              if cell is a key not yet collected, copy + add it
              else reuse the parent set (no allocation)
         - If newKeys.size() == k, answer is dist + 1.
         - Else if fingerprint(nr, nc, newKeys) not visited, mark + enqueue.

Why mark visited on PUSH (not on POP)
  Standard BFS for unit-weight edges: once a state has been queued, any
  later path to that exact state is at least as long, so re-queueing it
  cannot improve the answer.  Marking on push prevents both duplicate work
  AND duplicate queue entries -- same correctness, less memory.

Why we can return as soon as we step on the last key
  Picking up the last key happens AT the moment we walk onto its cell, not
  one step later.  So the move that produces a complete keychain IS the
  finishing move; report dist + 1.

Why TreeSet (not HashSet) for keys
  Two states are equivalent iff they hold the same SET of keys.  A
  HashSet has the right equals/hashCode contract for that, but to build
  a STABLE fingerprint string for `seen` we need a deterministic
  iteration order, hence TreeSet.

  When a step doesn't grab a new key, we REUSE the parent set (treating
  it as immutable) -- no allocation in the common case.  Only the rarer
  "step onto a new key" branch allocates a fresh TreeSet copy.

Complexity
  Let m, n = grid dimensions, k = number of keys (<= 6).
  Distinct keychains: 2^k.  Total states: m * n * 2^k.
  For LC limits m,n <= 30 and k <= 6 -> 30*30*64 = 57.6K states.

  Time:   O(m * n * 2^k * k)   the * k comes from set ops and fingerprint cost
  Space:  O(m * n * 2^k)       for the visited set + queue
*/

import java.util.*;

public class ShortestPathToGetAllKeys {

    public int shortestPathAllKeys(String[] grid) {
        int level = 0;
        int count = 0;
        int sr = 0, sc = 0;
        int rows = grid.length, cols = grid[0].length();
        char[][] board = new char[rows][cols];
        for (int i = 0; i < rows; i++) board[i] = grid[i].toCharArray();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cur = board[i][j];
                if (cur >= 'a' && cur <= 'f') count++;
                if (cur == '@') {
                    sr = i; sc = j;
                }
            }
        }
        if (count == 0) {
            return 0;
        }
        int end = (1 << count) - 1;

        Queue<int[]> queue = new LinkedList<>(); // r c keys_state
        boolean[][][] visited = new boolean[rows][cols][1 << count];
        queue.add(new int[]{sr, sc, 0});
        visited[sr][sc][0] = true;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                if (cur[2] == end) {
                    return level;
                }
                for (int[] dir : dirs) {
                    int nr = cur[0] + dir[0], nc = cur[1] + dir[1], state = cur[2];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] != '#') {
                        char nextChar = board[nr][nc];
                        if (nextChar >= 'a' && nextChar <= 'f') {
                            int nextState = (1 << (nextChar - 'a')) | state;
                            if (!visited[nr][nc][nextState]) {
                                int[] arr = {nr, nc, nextState};
                                queue.add(arr);
                                visited[nr][nc][nextState] = true;
                            }
                        } else if (nextChar >= 'A' && nextChar <= 'F') {
                            boolean hasKey = (state >> (nextChar - 'A') & 1) == 1;
                            if (hasKey && !visited[nr][nc][state]) {
                                int[] arr = {nr, nc, state};
                                queue.add(arr);
                                visited[nr][nc][state] = true;
                            }
                        } else {
                            if (!visited[nr][nc][state]) {
                                int[] arr = {nr, nc, state};
                                queue.add(arr);
                                visited[nr][nc][state] = true;
                            }
                        }
                    }
                }
            }
            level++;
        }
        return -1;
    }
}
