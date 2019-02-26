package airbnb;

import java.util.*;

public class PreferenceList {
    public List<Integer> getPreference(List<List<Integer>> preferences, int n) {
        if (preferences == null || preferences.size() == 0) {
            return null;
        }
        int[] indegree = new int[n];
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        for (List<Integer> list : preferences) {
            for (int i = 1; i < list.size(); i++) {
                indegree[list.get(i)]++;
                if (!map.containsKey(list.get(i - 1))) {
                    map.put(list.get(i - 1), new ArrayList<Integer>());
                }
                map.get(list.get(i - 1)).add(list.get(i));
            }
        }
        Queue<Integer> pq = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                result.add(i);
                pq.add(i);
            }
        }

        while (!pq.isEmpty()) {
            int size = pq.size();
            for (int s = 0; s < size; s++) {
                int cur = pq.poll();
                if (map.containsKey(cur)) {
                    for (Integer next : map.get(cur)) {
                        indegree[next]--;
                        if (indegree[next] == 0) {
                            result.add(next);
                            pq.add(next);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<List<Integer>> list = new ArrayList<>();
        list.add(Arrays.asList(3, 5, 7, 9, 6));
        list.add(Arrays.asList(1, 2, 3, 8, 4));
        list.add(Arrays.asList(5, 8, 0));
        PreferenceList solver = new PreferenceList();
        List<Integer> result = solver.getPreference(list, 10);
        System.out.println(result.toString());
    }
}
