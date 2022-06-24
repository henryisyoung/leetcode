package apple;

import java.util.*;

public class WordBreakII {
    public static List<String> wordBreak(String s, List<String> wordDict) {
        List<String> result = new ArrayList<>();

        Set<String> set = new HashSet<>(wordDict);
        dfsSearchAll(set, s, 0, result, "");

        return result;
    }

    private static void dfsSearchAll(Set<String> set, String s, int pos, List<String> result, String cur) {
        if (pos == s.length()) {
            result.add(cur);
            return;
        }
        for (int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if (set.contains(str)) {
                if (cur == "") {
                    dfsSearchAll(set, s, i, result, str);
                } else {
                    dfsSearchAll(set, s, i, result, cur + " " + str);
                }
            }
        }
    }

    public static void main(String[] args) {
        String s = "catsanddog";
        List<String> wordDict = Arrays.asList( "cat","cats","and","sand","dog");
        System.out.println(wordBreak(s, wordDict));

        int[] a = {1,1,1,1};
        int[] b = {1,1,1,1};
        Map<String, Integer> map = new HashMap<>();
        map.put(Arrays.toString(a), map.getOrDefault(Arrays.toString(a), 0) + 1);
        map.put(Arrays.toString(b), map.getOrDefault(Arrays.toString(b), 0) + 1);

        System.out.println(map.get(Arrays.toString(new int[]{1,1,1,1})));
    }

}
