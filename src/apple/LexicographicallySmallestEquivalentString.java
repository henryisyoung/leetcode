package apple;

import java.util.*;

public class LexicographicallySmallestEquivalentString {
    public String smallestEquivalentString(String s1, String s2, String baseStr) {
        if (s1 == null || s2 == null || s1.length() != s2.length() || s1.length() == 0) {
            return "";
        }
        Map<Character, Set<Character>> map = new HashMap<>();
        for (int i = 0; i < s1.length(); i++) {
            char c1 =s1.charAt(i), c2 = s2.charAt(i);
            map.putIfAbsent(c1, new HashSet<>());
            map.get(c1).add(c2);
            map.putIfAbsent(c2, new HashSet<>());
            map.get(c2).add(c1);
        }

        StringBuilder sb = new StringBuilder();
        Map<Character, Character> record = new HashMap<>();
        for (char c : baseStr.toCharArray()) {
            char smallest = findSmallest(record, map, c);
            record.put(c, smallest);
            sb.append(smallest);
        }
        return sb.toString();
    }

    private char findSmallest(Map<Character, Character> record, Map<Character, Set<Character>> map, char c) {
        if (record.containsKey(c)) return record.get(c);
        Queue<Character> queue = new LinkedList<>();
        queue.add(c);
        Set<Character> visited = new HashSet<>();
        visited.add(c);
        char result = c;

        while (!queue.isEmpty()) {
            char cur = queue.poll();
            result = cur < result ? cur : result;
            if (map.containsKey(cur)) {
                for (char next : map.get(cur)) {
                    if (visited.contains(next)) continue;
                    queue.add(next);
                    visited.add(next);
                }
            }
        }
        return result;
    }
}
