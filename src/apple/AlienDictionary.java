package apple;

import java.util.*;

public class AlienDictionary {
    public static String alienOrder(String[] words) {
        if (words == null || words.length == 0) return "";
        Map<Character, Integer> indegree = new HashMap<>();
        Map<Character, List<Character>> graph = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        buildGraph(indegree, graph, words);

        Queue<Character> queue = new LinkedList<>();

        for (Character c : indegree.keySet()) {
            if (indegree.get(c) == 0) {
                queue.add(c);
                sb.append(c);
            }
        }

        while (!queue.isEmpty()) {
            char c = queue.poll();
            if (graph.containsKey(c)) {
                for (char child : graph.get(c)) {
                    indegree.put(child, indegree.get(child) - 1);
                    if (indegree.get(child) == 0) {
                        queue.add(child);
                        sb.append(child);
                    }

                }
            }
        }

        return sb.length() == indegree.size() ? sb.toString() : "";
    }

    private static void buildGraph(Map<Character, Integer> indegree, Map<Character, List<Character>> graph, String[] words) {
        for (String w : words) {
            for (char c : w.toCharArray()) {
                indegree.putIfAbsent(c, 0);
            }
        }
        Set<String> set = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String a = words[i], b = words[i + 1];
            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                Character from = a.charAt(j), to = b.charAt(j);
                if (from == to) continue;
                if (set.contains(from + " " + to)) break;
                set.add(from + " " + to);
                indegree.put(to, indegree.get(to) + 1);

                graph.putIfAbsent(from, new ArrayList<>());
                graph.get(from).add(to);
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

        System.out.println(alienOrder(words));
    }
}
