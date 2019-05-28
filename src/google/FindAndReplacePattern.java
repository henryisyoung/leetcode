package google;

import java.util.*;

public class FindAndReplacePattern {
    public List<String> findAndReplacePattern(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        int[] target = getPos(pattern);
        for (String word : words) {
            int[] cur = getPos(word);
            if (Arrays.equals(cur, target)) {
                result.add(word);
            }
        }
        return result;
    }

    private int[] getPos(String pattern) {
        Map<Character, Integer> map = new HashMap<>();
        int n = pattern.length();
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) {
            char c = pattern.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c, i);
            }
            pos[i] = map.get(c);
        }
        return pos;
    }
}
