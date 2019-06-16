package leetcode.solution;
import java.util.*;
public class Question127_BFS {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        int length = 1;
        Queue<String> queue = new LinkedList<>();
        if(beginWord.equals(endWord)) return length;
        Set<String> set = new HashSet<>();
        wordList.add(endWord);
        wordList.add(beginWord);
        queue.add(beginWord);
        set.add(beginWord);
        while (!queue.isEmpty()){
            int size = queue.size();
            length++;
            for (int i = 0;i < size;i++){
                String cur = queue.poll();
                for (String nb : find_neibors(cur, wordList)){
                    if(!set.contains(nb)){
                        set.add(nb);
                        queue.add(nb);
                    }
                    if(nb.equals(endWord)) return length;
                }
            }
        }
        return 0;
    }

    private List<String> find_neibors(String s, List<String> wordList) {
        List<String> result = new ArrayList<>();
        for(int i = 0; i < s.length(); i++){
            for(char c = 'a'; c <= 'z'; c++){
                String cur = replace(s, i, c);
                if(c != s.charAt(i) && wordList.contains(cur)) result.add(cur);
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
