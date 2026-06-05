package airbnb.New2026;

import java.util.*;

/*
Ski Path — Max Score on a Weighted DAG  (Airbnb).

Weighted DAG: edges carry a COST, nodes carry a REWARD. Find the maximum
  sum(node rewards on path) - sum(edge costs on path)
over any path from a fixed START to ANY end node (id starts with "END").

Input
  travel: edges as [from, cost, to]      e.g. ["start","3","A"]
  points: node rewards as [node, reward] e.g. ["A","5"]
  start : the fixed start node id

Example
  travel = [["start","3","A"], ["A","4","B"], ["B","5","END1"]]
  points = [["A","5"], ["B","6"], ["END1","3"]]
  start  = "start"
  -> 2     # start->A->B->END1 : (5+6+3) - (3+4+5) = 14 - 12 = 2

Algorithm — topological sort + forward DP
  best[v] = best max score of a path START..v that ENDS at v (reward[v] included).
    best[start] = reward[start]   (often 0 — start usually has no reward)
    best[v]     = max over incoming edge (u,v) of  best[u] - cost(u,v) + reward[v]
  Process nodes in topological order (Kahn's) so every predecessor is final
  before we use it. Answer = max(best[e]) over reachable end nodes.

  Reward is on NODES, cost is on EDGES — keep them indexed separately, and
  don't assume START has a reward.

  If the graph could have cycles, longest-path is NP-hard; clarify it's a DAG
  (or fall back to Bellman-Ford with a positive-cycle check).

Complexity
  Time:   O(V + E)
  Memory: O(V + E)
*/
public class SkiPathMaxScore {

    static class Edge {
        int cost;
        String id;
        public Edge(int cost, String id) {
            this.id = id;
            this.cost = cost;
        }
    }

    /**
     * Max (rewards - costs) from `start` to any END* node, or Long.MIN_VALUE if none reachable.
     * Assumes `start` is the unique source (only indegree-0 node) and every node is
     * reachable from it — so a single-source Kahn's sweep visits everything in order
     * and `best` is always set by the time a node is processed (no null guard needed).
     */
    public static long maxScore(String[][] travel, String[][] pointList, String start) {
        Map<String, Integer> points = new HashMap<>();
        for (String[] p : pointList) {
            points.put(p[0], Integer.parseInt(p[1]));
        }

        Map<String, List<Edge>> graph = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();
        indegree.put(start, 0);
        for (String[] edge : travel) {
            String from = edge[0], to = edge[2];
            int cost = Integer.parseInt(edge[1]);
            indegree.putIfAbsent(from, 0);
            indegree.merge(to, 1, Integer::sum);
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(cost, to));
        }

        Map<String, Long> best = new HashMap<>();
        best.put(start, (long) points.getOrDefault(start, 0));

        Queue<String> queue = new LinkedList<>();
        queue.add(start);                              // single source

        long result = Long.MIN_VALUE;
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            long reward = best.get(cur);               // never null: start is the only source
            if (cur.startsWith("END")) {
                result = Math.max(result, reward);
            }
            if (!graph.containsKey(cur)) continue;
            for (Edge next : graph.get(cur)) {
                long cand = reward - next.cost + points.getOrDefault(next.id, 0);
                if (!best.containsKey(next.id) || best.get(next.id) < cand) {
                    best.put(next.id, cand);
                }
                indegree.merge(next.id, -1, Integer::sum);
                if (indegree.get(next.id) == 0) {
                    queue.add(next.id);
                }
            }
        }
        return result;
    }

    /* ============================================================
       Alternative: top-down DFS + memo, starting from `start`.

       g(v) = best (sum rewards - sum costs) over any path v..END,
              counting reward[v] and reward[END].
         g(v) = reward[v] + max(
                    v is END ? 0 : NEG,            // option: stop here
                    max over edge (v->w, cost) of (-cost + g(w))
                )
       Answer = g(start); NEG marks "no END reachable downstream".
       Only visits nodes reachable from `start`; memo gives O(V + E).
       ============================================================ */
    private static final long NEG = Long.MIN_VALUE / 4;

    public static long maxScoreDfs(String[][] travel, String[][] pointList, String start) {
        Map<String, Integer> points = new HashMap<>();
        for (String[] p : pointList) {
            points.put(p[0], Integer.parseInt(p[1]));
        }
        Map<String, List<Edge>> graph = new HashMap<>();
        for (String[] e : travel) {
            graph.computeIfAbsent(e[0], k -> new ArrayList<>())
                 .add(new Edge(Integer.parseInt(e[1]), e[2]));
        }

        long g = dfs(start, graph, points, new HashMap<>());
        return g <= NEG / 2 ? Long.MIN_VALUE : g;
    }

    private static long dfs(String v, Map<String, List<Edge>> graph,
                            Map<String, Integer> points, Map<String, Long> memo) {
        Long cached = memo.get(v);
        if (cached != null) return cached;

        long best = v.startsWith("END") ? 0 : NEG;     // can stop only at an END node
        List<Edge> edges = graph.get(v);
        if (edges != null) {
            for (Edge e : edges) {
                long sub = dfs(e.id, graph, points, memo);
                if (sub > NEG / 2) {                    // downstream can reach an END
                    best = Math.max(best, sub - e.cost);
                }
            }
        }
        long g = (best <= NEG / 2) ? NEG : best + points.getOrDefault(v, 0);
        memo.put(v, g);
        return g;
    }

    /* --------------------------- tests --------------------------- */

    public static void main(String[] args) {
        // Spec example -> 2.
        check("spec",
                new String[][]{{"start", "3", "A"}, {"A", "4", "B"}, {"B", "5", "END1"}},
                new String[][]{{"A", "5"}, {"B", "6"}, {"END1", "3"}},
                "start", 2);

        // Two branches, the pricier-reward branch wins.
        //   start->A(1)->END1(1): (5+2)-(1+1)=5
        //   start->B(2)->END2(1): (10+1)-(2+1)=8
        check("two branches",
                new String[][]{{"start", "1", "A"}, {"start", "2", "B"},
                               {"A", "1", "END1"}, {"B", "1", "END2"}},
                new String[][]{{"A", "5"}, {"B", "10"}, {"END1", "2"}, {"END2", "1"}},
                "start", 8);

        // Diamond into a single END: cheaper route to END wins.
        //   via A: best[A]=0-1+10=9, END=9-1=8
        //   via B: best[B]=0-5+10=5, END=5-1=4   -> max 8
        check("diamond to one end",
                new String[][]{{"start", "1", "A"}, {"start", "5", "B"},
                               {"A", "1", "END"}, {"B", "1", "END"}},
                new String[][]{{"A", "10"}, {"B", "10"}, {"END", "0"}},
                "start", 8);

        // START itself carries a reward.
        check("start has reward",
                new String[][]{{"start", "3", "A"}, {"A", "4", "B"}, {"B", "5", "END1"}},
                new String[][]{{"start", "100"}, {"A", "5"}, {"B", "6"}, {"END1", "3"}},
                "start", 102);

        // No END node reachable -> Long.MIN_VALUE.
        check("no end reachable",
                new String[][]{{"start", "3", "A"}, {"A", "4", "B"}},
                new String[][]{{"A", "5"}, {"B", "6"}},
                "start", Long.MIN_VALUE);

        // START is itself an END (degenerate single-node path).
        check("start is end",
                new String[][]{},
                new String[][]{{"END", "7"}},
                "END", 7);

        // Longer path can beat a short one if rewards outweigh costs.
        //   start->END1 directly: reward 1 - cost 10 = -9
        //   start->A->END1:       (5+1) - (1+1) = 4
        check("longer path wins",
                new String[][]{{"start", "10", "END1"}, {"start", "1", "A"}, {"A", "1", "END1"}},
                new String[][]{{"A", "5"}, {"END1", "1"}},
                "start", 4);
    }

    private static void check(String label, String[][] travel, String[][] points,
                              String start, long expected) {
        long bfs = maxScore(travel, points, start);
        long dfs = maxScoreDfs(travel, points, start);
        boolean ok = bfs == expected && dfs == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + label + " expected=" + expected + " bfs=" + bfs + " dfs=" + dfs);
    }
}
