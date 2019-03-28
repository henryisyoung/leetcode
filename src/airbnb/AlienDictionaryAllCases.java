package airbnb;

import java.util.*;

public class AlienDictionaryAllCases {
    public List<String> alienOrder(String[] words) {
        if (words == null || words.length == 0) {
            return null;
        }

        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        Map<Character, Boolean> visited = new HashMap<>();
        buildGraph(graph, inDegree, words, visited);

        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        dfsSearchAll(sb, result, inDegree, graph, visited);
        return result;
    }

    private void dfsSearchAll(StringBuilder sb, List<String> result, Map<Character, Integer> inDegree,
                              Map<Character, Set<Character>> graph, Map<Character, Boolean> visited) {
        boolean flag = false;
        for (char c : graph.keySet()) {
            if (!visited.get(c) && inDegree.get(c) == 0) {
                visited.put(c, true);
                sb.append(c);
                for (char next : graph.get(c)) {
                    inDegree.put(next, inDegree.get(next) - 1);
                }
                dfsSearchAll(sb, result, inDegree, graph, visited);
                visited.put(c, false);
                sb.deleteCharAt(sb.length() - 1);
                for (char next : graph.get(c)) {
                    inDegree.put(next, inDegree.get(next) + 1);
                }
                flag = true;
            }
        }
        if (!flag) {
            result.add(sb.toString());
        }
    }

    private void buildGraph(Map<Character, Set<Character>> graph, Map<Character, Integer> inDegree, String[] words,
                            Map<Character, Boolean> visited) {
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (!graph.containsKey(c)) {
                    graph.put(c, new HashSet<>());
                }
                if (!inDegree.containsKey(c)) {
                    inDegree.put(c, 0);
                }
                visited.put(c, false);
            }
        }
        Set<String> set = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i], word2 = words[i + 1];
            for (int j = 0; j < word1.length() && j < word2.length(); j++) {
                char from = word1.charAt(j), to = word2.charAt(j);
                if (from == to) {
                    continue;
                }
                if (set.contains(from + " " + to)) {
                    break;
                } else {
                    Set<Character> list = graph.get(from);
                    list.add(to);
                    graph.put(from, list);
                    inDegree.put(to, 1 + inDegree.get(to));
                    set.add(from + " " + to);
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] words = new String[]{"wrt", "wrf", "er", "ett", "rftt"};
        String[] words2 = new String[]{"wrt", "wrf", "er", "ett", "rftt", "te"};
        AlienDictionaryAllCases solver = new AlienDictionaryAllCases();
//        System.out.println(solver.alienOrder(words));
        System.out.println(solver.alienOrder(words2));
    }
}
