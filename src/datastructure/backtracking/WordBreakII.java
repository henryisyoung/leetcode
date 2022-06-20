package datastructure.backtracking;

import java.util.*;

public class WordBreakII {
    public static List<String> wordBreak(String s, List<String> wordDict) {
        List<String> result = new ArrayList<>();

        Set<String> set = new HashSet<>(wordDict);
        dfsFindAll(s, 0, set, result, "");
        return result;
    }

    private static void dfsFindAll(String s, int pos, Set<String> set, List<String> result, String cur) {
        if (pos == s.length()) {
            result.add(cur.trim());
            return;
        }
        for (int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if (set.contains(str)) {
                dfsFindAll(s, i, set, result, cur + " " + str);
            }
        }
    }

    public static void main(String[] args) {
        List<String> wordDict = Arrays.asList("cat","cats","and","sand","dog");
        String s = "catsanddog";
        System.out.println(wordBreak(s, wordDict));
    }
}
