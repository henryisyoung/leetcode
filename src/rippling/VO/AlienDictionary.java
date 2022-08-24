package rippling.VO;

import java.util.*;

public class AlienDictionary {
    // bfs
    public String alienOrder(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }
        Map<Character, Integer> indegree = new HashMap<>();
        Map<Character, List<Character>> graph = new HashMap<>();
        boolean validWords = buildGraph(words, indegree, graph);
        if (!validWords) {
            return "";
        }
        Queue<Character> queue = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        for (char key : indegree.keySet()) {
            if (indegree.get(key) == 0) {
                queue.add(key);
                sb.append(key);
            }
        }
        while (!queue.isEmpty()) {
            char cur = queue.poll();
            if (graph.containsKey(cur)) {
                for (char next : graph.get(cur)) {
                    int val = indegree.get(next) - 1;
                    if (val == 0) {
                        queue.add(next);
                        sb.append(next);
                    }
                    indegree.put(next, val);
                }
            }
        }
        return sb.length() == indegree.size() ? sb.toString() : "";
    }

    private boolean buildGraph(String[] words, Map<Character, Integer> indegree, Map<Character, List<Character>> graph) {
        for (String word : words) {
            for (char c : word.toCharArray()) {
                indegree.put(c, 0);
            }
        }
        Set<String> visited = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String a = words[i], b = words[i + 1];
            if (a.length() > b.length() && a.startsWith(b)) {
                return false;
            }
            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                char aChar = a.charAt(j), bChar = b.charAt(j);
                if (aChar != bChar) {
                    if (visited.contains(aChar + " " + bChar)) {
                        break;
                    }
                    visited.add(aChar + " " + bChar);
                    indegree.put(bChar, indegree.getOrDefault(bChar, 0) + 1);
                    graph.putIfAbsent(aChar, new ArrayList<>());
                    graph.get(aChar).add(bChar);
                    break;
                }
            }
        }
        return true;
    }

    // dfs
    public String alienOrderDFS(String[] words) {
        int[] visited = new int[26];
        Arrays.fill(visited, -1);
        for (String word : words) {
            for (char c : word.toCharArray()) {
                visited[c - 'a'] = 0;
            }
        }
        Map<Character, List<Character>> graph = new HashMap<>();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String a = words[i], b = words[i + 1];
            if (a.length() > b.length() && a.startsWith(b)) {
                return "";
            }
            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                char aChar = a.charAt(j), bChar = b.charAt(j);
                if (aChar != bChar) {
                    if (set.contains(aChar + " " + bChar)) {
                        break;
                    }
                    set.add(aChar + " " + bChar);
                    graph.putIfAbsent(aChar, new ArrayList<>());
                    graph.get(aChar).add(bChar);
                    break;
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 26; i++) {
            if (visited[i] == 0) {
                if (!dfsFindPath(sb, visited, graph, i)) {
                    return "";
                }
            }
        }
        return sb.reverse().toString();
    }

    private boolean dfsFindPath(StringBuilder sb, int[] visited, Map<Character, List<Character>> graph, int cur) {
        visited[cur] = 1;
        char curChar = (char) (cur + 'a');
        if (graph.containsKey(curChar)) {
            for (char next : graph.get(curChar)) {
                if (visited[next - 'a'] == 1) {
                    return false;
                }
                if (visited[next - 'a'] == 0) {
                    if (!dfsFindPath(sb, visited, graph, next - 'a')) {
                        return false;
                    }
                }
            }
        }
        visited[cur] = 2;
        sb.append(curChar);
        return true;
    }
}