package uber;

import java.util.*;

public class EvaluateDivision {
    public  static double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        HashMap<String, Double> res = new HashMap<>();;
        HashMap<String, Set<String>> graph = new HashMap<>();
        for(int i = 0; i<values.length;i++){
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            graph.putIfAbsent(a, new HashSet<>());
            graph.putIfAbsent(b, new HashSet<>());
            graph.get(a).add(b);
            graph.get(b).add(a);
            res.put(a+b, values[i]);
            res.put(b+a, 1.0/values[i]);
        }

        int n = queries.size();
        double[] result = new double[n];
        for(int i = 0;i<n;i++){
            String start = queries.get(i).get(0), end = queries.get(i).get(1);
            result[i] = dfsSearchAll(start, end, start, graph, res, 1.0, new HashSet<>());
        }
        return result;
    }

    private static double dfsSearchAll(String start, String end, String cur, HashMap<String, Set<String>> graph,
                                HashMap<String, Double> edges, double result, HashSet<String> visited) {
        if (!graph.containsKey(cur)) return -1;
        if (cur.equals(end)) return  result;
        if (edges.containsKey(cur + end)) return edges.get(cur + end) * result;
        edges.put(start + cur, result);
        visited.add(cur);
        for (String next : graph.get(cur)) {
            if (visited.contains(next)) continue;
            double r = dfsSearchAll(start, end, next, graph, edges, result * edges.get(cur + next), visited);
            if (r != -1.0) return r;
        }
        visited.remove(cur);
        return -1;
    }


    public static void main(String[] args) {
        List<List<String>> equations = new ArrayList<>(), queries = new ArrayList<>();
        double[] values = {2.0, 3.0};
        equations.add(Arrays.asList("a", "b"));
        equations.add(Arrays.asList("b", "c"));
        //[ ["a", "c"], ["b", "a"], ["a", "e"], ["a", "a"], ["x", "x"] ].
        queries.add(Arrays.asList("a", "c"));
        queries.add(Arrays.asList("b", "a"));
        queries.add(Arrays.asList("a", "e"));
        queries.add(Arrays.asList("a", "a"));
        queries.add(Arrays.asList("x", "x"));
        double[] result = calcEquation(equations, values, queries);
        System.out.println(Arrays.toString(result));
    }
}
