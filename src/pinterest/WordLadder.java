package pinterest;

import java.util.*;

public class WordLadder {
    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (wordList == null || wordList.size() == 0) {
            return 0;
        }
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        Set<String> set = new HashSet<>();
        set.add(beginWord);
        int count = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(endWord)) {
                    return count;
                }
                List<String> list = findNeighbors(cur, wordList);
//                System.out.println("cur " + cur + " list " + list.toString());
                for (String next : list) {
                    if (!set.contains(next)) {
                        set.add(next);
                        queue.add(next);
                    }
                }
            }
            count++;
        }
        return 0;
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
        List<String> list = Arrays.asList("hot","dot","dog","lot","log","cog");
        String beginWord = "hit", endWord = "cog";
        System.out.println(ladderLength(beginWord, endWord, list));
    }
}
