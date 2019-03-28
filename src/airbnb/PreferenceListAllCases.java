package airbnb;

import java.util.*;

public class PreferenceListAllCases {
    public List<List<Integer>> getPreference(List<List<Integer>> preferences, int n) {
        if (preferences == null || preferences.size() == 0) {
            return null;
        }
        int[] indegree = new int[n];
        boolean[] visited = new boolean[n];
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> list : preferences) {
            for (int i = 1; i < list.size(); i++) {
                indegree[list.get(i)]++;
                if (!map.containsKey(list.get(i - 1))) {
                    map.put(list.get(i - 1), new ArrayList<Integer>());
                }
                map.get(list.get(i - 1)).add(list.get(i));
            }
        }
        dfsFindAll(result, new ArrayList<Integer>(), visited, indegree, map, n);
        return result;
    }

    private void dfsFindAll(List<List<Integer>> result, ArrayList<Integer> list, boolean[] visited, int[] indegree,
                            Map<Integer, List<Integer>> map, int n) {
        boolean flag = false;
        for (int cur = 0; cur < n; cur++) {
            if (!visited[cur] && indegree[cur] == 0) {
                list.add(cur);
                visited[cur] = true;

                if (map.containsKey(cur)) {
                    for (int next : map.get(cur)) {
                        indegree[next]--;
                    }
                }

                dfsFindAll(result, list, visited, indegree, map, n);
                list.remove(list.size() - 1);
                visited[cur] = false;
                if (map.containsKey(cur)) {
                    for (int next : map.get(cur)) {
                        indegree[next]++;
                    }
                }
                flag = true;
            }
        }
        if (!flag) {
            result.add(new ArrayList<>(list));
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> list = new ArrayList<>();
        list.add(Arrays.asList(3, 5, 7, 9, 6));
        list.add(Arrays.asList(1, 2, 3, 8, 4));
        list.add(Arrays.asList(5, 8, 0));
        PreferenceListAllCases solver = new PreferenceListAllCases();
        List<List<Integer>> result = solver.getPreference(list, 10);
        for (List<Integer> l : result) {
            System.out.println(l.toString());
        }
    }
}
