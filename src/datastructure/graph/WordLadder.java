package datastructure.graph;

import java.util.*;

public class WordLadder {
    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        int level = 1;

        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        Set<String> set = new HashSet<>(wordList);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(endWord)) {
                    return level;
                }

                for (String child : findChildren(cur, set)) {
                    if (visited.contains(child)) continue;
                    visited.add(child);
                    queue.add(child);
                }
            }
            level++;
        }

        return 0;
    }

    public static Set<String> findChildren(String cur, Set<String> wordList) {
        Set<String> result = new HashSet<>();
        for (char i = 'a'; i <= 'z'; i++) {
            for (int j = 0; j < cur.length(); j++) {
                char c = cur.charAt(j);
                if (c != i) {
                    char[] array = cur.toCharArray();
                    array[j] = i;
                    if (wordList.contains(new String(array))) {
                        result.add(new String(array));
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String beginWord = "hit", endWord = "cog";
        List<String> wordList = Arrays.asList("hot","dot","dog","lot","log","cog");
        System.out.println(ladderLength(beginWord, endWord, wordList));

        Set<String> set = new HashSet<>(wordList);
        System.out.println(findChildren("hot", set));

    }
}
