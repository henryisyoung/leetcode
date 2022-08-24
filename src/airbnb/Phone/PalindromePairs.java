package airbnb.Phone;

import java.util.*;

public class PalindromePairs {
    public List<List<Integer>> palindromePairs(String[] words) {
        List<List<Integer>> result = new ArrayList<>();

        Map<String, Integer> map = new HashMap<>();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            map.put(word, i);
        }

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            for (int j = 0; j <= word.length(); j++) {
                String first = word.substring(0, j), second = word.substring(j);
                if (isPalindrome(first)) {
                    String reverseSecond = new StringBuilder(second).reverse().toString();
                    if (map.containsKey(reverseSecond) && map.get(reverseSecond) != i) {
                        int index = map.get(reverseSecond);
                        result.add(Arrays.asList(index, i));
                    }
                }
                if (isPalindrome(second)) {
                    String reverseFirst = new StringBuilder(first).reverse().toString();
                    if (map.containsKey(reverseFirst) && map.get(reverseFirst) != i) {
                        int index = map.get(reverseFirst);
                        result.add(Arrays.asList(i, index));
                    }
                }
            }
        }

        return result;
    }

    private boolean isPalindrome(String str) {
        int l = 0, r = str.length() - 1;
        while (l < r) {
            if (str.charAt(l) != str.charAt(r)) {
                return false;
            }
            l++;
            r--;
        }
        return true;
    }
}
