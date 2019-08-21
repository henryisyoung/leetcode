package leetcode.graphAndSearch.backTracking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordPatternII2 {
    public boolean wordPatternMatch(String pattern, String str) {
        if (pattern == null || str == null || pattern.length() > str.length()) return false;
        Set<String> set = new HashSet<>();
        Map<Character, String> map = new HashMap<>();
        return dfsSearchAll(pattern, str, 0, 0, set, map);
    }

    private boolean dfsSearchAll(String pattern, String str, int pos1, int pos2, Set<String> set, Map<Character, String> map) {
        if (pos1 == pattern.length()) {
            return pos2 == str.length();
        }
        char c = pattern.charAt(pos1);
        if (map.containsKey(c)) {
            String s = map.get(c);
            if (str.startsWith(s, pos2)) {
                if (dfsSearchAll(pattern, str, pos1 + 1, pos2 + s.length(), set, map)) {
                    return true;
                }
            }
            return false;
        }
        for (int i = pos2 + 1; i <= str.length(); i++) {
            String s = str.substring(pos2, i);
            if (set.contains(s)) continue;
            map.put(c, s);
            set.add(s);
            if (dfsSearchAll(pattern, str, pos1 + 1, i, set, map)) {
                return true;
            }
            map.remove(c);
            set.remove(s);
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
