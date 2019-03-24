package airbnb;

import java.util.*;

public class AlienDictionary {
    public String alienOrder(String[] words) {
        StringBuilder order = new StringBuilder();
        Map<Character, Integer> inDegree = new HashMap<>();
        Map<Character, Set<Character>> graph = new HashMap<>();
        buildGraph(inDegree, graph, words);

        Queue<Character> queue = new LinkedList<>();
        for (Character c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                queue.add(c);
                order.append(c);
            }
        }

        while (!queue.isEmpty()) {
            char c = queue.poll();
            if (graph.containsKey(c)) {
                for (char next : graph.get(c)) {
                    int val = inDegree.get(next);
                    if (val == 1) {
                        queue.add(next);
                        order.append(next);
                    }
                    inDegree.put(next, val - 1);
                }
            }
        }
        return inDegree.size() == order.length() ? order.toString() : "";
    }

    private void buildGraph(Map<Character, Integer> inDegree, Map<Character, Set<Character>> graph, String[] words) {
        for(String word : words){
            for(int i = 0; i < word.length(); i++){
                char curr = word.charAt(i);
                // 对每个单词的每个字母初始化计数器和图节点
                if(graph.get(curr) == null){
                    graph.put(curr, new HashSet<Character>());
                }
                if(inDegree.get(curr) == null){
                    inDegree.put(curr, 0);
                }
            }
        }

        Set<String> set = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String s1 = words[i], s2 = words[i + 1];
            for (int j = 0; j < s1.length() && j < s2.length(); j++) {
                char from = s1.charAt(j), to = s2.charAt(j);
                if (from == to) {
                    continue;
                }
                if (!set.contains(from+ " " + to)) {
                    Set<Character> neighbors = graph.get(from);
                    neighbors.add(to);
                    graph.put(from, neighbors);
                    int val = inDegree.get(to);
                    inDegree.put(to, val + 1);
                    set.add(from+ " " + to);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] words = new String[]{"wrt", "wrf", "er", "ett", "rftt"};
        AlienDictionary solver = new AlienDictionary();
        System.out.println(solver.alienOrder(words));
    }
}
