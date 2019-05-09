package pinterest;

import leetcode.ListNode;

import java.util.*;

public class WordBreakII {
    public List<String> wordBreak(String s, List<String> wordDict) {
        List<String> result = new ArrayList<>();
        if (wordDict == null || wordDict.size() == 0) {
            return result;
        }
        Set<String> set = new HashSet<>();
        set.addAll(wordDict);
        dfsSearchAll(s, "", set, 0, result);
        return result;
    }

    private void dfsSearchAll(String s, String cur, Set<String> wordDict, int pos, List<String> result) {
        if (pos == s.length()) {
            result.add(cur);
            return;
        }
        for (int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if (wordDict.contains(str)){
                if (cur.length() == 0) {
                    dfsSearchAll(s, cur + str, wordDict, i, result);
                } else {
                    dfsSearchAll(s, cur + " " + str, wordDict, i, result);
                }
            }
        }
    }

    public static void main(String[] args) {
        String s = "catsanddog";
        List<String> words = Arrays.asList("cat", "cats", "and", "sand", "dog");
        WordBreakII solver = new WordBreakII();
        System.out.println(solver.wordBreak(s, words));
    }
}
