package pinterest;

import java.util.*;

public class WordLadderII {
    public static List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        if (wordList == null || wordList.size() == 0) {
            return result;
        }
        if(!wordList.contains(endWord)) {
            return result;
        }
        wordList.add(beginWord);
        Map<String, Integer> levelMap = new HashMap<>();
        Map<String, List<String>> neighborsMap = new HashMap<>();

        bfsBuildGraph(wordList, beginWord, endWord, levelMap, neighborsMap);
        for (String key : levelMap.keySet()) {
            System.out.println("key " + key + " level " + levelMap.get(key));
        }

        for (String key : neighborsMap.keySet()) {
            System.out.println("key " + key + " neighbors " + neighborsMap.get(key).toString());
        }
        dfsSearchAll(beginWord, endWord, new ArrayList<>(), result, levelMap, neighborsMap);
        return result;
    }

    private static void dfsSearchAll(String beginWord, String endWord, ArrayList<String> list, List<List<String>> result,
                                     Map<String, Integer> levelMap, Map<String, List<String>> neighborsMap) {
        if (beginWord.equals(endWord)) {
            List<String> copy = new ArrayList<>(list);
            copy.add(endWord);
            result.add(new ArrayList<>(copy));
            return;
        }
        list.add(beginWord);
        if (neighborsMap.containsKey(beginWord)) {
            for (String next : neighborsMap.get(beginWord)) {
                if (levelMap.containsKey(next) && levelMap.get(next) - 1 == levelMap.get(beginWord)) {
                    dfsSearchAll(next, endWord, list, result, levelMap, neighborsMap);
                }
            }
        }
        list.remove(list.size() - 1);
    }

    private static void bfsBuildGraph(List<String> wordList, String beginWord, String endWord, Map<String, Integer> levelMap,
                               Map<String, List<String>> neighborsMap) {
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        levelMap.put(beginWord, 0);
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            level++;
            for (int i = 0; i < size; i++){
                String cur = queue.poll();
                List<String> list = findNeighbors(cur, wordList);
                if (!neighborsMap.containsKey(cur)) {
                    neighborsMap.put(cur, list);
                }
                for (String next : list) {
                    if (!levelMap.containsKey(next)) {
                        levelMap.put(next, level);
                        queue.add(next);
                    }
                }
            }
        }
    }

    private static List<String> findNeighbors(String s, List<String> wordList) {
        List<String> result = new ArrayList<>();
        Set<String> set = new HashSet<>(wordList);
        for (int i = 0; i < s.length(); i++){
            for (char c = 'a'; c <= 'z'; c++) {
                if (c != s.charAt(i)) {
                    String next = replace(s, i, c);
                    if (set.contains(next)) {
                        result.add(next);
                    }
                }
            }
        }
        return result;
    }

    public static String replace(String head, int j, char c) {
        char[] arr = head.toCharArray();
        arr[j] = c;
        return new String(arr);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList("hot","dot","dog","lot","log","cog"));
        String beginWord = "hit", endWord = "cog";
        System.out.println(findLadders(beginWord, endWord, list));
    }
}
