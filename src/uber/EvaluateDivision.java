package uber;

import java.util.*;

public class EvaluateDivision {
    public static double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
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
            result[i] = dfs(res, graph, queries.get(i).get(0),queries.get(i).get(0), queries.get(i).get(1), new HashSet<>(), 1.0);
        }
        return result;
    }

    public static double dfs(HashMap<String, Double> res, HashMap<String, Set<String>> graph, String start, String curr, String end, Set<String> visited, double result){
        if(!graph.containsKey(curr)){
            return -1.0;
        }
        if(curr.equals(end)){
            return result;
        }
        if(res.containsKey(curr + end)) return result * res.get(curr+end);
        res.put(start+curr, result);  //for memorization
        visited.add(curr);
        for(String next: graph.get(curr)){
            if(!visited.contains(next)){
                double r = dfs(res, graph, start,next, end, visited, result*res.get(curr+next));
                if(r!=-1.0){
                    return r;
                    //pruning, unlike other graph problems, in this question there is only one value for two vertices
                    //so as long as we found one valid value, then this value must be the result
                }
            }
        }
        visited.remove(curr);
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
