package facebook;

import java.util.*;

public class AlienDictionary {
    public static String alienOrder(String[] words) {
        StringBuilder sb = new StringBuilder();
        if (words == null || words.length == 0) return "";
        Map<Character, List<Character>> map = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();
        buildGraph(map, indegree, words);

        Queue<Character> queue = new LinkedList<>();

        for (Character c : indegree.keySet()) {
            if (indegree.get(c) == 0) {
                sb.append(c);
                queue.add(c);
            }
        }

        while (!queue.isEmpty()) {
            char c = queue.poll();

            for (char next : map.get(c)) {
                int count = indegree.get(next) - 1;
                indegree.put(next, count);
                if (count == 0) {
                    queue.add(next);
                    sb.append(next);
                }
            }
        }
        return sb.length() == indegree.size() ? sb.toString() : "";
    }

    private static void buildGraph(Map<Character, List<Character>> map, Map<Character, Integer> indegree, String[] words) {
        for (String word : words) {
            for (char c : word.toCharArray()) {
                indegree.put(c, 0);
                map.putIfAbsent(c, new ArrayList<>());
            }
        }

        Set<String> visited = new HashSet<>();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i], word2 = words[i + 1];
            for (int j = 0; j < Math.min(word1.length(), word2.length()); j++) {
                char c1 = word1.charAt(j), c2 = word2.charAt(j);
                if (c1 == c2) continue;
                String str = c1 +  "#" + c2;
                if (visited.contains(str)) break;
                visited.add(str);
                indegree.put(c2, indegree.get(c2) + 1);
                map.get(c1).add(c2);
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
