package leetcode.graphAndSearch;

import java.util.*;

public class WordLadder {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {

        if(beginWord.equals(endWord)) return 0;
        wordList.add(beginWord);
        wordList.add(endWord);
        Set<String> set = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        set.add(beginWord);
        queue.add(beginWord);
        int length = 1;
        while (! queue.isEmpty()) {
            length++;
            int size = queue.size();
            for (int i = 0; i< size; i++){
                String cur = queue.poll();

                for (String nb : findNeighbors(cur, wordList)) {
                    if (! set.contains(nb)) {
                        queue.add(nb);
                        set.add(nb);
                    }
                    if (nb.equals(endWord)) {
                        return length;
                    }
                }
            }

        }
        return 0;
    }

    private List<String> findNeighbors(String s, List<String> wordList) {
        List<String> result = new ArrayList<>();
        for(int i = 0; i < s.length(); i++){
            for(char c = 'a'; c <= 'z'; c++){
                String cur = replace(s, i, c);
                if(c != s.charAt(i) && wordList.contains(cur)) {
                    result.add(cur);
                }
            }
        }
        return result;
    }

    public String replace(String head, int j, char c) {
        char[] arr = head.toCharArray();
        arr[j] = c;
        return new String(arr);
    }
}
