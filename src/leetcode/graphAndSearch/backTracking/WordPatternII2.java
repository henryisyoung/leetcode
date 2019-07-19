package leetcode.graphAndSearch.backTracking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordPatternII2 {

    public boolean wordPatternMatch(String pattern, String str) {
        Map<Character, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        return match(pattern, str, map, set);
    }

    private boolean match(String pattern, String str, Map<Character, String> map, Set<String> set) {
        if (pattern.length() == 0) {
            return str.length() == 0;
        }

        Character c = pattern.charAt(0);
        if (map.containsKey(c)) {
            if (!str.startsWith(map.get(c))) {
                return false;
            }
            return match(pattern.substring(1), str.substring(map.get(c).length()), map, set);
        }

        for (int i = 0; i < str.length(); i++) {
            String word = str.substring(0, i + 1);
            if (set.contains(word)) {
                continue;
            }
            map.put(c, word);
            set.add(word);
            if (match(pattern.substring(1), str.substring(i + 1), map, set)) {
                return true;
            }
            set.remove(word);
            map.remove(c);
        }

        return false;
    }

    public static void main(String[] args) {
        String pattern = "aabb", str = "xyzabcxzyabc";
        WordPatternII2 solver = new WordPatternII2();
        System.out.println(solver.wordPatternMatch(pattern, str));
    }
}
