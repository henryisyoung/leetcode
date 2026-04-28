package LinkedIn;

import java.util.*;

public class WordLadder {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        int level = 1;
        Set<String> words = new HashSet<>();
        words.addAll(wordList);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(endWord)) {
                    return level;
                }
                for(String next : findChildren(cur, words)) {
                    if (visited.contains(next)) continue;
                    visited.add(next);
                    queue.add(next);
                }
            }
            level++;
        }
        return 0;
    }

    private List<String> findChildren(String cur, Set<String> words) {
        List<String> result = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            for (int j = 0; j < cur.length(); j++) {
                char orgChar = cur.charAt(j);
                if (c != orgChar) {
                    char[] array = cur.toCharArray();
                    array[j] = c;
                    if (words.contains(new String(array))) {
                        result.add(new String(array));
                    }
                }
            }
        }
        return result;
    }
}
