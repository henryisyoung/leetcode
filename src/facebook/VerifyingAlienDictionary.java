package facebook;

import java.util.HashMap;
import java.util.Map;

public class VerifyingAlienDictionary {
    public static boolean isAlienSorted(String[] words, String order) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < order.length(); i++) {
            map.put(order.charAt(i), i);
        }
        for (int i = 0; i < words.length - 1; i++) {
            String a = words[i], b = words[i + 1];
            int j = 0;
            for (; j < Math.min(a.length(), b.length()); j++) {
                if (a.charAt(j) == b.charAt(j)) continue;
                int aPos= map.get(a.charAt(j)), bPos= map.get(b.charAt(j));
                if (aPos > bPos) {
                    return false;
                } else {
                    break;
                }
            }
            if (j == Math.min(a.length(), b.length()) && a.length() > b.length()) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String[] words = {"hello","leetcode"};
        String[] words2 = {"word","world","row"};
        String order = "hlabcdefgijkmnopqrstuvwxyz";
        String order2 = "worldabcefghijkmnpqstuvxyz";
        System.out.println(isAlienSorted(words, order));
        System.out.println(isAlienSorted(words2, order2));
    }
}
