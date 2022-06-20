package datastructure.graph;

import java.util.*;

public class EvaluateDivision {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, List<String>> map = new HashMap<>();
        Map<String, Double> result = new HashMap<>();

        for (int i = 0 ; i < equations.size(); i++) {
            List<String> eq = equations.get(i);
            double val = values[i];
            String from = eq.get(0), to = eq.get(1);
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
            map.putIfAbsent(to, new ArrayList<>());
            map.get(to).add(from);

            result.put(from + " " + to, val);
            result.put(to + " " + from, 1 / val);
        }
        int n = queries.size();
        double[] res = new double[n];

        for (int i = 0; i < n; i ++) {
            String start = queries.get(i).get(0), end = queries.get(i).get(1);
            res[i] = dfsFind(map, result, end, start, 1.0, new HashSet<String>());
        }

        return res;
    }

    private double dfsFind(Map<String, List<String>> map, Map<String, Double> res, String end, String cur, double result, HashSet<String> visited) {
        if (!map.containsKey(cur)) return -1;
        if (cur.equals(end)) return result;
        if (res.containsKey(cur + " " + end)) {
            return result * res.get(cur + " " + end);
        }
        visited.add(cur);
        for (String child : map.get(cur)) {
            if (visited.contains(child)) continue;

            double val = dfsFind(map, res, end, child, result * res.get(cur + " " + child), visited);
            if (val != -1) return val;
        }
        visited.remove(cur);
        return -1;
    }
}
