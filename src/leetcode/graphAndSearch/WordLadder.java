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

    public int ladderLength2(String beginWord, String endWord, List<String> wordList) {
        int count = 1;
        if (!wordList.contains(endWord)) {
            return 0;
        }
        Queue<String> queue = new LinkedList<>();
        Set<String> set = new HashSet<>();
        queue.add(beginWord);
        set.add(beginWord);
        while (!queue.isEmpty()) {
            int size = queue.size();
            count++;
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                for (String neighbor : findNeighbors2(cur, wordList)) {
                    if (neighbor.equals(endWord)) {
                        return count;
                    }
                    if (!set.contains(neighbor)) {
                        queue.add(neighbor);
                        set.add(neighbor);
                    }
                }
            }

        }
        return 0;
    }

    private List<String> findNeighbors2(String cur, List<String> wordList) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < cur.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if(wordList.contains(replace(cur, i, c))) {
                    result.add(cur);
                }
            }
        }
        return result;
    }
}
