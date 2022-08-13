package roblox.onsite;

import java.util.*;

public class AlienDictionary {
    // bfs
    public String alienOrder(String[] words) {
        Map<Character, Integer> indegree = new HashMap<>();
        Map<Character, Set<Character>> map = new HashMap<>();

        boolean validGraph = buildGraph(map, indegree, words);
        if (!validGraph) {
            return "";
        }
        int size = indegree.size();
        StringBuilder sb = new StringBuilder();
        Queue<Character> queue = new LinkedList<>();
        for (Character key : indegree.keySet()) {
            if (indegree.get(key) == 0) {
                queue.add(key);
                sb.append(key);
            }
        }
        while (!queue.isEmpty()) {
            char cur = queue.poll();
            if (map.containsKey(cur)) {
                for (char next : map.get(cur)) {
                    int val = indegree.get(next) - 1;
                    indegree.put(next, val);
                    if (val == 0) {
                        queue.add(next);
                        sb.append(next);
                    }
                }
            }
        }

        return sb.length() == size ? sb.toString() : "";
    }

    private boolean buildGraph(Map<Character, Set<Character>> map, Map<Character, Integer> indegree, String[] words) {
        for (String word : words) {
            for (char c : word.toCharArray()) {
                indegree.put(c, 0);
            }
        }
        int n = words.length;
        Set<String> pair = new HashSet<>();

        for (int i = 0; i < n - 1; i++) {
            String a = words[i], b = words[i + 1];
            if (a.length() > b.length() && a.startsWith(b)) {
                return false;
            }
            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                if (a.charAt(j) == b.charAt(j)) {
                    continue;
                }
                char from = a.charAt(j), to = b.charAt(j);
                if (pair.contains(from + " " + to)) {
                    break;
                }
                pair.add(from + " " + to);
                indegree.put(to, indegree.get(to) + 1);
                map.putIfAbsent(from, new HashSet<>());
                map.get(from).add(to);
                break;
            }
        }
        return true;
    }

    //dfs
    public String alienOrderDfs(String[] words) {
        int n = words.length;
        int[] visited = new int[26];
        Map<Character, Set<Character>> map = new HashMap<>();
        Arrays.fill(visited, -1);
        for (String word : words) {
            for (char c : word.toCharArray()) {
                visited[c - 'a'] = 0;
            }
        }
        for (int i = 0; i < n - 1; i++) {
            String a = words[i], b = words[i + 1];
            if (a.length() > b.length() && a.startsWith(b)) {
                return "";
            }
            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                char aChar = a.charAt(j), bChar = b.charAt(j);
                if (aChar != bChar) {
                    map.putIfAbsent(aChar, new HashSet<>());
                    map.get(aChar).add(bChar);
                    break;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (visited[i] == 0) {
                if (!dfsFindAll(visited, i, map, sb)) {
                    return "";
                }
            }
        }
        return sb.reverse().toString();
    }

    private boolean dfsFindAll(int[] visited, int cur, Map<Character, Set<Character>> map, StringBuilder sb) {
        visited[cur] = 1;
        char curChar = (char) (cur + 'a');

        if (map.containsKey(curChar)) {
            for (char next : map.get(curChar)) {
                if (visited[next - 'a'] == 1) {
                    return false;
                }
                if (visited[next - 'a'] == 0) {
                    if (!dfsFindAll(visited, next - 'a', map, sb)) {
                        return false;
                    }
                }
            }
        }
        visited[cur] = 2;
        sb.append((char) (cur + 'a'));
        return true;
    }
}