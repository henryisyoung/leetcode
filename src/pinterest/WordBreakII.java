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
        Set<String> set = new HashSet<>();
        set.addAll(wordDict);
        Map<Integer, List<String>> map = new HashMap<>();
        return dfsSearchAllMemo(0, s, map, set);
    }

    private List<String> dfsSearchAllMemo(int pos, String s, Map<Integer, List<String>> map, Set<String> set) {
        if (map.containsKey(pos)) return map.get(pos);
        List<String> result = new ArrayList<>();
        if (pos == s.length()) {
            result.add("");
        }
        for (int end = pos + 1; end <= s.length(); end++) {
            String str = s.substring(pos, end);
            if (set.contains(str)) {
                List<String> nexts = dfsSearchAllMemo(end, s, map, set);
                for (String next : nexts) {
                    if (next.length() == 0) {
                        result.add(str);
                    } else {
                        result.add(str + " " + next);
                    }
                }
            }
        }
        map.put(pos, result);
        return result;
    }

    public static void main(String[] args) {
        String s = "catsanddog";
        List<String> words = Arrays.asList("cat", "cats", "and", "sand", "dog");
        WordBreakII solver = new WordBreakII();
        System.out.println(solver.wordBreakMemo(s, words));
    }
}
