package facebook;

import java.util.*;

public class WordLadder {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        int len = 1;
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        wordList.add(endWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        Set<String> words = new HashSet<>(wordList);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(endWord)) return len;
                for (String next : findNeighbors(words, cur)) {
                    if (visited.contains(next)) continue;
                    queue.add(next);
                    visited.add(next);
                }
            }
            len++;
        }

        return 0;
    }

    private List<String> findNeighbors(Set<String> wordList, String cur) {
        List<String> result = new ArrayList<>();
        char[] arr = cur.toCharArray();
        for (int i = 0; i < cur.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (cur.charAt(i) != c) {
                    char tem = arr[i];
                    arr[i] = c;
                    if (wordList.contains(new String(arr))) result.add(new String(arr));
                    arr[i] = tem;
                }
            }
        }

        return result;
    }
}
