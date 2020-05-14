package facebook;

import leetcode.union.Union;

import java.util.*;

public class _AccountsMerge {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        List<List<String>> result = new ArrayList<>();

        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        Union union = new Union(1000);
        int id = 0;
        for (List<String> list : accounts) {
            String name = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                String place = list.get(i);
                emailToName.put(place, name);
                emailToId.putIfAbsent(place, id++);
                if (i != 1) {
                    union.union(emailToId.get(place), emailToId.get(list.get(1)));
                }
            }
        }

        Map<Integer, List<String>> resultMap = new HashMap<>();

        for (String email : emailToName.keySet()) {
            int father = union.find(emailToId.get(email));
            resultMap.putIfAbsent(father, new ArrayList<>());
            resultMap.get(father).add(email);
        }

        for (List<String> values : resultMap.values()) {
            Collections.sort(values);
            String name = emailToName.get(values.get(0));
            values.add(0, name);
            result.add(values);
        }
        return result;
    }

    public static void main(String[] args) {
    }
}
