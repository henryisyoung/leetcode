package reddit.New2026.leetacode;

import java.util.*;

public class WorldLadder {
    public boolean validLadder(List<String> list) {
        if (list == null || list.isEmpty()) return true;
        int size = list.size();
        for (int i = 0; i < size - 1; i++) {
            String a = list.get(i), b = list.get(i + 1);
            if (!hsaOneDiff(a, b)) return false;
        }
        return true;
    }

    private boolean hsaOneDiff(String a, String b) {
        if (a.length() != b.length() || a.equals(b)) return false;
        int count = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) count++;
        }

        return count == 1;
    }

    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        Set<String> set = new HashSet<>(wordList);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);

        int dist = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(endWord)) return dist;
                for (String next : findChildren(cur, set)) {
                    if (visited.contains(next)) continue;
                    visited.add(next);
                    queue.add(next);
                }
            }
            dist++;
        }

        return 0;
    }

    private static List<String> findChildren(String cur, Set<String> set) {
        List<String> list = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < cur.length(); i++) {
                if (c == cur.charAt(i)) continue;
                char[] arr = cur.toCharArray();
                arr[i] = c;
                if (set.contains(new String(arr))) {
                    list.add(new String(arr));
                }
            }
        }

        return list;
    }

    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        Map<String, Integer> level = new HashMap<>();
        Set<String> set = new HashSet<>(wordList);
        if (!buildMap(map, level, beginWord, endWord, set)) {
            return result;
        }

        List<String> list = new ArrayList<>();
        list.add(beginWord);
        dfsFindAll(result, map, level, beginWord, endWord, list);


        return result;
    }

    private void dfsFindAll(List<List<String>> result, Map<String, List<String>> map, Map<String, Integer> level, String cur, String endWord, List<String> list) {
        if (cur.equals(endWord)) {
            result.add(new ArrayList<>(list));
            return;
        }

        if (map.containsKey(cur)) {
            for (String next : map.get(cur)) {
                if (level.get(next) == level.get(cur) + 1) {
                    list.add(next);
                    dfsFindAll(result, map, level, next, endWord, list);
                    list.remove(list.size() - 1);
                }
            }
        }
    }

    private boolean buildMap(Map<String, List<String>> map, Map<String, Integer> level, String str, String endWord, Set<String> set) {
        Queue<String> queue = new LinkedList<>();
        queue.add(str);
        Set<String> visited = new HashSet<>();
        visited.add(str);
        level.put(str, 0);
        int dist = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(endWord)) return true;
                List<String> nexts =  findChildren(cur, set);
                map.put(cur, nexts);
                for (String next : nexts) {
                    if (visited.contains(next)) continue;
                    visited.add(next);
                    queue.add(next);
                    level.put(next, dist);
                }
            }
            dist++;
        }

        return false;
    }
}