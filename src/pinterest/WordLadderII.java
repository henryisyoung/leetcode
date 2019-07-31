package pinterest;

import java.util.*;

public class WordLadderII {
    public static List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        Map<String, Integer> levelMap = new HashMap<>();
        Map<String, List<String>> map = new HashMap<>();
        bfsBuilder(map, levelMap, beginWord, wordList);
        dfsSearchAll(beginWord, endWord, map, levelMap, new ArrayList<>(), result);
        return result;
    }

    private static void dfsSearchAll(String beginWord, String endWord, Map<String, List<String>> map,
                                     Map<String, Integer> levelMap, ArrayList<String> list, List<List<String>> result) {
        if (beginWord.equals(endWord)) {
            List<String> copy = new ArrayList<>(list);
            copy.add(endWord);
            result.add(copy);
            return;
        }
        list.add(beginWord);
        if (map.containsKey(beginWord)) {
            for (String next : map.get(beginWord)) {
                if (levelMap.containsKey(next) && levelMap.get(next) - 1 == levelMap.get(beginWord)) {
                    dfsSearchAll(next, endWord, map, levelMap, list, result);
                }
            }
        }
        list.remove(list.size() - 1);
    }

    private static void bfsBuilder(Map<String, List<String>> map, Map<String, Integer> levelMap,
                                   String beginWord, List<String> wordList) {
        Queue<String> queue = new LinkedList<>();
        int level = 0;
        levelMap.put(beginWord, 0);
        queue.add(beginWord);
        while (!queue.isEmpty()) {
            level++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                List<String> nbs = findNeihgtbors(cur, wordList);
                map.putIfAbsent(cur, nbs);
                for (String next : map.get(cur)) {
                    if (!levelMap.containsKey(next)) {
                        levelMap.put(next, level);
                        queue.add(next);
                    }
                }
            }
        }
    }

    private static List<String> findNeihgtbors(String s, List<String> wordList) {
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
