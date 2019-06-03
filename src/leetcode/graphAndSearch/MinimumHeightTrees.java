package leetcode.graphAndSearch;

import java.util.*;

public class MinimumHeightTrees {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        Queue<Integer> leaf = new LinkedList<Integer>();
        if(n<=1) {
            leaf.add(0);
            return (List<Integer>) leaf;
        }
        Map<Integer, ArrayList<Integer>> graph = new HashMap<Integer, ArrayList<Integer>>();
        for(int i=0;i<n;i++) {
            graph.put(i, new ArrayList<Integer>());
        }
        int[] neighbors = new int[n];
        for(int[] edge : edges) {
            neighbors[edge[0]]++;
            neighbors[edge[1]]++;
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        for(int i=0;i<n;i++) {
            if(neighbors[i] ==1 ) leaf.offer(i);
        }
        while(n>2) {
            int size = leaf.size();
            for(int i = 0; i < size; i++) {
                n--;
                int cur = leaf.poll();
                for(int nb : graph.get(cur)) {
                    if(--neighbors[nb] == 1) leaf.add(nb);
                }
            }

        }
        return (List<Integer>) leaf;
    }

    public static void main(String[] args) {
        int n = 6;
        int[][] edges = {{0, 3}, {1, 3}, {2, 3}, {4, 3}, {5, 4}};
        MinimumHeightTrees solver = new MinimumHeightTrees();
        List<Integer> result = solver.findMinHeightTrees(n, edges);
        System.out.println(result.toString());
    }
}
