package airbnb;

import java.util.*;

public class PreferenceList {
    public List<Integer> getPreference(List<List<Integer>> preferences) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, Set<Integer>> nodes = new HashMap<>();
        for (List<Integer> l : preferences) {
            for (int i = 0; i < l.size() - 1; i++) {
                int from = l.get(i);
                int to = l.get(i + 1);
                if (!nodes.containsKey(from)) {
                    inDegree.put(from, 0);
                    Set<Integer> set = new HashSet<>();
                    nodes.put(from, set);
                }
                if (!nodes.containsKey(to)) {
                    inDegree.put(to, 0);
                    Set<Integer> set = new HashSet<>();
                    nodes.put(to, set);
                }
                if (!nodes.get(from).contains(to)) {
                    Set<Integer> s = nodes.get(from);
                    s.add(to);
                    nodes.put(from, s);
                }
                if (inDegree.containsKey(to)) {
                    inDegree.put(to,inDegree.get(to) + 1);
                } else {
                    inDegree.put(to, 0);
                }
            }
        }
        Queue<Integer> q = new LinkedList<>();
        for (int k : inDegree.keySet()) {
            if (inDegree.get(k) == 0) {
                q.offer(k);
            }
        }
        List<Integer> res = new ArrayList<>();
        while (!q.isEmpty()) {
            int id = q.poll();
            res.add(id);
            Set<Integer> neighbors = nodes.get(id);
            for (int next : neighbors) {
                int degree = inDegree.get(next) - 1;
                inDegree.put(next, degree);
                if (degree == 0) {
                    q.offer(next);
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        List<List<Integer>> list = new ArrayList<>();
        list.add(Arrays.asList(3, 5, 7, 9));
        list.add(Arrays.asList(2, 3, 8));
        list.add(Arrays.asList(5, 8));
        PreferenceList solver = new PreferenceList();
        List<Integer> result = solver.getPreference(list);
        System.out.println(result.toString());
    }
}
