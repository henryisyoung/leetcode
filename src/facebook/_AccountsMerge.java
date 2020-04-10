package facebook;

import leetcode.union.Union;

import java.util.*;

public class _AccountsMerge {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        List<List<String>> result = new ArrayList<>();
        Union union = new Union(1000 * 10);

        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        int id = 0;
        for (List<String> acc : accounts) {
            String name = acc.get(0);
            for (int i = 1; i < acc.size(); i++) {
                emailToId.putIfAbsent(acc.get(i), id++);
                emailToName.putIfAbsent(acc.get(i), name);
                union.union(emailToId.get(acc.get(i)), emailToId.get(acc.get(1)));
            }
        }

        Map<Integer, List<String>> fatherMap = new HashMap<>();

        for (String acc : emailToId.keySet()) {
            int father = union.find(emailToId.get(acc));
            fatherMap.putIfAbsent(father, new ArrayList<>());
            fatherMap.get(father).add(acc);
        }

        for (int father : fatherMap.keySet()) {
            String name = emailToName.get(fatherMap.get(father).get(0));
            List<String> list = new ArrayList<>();
            list.add(name);
            Collections.sort(fatherMap.get(father));
            list.addAll(fatherMap.get(father));
            result.add(list);
        }

        return result;
    }

    public static void main(String[] args) {
    }
}
