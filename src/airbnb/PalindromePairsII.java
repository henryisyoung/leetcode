package airbnb;

import org.omg.CORBA.INTERNAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalindromePairsII {
    private class Node {
        int pos;
        String word;
        boolean[][] isPalindrome;
        public Node (int i, String word) {
            this.pos = i;
            this.word = word;
            this.isPalindrome = buildPalindrome(word);
        }

        private boolean[][] buildPalindrome(String word) {
            int n = word.length();
            boolean[][] isPalindrome = new boolean[n][n];
            for (int i = 0; i < n; i++) {
                isPalindrome[i][i] = true;
            }
            for (int i = 0; i < n - 1; i++) {
                if (word.charAt(i) == word.charAt(i + 1)) {
                    isPalindrome[i][i + 1] = true;
                }
            }
            for (int len = 2; len < n; len++) {
                for (int start = 0; start + len < n; start++) {
                    if (isPalindrome[start + 1][start + len - 1] && word.charAt(start) == word.charAt(start + len)) {
                        isPalindrome[start][start + len] = true;
                    }
                }
            }
            return isPalindrome;
        }
    }
    public List<List<Integer>> palindromePairs(String[] words) {
        List<List<Integer>> result = new ArrayList<>();

        Map<String, Node> map = new HashMap<>();
        int n = words.length;
        for (int i = 0; i < n; i++) {
            map.put(words[i], new Node(i, words[i]));
        }
        for (int i = 0; i < n; i++) {
            String word = words[i];
            Node node = map.get(word);
            boolean[][] isP = node.isPalindrome;
            for (int j = 0; j < word.length(); j++) {
                if (j == word.length() - 1) {
                    String reverseWord = new StringBuilder(word).reverse().toString();
                    if (map.containsKey(reverseWord) && map.get(reverseWord).pos != i) {
                        List<Integer> list = new ArrayList<>();
                        list.add(i);
                        list.add(map.get(reverseWord).pos);
                        result.add(list);
                        list = new ArrayList<>();
                        list.add(map.get(reverseWord).pos);
                        list.add(i);
                        result.add(list);
                    }
                } else {
                    if (isP[0][j]) {

                    }
                    if (isP[j][word.length() - 1]) {

                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String[] words = {"abc", "cb"};
        PalindromePairsII pp = new PalindromePairsII();
        List<List<Integer>> list = pp.palindromePairs(words);
        System.out.println(list.toString());
    }
}
