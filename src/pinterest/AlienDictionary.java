package pinterest;
import java.util.*;
public class AlienDictionary {
    public String alienOrder(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }
        Map<Character, Integer> inDegree = new HashMap<>();
        Map<Character, Set<Character>> map = new HashMap<>();
        buildGraph(words, inDegree, map);
        StringBuilder sb = new StringBuilder();
        Queue<Character> queue = new LinkedList<>();

        for (Character c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                System.out.println("key " + c);
                sb.append(c);
                queue.add(c);
            }
        }
        while (!queue.isEmpty()) {
            char cur = queue.poll();
            if (map.containsKey(cur)) {
                for (char next : map.get(cur)) {
                    int val = inDegree.get(next) - 1;
                    if (val == 0) {
                        queue.add(next);
                        sb.append(next);
                    }
                    inDegree.put(next, val);
                }
            }
        }
        return sb.length() == inDegree.size() ? sb.toString() : "";
    }

    private void buildGraph(String[] words, Map<Character, Integer> inDegree, Map<Character, Set<Character>> map) {
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (!map.containsKey(c)) {
                    map.put(c, new HashSet<>());
                }
                if (!inDegree.containsKey(c)) {
                    inDegree.put(c, 0);
                }
            }
        }
        Set<String> set = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String s1 = words[i], s2 = words[i + 1];
            for (int j = 0; j < s1.length() && j < s2.length(); j++){
                char from = s1.charAt(j), to = s2.charAt(j);
                if (from == to) {
                    continue;
                }
                if (set.contains(from + "" + to)) {
                    break;
                }
                set.add(from + "" + to);
                inDegree.put(to, inDegree.get(to) + 1);

                map.get(from).add(to);
                break;
            }
        }
    }

    public static void main(String[] args) {
        String[] words = {"wrt", "wrf", "er", "ett", "rftt"};
        String[] words2 = {"ri","xz","qxf","jhsguaw","dztqrbwbm","dhdqfb","jdv","fcgfsilnb","ooby"};
        AlienDictionary solver = new AlienDictionary();
//        System.out.println(solver.alienOrder(words));
        System.out.println(solver.alienOrder(words2));
    }
}
