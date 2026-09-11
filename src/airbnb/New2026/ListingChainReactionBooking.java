package airbnb.New2026;

/*
Listing Chain Reaction Booking

Given a list of listings:
  listings[i] = [xi, yi, ri]

where (xi, yi) is the listing coordinate and ri is its chain-reaction radius.

If listing A is booked, every other listing inside A's radius is also booked.
Those newly booked listings then trigger their own radius and continue the
chain reaction.

If we can initially book exactly one listing, return the maximum number of
listings that can eventually be booked.

This is the same graph shape as "detonate bombs":
  listing i -> listing j if distance(i, j) <= ri

Example:
  [[1,2,3], [5,2,2], [3,4,1]]

  From listing 0: distance to listing 2 is sqrt(8) <= 3, so 0 triggers 2.
  Listing 2 may then trigger others within radius 1.
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/*
Algorithm:
  1. Build a directed graph.
     Edge i -> j exists if listing j is within listing i's radius.

  2. For each possible starting listing, run BFS/DFS and count how many nodes
     are reachable.

  3. Return the maximum reachable count.

Why directed:
  Radius belongs to the source listing. If j is inside i's radius, i can trigger
  j. But i may not be inside j's radius, so j may not trigger i.

Avoid floating point:
  Compare squared distance:
      dx*dx + dy*dy <= r*r

Complexity:
  Build graph: O(n^2)
  Try every start with BFS: O(n * (n + e)), worst-case O(n^3)
  For typical interview n, this is fine. If needed, SCC compression can optimize.
*/
public class ListingChainReactionBooking {

    public int maxBookedListings(int[][] listings) {
        if (listings == null || listings.length == 0) return 0;

        int n = listings.length;
        List<List<Integer>> graph = buildGraph(listings);

        int best = 0;
        for (int start = 0; start < n; start++) {
            best = Math.max(best, bfsCount(graph, start));
        }
        return best;
    }

    private List<List<Integer>> buildGraph(int[][] listings) {
        int n = listings.length;
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            long x1 = listings[i][0];
            long y1 = listings[i][1];
            long r = listings[i][2];
            long radiusSq = r * r;

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                long dx = x1 - listings[j][0];
                long dy = y1 - listings[j][1];
                long distSq = dx * dx + dy * dy;
                if (distSq <= radiusSq) {
                    graph.get(i).add(j);
                }
            }
        }
        return graph;
    }

    private int bfsCount(List<List<Integer>> graph, int start) {
        boolean[] seen = new boolean[graph.size()];
        Queue<Integer> queue = new ArrayDeque<>();
        seen[start] = true;
        queue.offer(start);

        int count = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            count++;
            for (int next : graph.get(cur)) {
                if (!seen[next]) {
                    seen[next] = true;
                    queue.offer(next);
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        ListingChainReactionBooking solver = new ListingChainReactionBooking();

        check(solver, new int[][]{{1, 2, 3}, {5, 2, 2}, {3, 4, 1}}, 2);

        // 0 triggers 1, 1 triggers 2, 2 triggers 3: all four can be booked.
        check(solver, new int[][]{{0, 0, 2}, {2, 0, 2}, {4, 0, 2}, {6, 0, 1}}, 4);

        // Direction matters: listing 1 has tiny radius, but listing 0 can trigger it.
        check(solver, new int[][]{{0, 0, 10}, {9, 0, 1}, {20, 0, 1}}, 2);

        // No listing can trigger another.
        check(solver, new int[][]{{0, 0, 1}, {10, 10, 1}, {20, 20, 1}}, 1);

        // All at same coordinate: any start triggers everyone with radius 0.
        check(solver, new int[][]{{1, 1, 0}, {1, 1, 0}, {1, 1, 0}}, 3);

        System.out.println("All tests passed.");
    }

    private static void check(ListingChainReactionBooking solver, int[][] listings, int expected) {
        int actual = solver.maxBookedListings(listings);
        if (actual != expected) {
            throw new AssertionError(Arrays.deepToString(listings)
                    + " expected " + expected + " but got " + actual);
        }
        System.out.println(Arrays.deepToString(listings) + " -> " + actual);
    }
}
