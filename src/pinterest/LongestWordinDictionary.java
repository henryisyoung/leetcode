package pinterest;

import java.util.*;

public class LongestWordinDictionary {
    public String longestWord(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }
        Set<String> set = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        for (String word : words) {
            set.add(word);
            if (word.length() == 1) {
                queue.add(word);
            }
        }
        int max = -1;
        String result = "";
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            if (cur.length() > max) {
                max = cur.length();
                result = cur;
            } else if (cur.length() == max) {
                result = result.compareTo(cur) > 0 ? cur : result;
            }
            for (char c = 'a'; c <= 'z'; c++) {
                String next = cur + c;
                if (set.contains(next)) {
                    queue.add(next);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String[] words = {"w","wo","wor","worl", "world"};
        LongestWordinDictionary sovler = new LongestWordinDictionary();
        System.out.println(sovler.longestWord(words));
    }
}
