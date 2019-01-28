package leetcode.graphAndSearch;

import java.util.*;

public class WordLadderII {
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        if(!wordList.contains(endWord)) {
            return result;
        }
        Map<String, Integer> minLevel = new HashMap<>();
        Map<String, List<String>> reverseNeighbors = new HashMap<>();

        bfsSearchLevel(minLevel, reverseNeighbors, wordList, beginWord, endWord);

        dfsSearchAllCases(minLevel, reverseNeighbors, beginWord, endWord, result, new ArrayList<String>());
        return result;
    }

    private void dfsSearchAllCases(Map<String, Integer> minLevel, Map<String, List<String>> reverseNeightbors,
                                    String beginWord, String curWord, List<List<String>> result, ArrayList<String> list) {
        list.add(curWord);
        if(curWord.equals(beginWord)) {
            Collections.reverse(list);
            result.add(new ArrayList<>(list));
            Collections.reverse(list);
        } else {
            for (String nexWord : reverseNeightbors.get(curWord)) {
                if(minLevel.containsKey(nexWord) && minLevel.get(nexWord) + 1 == minLevel.get(curWord)) {
                    dfsSearchAllCases(minLevel, reverseNeightbors, beginWord, nexWord, result, list);
                }
            }
        }
        list.remove(list.size() - 1);
    }

    private void bfsSearchLevel(Map<String, Integer> minLevel, Map<String, List<String>> reverseNeighbors,
                                List<String> wordList, String beginWord, String endWord) {
        Queue<String> queue = new LinkedList<>();
        minLevel.put(beginWord, 0);
        queue.offer(beginWord);
        for (String word : wordList) {
            reverseNeighbors.put(word, new ArrayList<String>());
        }

        while (! queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String str = queue.poll();
                int level = minLevel.get(str);
                for (String neib : findNeighbors(str, wordList)) {
                    reverseNeighbors.get(neib).add(str);
                    if(! minLevel.containsKey(neib)) {
                        minLevel.put(neib, level + 1);
                        queue.offer(neib);
                        List<String> neighborList = new ArrayList<>();
                        neighborList.add(str);
                    }
                }
            }
        }
    }

    private List<String> findNeighbors(String str, List<String> wordList) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String newStr = replaceChar(str, i, c);
                if(wordList.contains(newStr)) {
                    result.add(newStr);
                }
            }
        }
        return result;
    }

    private String replaceChar(String str, int i, char c) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(i, c);
        return sb.toString();
    }
}
