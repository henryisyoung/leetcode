package leetcode.graphAndSearch.backTracking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordPatternII2 {
    public boolean wordPatternMatch(String pattern, String str) {
        if (pattern == null || str == null || pattern.length() > str.length()) return false;
        Map<Character, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        return dfsSearchAll(map, 0, 0, pattern, str, set);
    }

    private boolean dfsSearchAll(Map<Character, String> map, int pPos, int sPos, String pattern, String str, Set<String> set) {
        if (pPos == pattern.length()) {
            return sPos == str.length();
        }
        char c = pattern.charAt(pPos);
        if (map.containsKey(c)) {
            String s = map.get(c);
            if (!str.startsWith(s, sPos)) {
                return false;
            }
            if (dfsSearchAll(map, pPos + 1, sPos + s.length(), pattern, str, set)) {
                return true;
            }
        } else {
            for (int i = sPos + 1; i <= str.length(); i++) {
                String s = str.substring(sPos, i);
                if (set.contains(s)) {
                    continue;
                }
                map.put(c, s);
                set.add(s);
                if (dfsSearchAll(map, pPos + 1, i, pattern, str, set)) {
                    return true;
                }
                set.remove(s);
                map.remove(c);
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String pattern = "aabb", str = "xyzabcxzyabc";
        String pattern2 = "aaaa", str2 = "asdasdasdasd";
        WordPatternII2 solver = new WordPatternII2();
        System.out.println(solver.wordPatternMatch(pattern, str));
        System.out.println(solver.wordPatternMatch(pattern2, str2));
    }
}
