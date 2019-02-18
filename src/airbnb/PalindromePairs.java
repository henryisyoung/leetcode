package airbnb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalindromePairs {
    public List<List<Integer>> palindromePairs(String[] words) {
        List<List<Integer>> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }
        int n = words.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int val = isPalindromePair(words[i], words[j]);
                if (val != 0) {
                    if (val == 2) {
                        List<Integer> list1 = new ArrayList<>();
                        List<Integer> list2 = new ArrayList<>();
                        list1.add(i);
                        list1.add(j);
                        result.add(list1);
                        list2.add(j);
                        list2.add(i);
                        result.add(list2);
                    } else if (val == 1) {
                        List<Integer> list1 = new ArrayList<>();
                        list1.add(i);
                        list1.add(j);
                        result.add(list1);
                    } else {
                        List<Integer> list2 = new ArrayList<>();
                        list2.add(j);
                        list2.add(i);
                        result.add(list2);
                    }

                }
            }
        }
        return result;
    }

    private int isPalindromePair(String word, String word1) {
        String s1 = word + word1;
        String s2 = word1 + word;
        String reverseS1 = new StringBuilder(s1).reverse().toString();
        String reverseS2 = new StringBuilder(s2).reverse().toString();
        if (s1.equals(reverseS1) && s2.equals(reverseS2)) {
            return 2;
        } else if (s1.equals(reverseS1)) {
            return 1;
        } else if (s2.equals(reverseS2)) {
            return -1;
        }
        return 0;
    }

    public List<List<Integer>> palindromePairs2(String[] words) {
        List<List<Integer>> result = new ArrayList<>();

        Map<String, Integer> map = new HashMap<>();
        int n = words.length;
        for (int i = 0; i < n; i++) {
            map.put(words[i], i);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= words[i].length(); j++) {
                String s1 = words[i].substring(0, j);
                String s2 = words[i].substring(j);
                if (isPalindrome(s1)) {
                    List<Integer> list = new ArrayList<>();
                    String reverseS2 = new StringBuilder(s2).reverse().toString();
                    if (map.containsKey(reverseS2) && map.get(reverseS2) != i) {
                        list.add(map.get(reverseS2));
                        list.add(i);
                        result.add(list);
                    }
                }
                if (isPalindrome(s2)) {
                    List<Integer> list = new ArrayList<>();
                    String reverseS1 = new StringBuilder(s1).reverse().toString();
                    if (map.containsKey(reverseS1) && map.get(reverseS1) != i && s2.length() != 0) {
                        list.add(i);
                        list.add(map.get(reverseS1));
                        result.add(list);
                    }
                }
            }
        }
        return result;
    }

    private boolean isPalindrome(String str) {
        int left = 0, right = str.length() - 1;
        while (left < right) {
            if (str.charAt(left++) != str.charAt(right--)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String[] words = {"abc", "cb"};
        PalindromePairs pp = new PalindromePairs();
        List<List<Integer>> list = pp.palindromePairs2(words);
        System.out.println(list.toString());
    }
}
