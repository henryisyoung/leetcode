package airbnb.pre2026;

import java.util.*;

public class AlienDictionary2 {
    public String alienOrder(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        if (!buildGraph(words, graph, inDegree)) return "";
        Queue<Character> queue = new LinkedList<>();
        for (Character c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                sb.append(c);
                queue.add(c);
            }
        }

        while (!queue.isEmpty()) {
            char c = queue.poll();
            if (graph.containsKey(c)) {
                for (char next : graph.get(c)) {
                    int val = inDegree.get(next) - 1;
                    inDegree.put(next, val);
                    if (val == 0) {
                        sb.append(next);
                        queue.add(next);
                    }
                }
            }
        }
        return inDegree.size() == sb.length() ? sb.toString() : "";
    }

    private boolean buildGraph(String[] words, Map<Character, Set<Character>> graph, Map<Character, Integer> inDegree) {
        for (String word : words) {
            for (char c : word.toCharArray()) {
                if (!graph.containsKey(c)) {
                    graph.put(c, new HashSet<Character>());
                }
                if (!inDegree.containsKey(c)) {
                    inDegree.put(c, 0);
                }
            }
        }

        int n = words.length;
        for (int i = 0; i < n - 1; i++) {
            String word1 = words[i], word2 = words[i + 1];
            int j = 0;
            while (j < word1.length() && j < word2.length() && word1.charAt(j) == word2.charAt(j)) {
                j++;
            }
            if (j == word1.length()) continue;
            if (j == word2.length()) return false;

            char from = word1.charAt(j), to = word2.charAt(j);
            if (graph.get(from).add(to)) {
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String[] words = new String[]{"wrt", "wrf", "er", "ett", "rftt"};
        String[] words2 = new String[]{"wrt", "wrf", "er", "ett", "rftt", "te"};
        AlienDictionary2 solver = new AlienDictionary2();
        System.out.println(solver.alienOrder(words));
        System.out.println(solver.alienOrder(words2));
        System.out.println(solver.alienOrder(new String[]{"abc", "ab"})); // expected ""
        System.out.println(solver.alienOrder(new String[]{"z", "x", "z"})); // expected ""
    }
}
