package google;

import java.util.*;

public class FindAndReplacePattern {
    public static List<String> findAndReplacePattern(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        int[] target = posPattern(pattern);
        for (String word : words) {
            int[] pos = posPattern(word);
            if (Arrays.equals(pos, target)) {
                result.add(word);
            }
        }
        return result;
    }

    private static int[] posPattern(String pattern) {
        int[] pos = new int[pattern.length()];
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            if (!map.containsKey(pattern.charAt(i))) {
                map.put(pattern.charAt(i), i);
            }
            pos[i] = map.get(pattern.charAt(i));
        }
        return pos;
    }

    public static void main(String[] args) {
        String[] words = {"abc","deq","mee","aqq","dkd","ccc"};
        String pattern = "abb";
        List<String> result = findAndReplacePattern(words, pattern);
        System.out.println(result.toString());
    }
}
