package airbnb;

import java.util.*;

public class PreferenceListAllCases2 {
    public List<List<Integer>> getPreference(List<List<Integer>> preferences, int n) {
        List<List<Integer>> result = new ArrayList<>();
        if (preferences == null || preferences.size() == 0) {
            return result;
        }
        int[] indegree = new int[n];
        boolean[] visited = new boolean[n];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (List<Integer> list : preferences) {
            for (int i = 1; i < list.size(); i++) {
                indegree[list.get(i)]++;
                if (!map.containsKey(list.get(i - 1))) {
                    map.put(list.get(i - 1), new ArrayList<Integer>());
                }
                map.get(list.get(i - 1)).add(list.get(i));
            }
        }
        dfsSearchAll(result, visited, new ArrayList<Integer>(), n, indegree, map);
        System.out.println(result.size());
        return result;
    }

    private void dfsSearchAll(List<List<Integer>> result, boolean[] visited, ArrayList<Integer> list, int n,
                              int[] indegree, Map<Integer, List<Integer>> map) {
        if (list.size() == n) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i] && indegree[i] == 0) {
                visited[i] = true;
                list.add(i);
                if (map.containsKey(i)) {
                    for (int next : map.get(i)) {
                        indegree[next]--;
                    }
                }
                dfsSearchAll(result, visited, list, n, indegree, map);
                visited[i] = false;
                list.remove(list.size() - 1);
                if (map.containsKey(i)) {
                    for (int next : map.get(i)) {
                        indegree[next]++;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> list = new ArrayList<>();
        list.add(Arrays.asList(3, 5, 7, 9, 6));
        list.add(Arrays.asList(1, 2, 3, 8, 4));
        list.add(Arrays.asList(5, 8, 0));
        PreferenceListAllCases2 solver = new PreferenceListAllCases2();
        List<List<Integer>> result = solver.getPreference(list, 10);
        for (List<Integer> l : result) {
            System.out.println(l.toString());
        }
    }
}
