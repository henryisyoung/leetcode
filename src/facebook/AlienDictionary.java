package facebook;

import java.util.*;

public class AlienDictionary {
    public static String alienOrder(String[] words) {
        StringBuilder sb = new StringBuilder();
        Map<Character, Integer> indegree = new HashMap<>();
        Map<Character, List<Character>> graph = new HashMap<>();

        buildGraph(indegree, graph, words);

        Queue<Character> queue = new LinkedList<>();


        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) {
                queue.add(c);
                sb.append(c);
            }
        }

        while (!queue.isEmpty()) {
            char cur = queue.poll();
            if (!graph.containsKey(cur)) continue;
            for (char next : graph.get(cur)) {
                int val = indegree.get(next) - 1;
                indegree.put(next, val);
                if (val == 0) {
                    queue.add(next);
                    sb.append(next);
                }
            }
        }

        return sb.length() == indegree.size() ? sb.toString() : "";
    }

    private static void buildGraph(Map<Character, Integer> indegree, Map<Character, List<Character>> graph, String[] words) {
        for (String word : words) {
            for (char c : word.toCharArray()) {
                indegree.put(c, 0);
            }
        }
        Set<String> visited = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i], w2 = words[i + 1];
            for (int j = 0; j < w1.length() && j < w2.length(); j++) {
                char c1 = w1.charAt(j), c2 = w2.charAt(j);
                if (c1 == c2) continue;
                if (visited.contains(c1 + " " + c2)) break;
                indegree.put(c2, indegree.get(c2) + 1);
                graph.putIfAbsent(c1, new ArrayList<>());
                graph.get(c1).add(c2);
                visited.add(c1 + " " + c2);
                break;
            }
        }
    }

    public static void main(String[] args) {
        String[] words = {"wrt",
                "wrf",
                "er",
                "ett",
                "rftt"};
        String[] words2 = {"z","x","z"};
                System.out.println(alienOrder(words));
                System.out.println(alienOrder(words2));
    }
}
