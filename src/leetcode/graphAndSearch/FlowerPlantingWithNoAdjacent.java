package leetcode.graphAndSearch;

import java.util.*;

public class FlowerPlantingWithNoAdjacent {
    public int[] gardenNoAdj(int N, int[][] paths) {
        int[] result = new int[N];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] path : paths) {
            int a = path[0], b = path[1];
            if (!map.containsKey(a)) map.put(a, new ArrayList<>());
            if (!map.containsKey(b)) map.put(b, new ArrayList<>());
            map.get(a).add(b);
            map.get(b).add(a);
        }

        for (int i = 1; i <= N; i++) {
            List<Integer> colors = new ArrayList<>(Arrays.asList(1,2,3,4));
            if (map.containsKey(i)) {
                for (int next : map.get(i)) {
                    if (result[next - 1] != 0) {
                        colors.remove((Integer) result[next - 1]);
                    }
                }

            }
            result[i - 1] = colors.get(0);
        }
        return result;
    }

    public static void main(String[] args) {
        int N = 4;
        FlowerPlantingWithNoAdjacent solver = new FlowerPlantingWithNoAdjacent();
        int[][] paths = {{1,2},{2,3},{3,4},{4,1},{1,3},{2,4}};
        int[] result = solver.gardenNoAdj(N, paths);
        System.out.println(Arrays.toString(result));
    }
}
