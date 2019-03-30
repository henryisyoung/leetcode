package airbnb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalindromePairs {
    public List<List<Integer>> palindromePairs(String[] words) {
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

    private boolean[][] isPalindromeHelper(String s) {
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];
        for(int i = 0; i < s.length(); i++){
            isPalindrome[i][i] = true;
        }
        for(int i = 0; i < s.length() - 1; i++){
            if(s.charAt(i) == s.charAt(i + 1)){
                isPalindrome[i][i + 1] = true;
            }
        }
        for (int len = 2; len < n; len++){
            for(int start = 0; start + len < n; start++){
                if(isPalindrome[start + 1][start + len - 1] && s.charAt(start) == s.charAt(start + len)) {
                    isPalindrome[start][start + len] = true;
                }
            }
        }
        return isPalindrome;
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
        List<List<Integer>> list = pp.palindromePairs(words);
        System.out.println(list.toString());
    }
}
