package pinterest;

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

    public List<String> wordBreakMemo(String s, List<String> wordDict) {
        List<String> result = new ArrayList<>();
        if (wordDict == null || wordDict.size() == 0) {
            return result;
        }
        Set<String> set = new HashSet<>();
        set.addAll(wordDict);
        Map<Integer, List<String>> map = new HashMap<>();
        return dfsSearchAllMemo(map, s, set, 0);
    }

    private List<String> dfsSearchAllMemo(Map<Integer, List<String>> map, String s, Set<String> set, int start) {
        if (map.containsKey(start)) {
            return map.get(start);
        }
        List<String> result = new ArrayList<>();
        if (start == s.length()) {
            result.add("");
        }
        for (int end = start + 1; end <= s.length(); end++) {
            String sub = s.substring(start, end);
            if (set.contains(sub)) {
                List<String> next = dfsSearchAllMemo(map, s, set, end);
                for (String str : next) {
                    if (str.length() == 0) {
                        result.add(sub);
                    } else {
                        result.add(sub + " " + str);
                    }
                }
            }
        }
        map.put(start, result);
        return result;
    }

    public static void main(String[] args) {
        String s = "catsanddog";
        List<String> words = Arrays.asList("cat", "cats", "and", "sand", "dog");
        WordBreakII solver = new WordBreakII();
        System.out.println(solver.wordBreakMemo(s, words));
    }
}
