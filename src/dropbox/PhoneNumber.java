package dropbox;

import leetcode.union.Union;

import java.util.*;

public class PhoneNumber {
    class TrieNode{
        TrieNode[] children;
        boolean isWord;
        String val;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.val = "";
        }
    }
    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode n = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (n.children[pos] == null) {
                    n.children[pos] = new TrieNode();
                }
                n = n.children[pos];
            }
            n.isWord = true;
            n.val = s;
        }

    }
    private final static String[] KEYS = {"","","abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    private static Set<String> dict;
    private Trie trie;

    public PhoneNumber(Set<String> dict) {
        this.dict = dict;
        this.trie = new Trie();
    }

    public List<String> letterCombinationsTrie(String digits) {
        for (String word : dict) {
            trie.insert(word);
        }
        List<String> result = new ArrayList<>();
        dfsCheck(0, digits, result, trie.root);
        return result;
    }

    private void dfsCheck(int pos, String digits, List<String> result, TrieNode root) {
        if (pos == digits.length()) {
            if (root.isWord) {
                result.add(root.val);
                return;
            }
        } else {
            String key = KEYS[digits.charAt(pos) - '0'];
            for (char c : key.toCharArray()) {
                int index = c - 'a';
                if (root.children[index] == null) {
                    continue;
                }
                dfsCheck(pos + 1, digits, result, root.children[index]);
            }
        }
    }

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.equals("") || dict == null || dict.isEmpty()) {
            return result;
        }
        dfsSearchAll(digits, 0, "", result);
        return result;
    }

    private void dfsSearchAll(String digits, int pos, String cur, List<String> result) {
        if(pos == digits.length() ) {
            if (dict.contains(cur)) {
                result.add(cur);
                return;
            }
        }
        else {
            String key = KEYS[digits.charAt(pos) - '0'];
            for (char c : key.toCharArray()) {
                dfsSearchAll(digits, pos + 1, cur + c, result);
            }
        }
    }


    public static void main(String[] args) {
        Set<String> dict = new HashSet<>();
        dict.add("drop");
        dict.add("box");
        dict.add("dropbox");

        List<String> combinations = new PhoneNumber(dict).letterCombinations("3767269");
        List<String> combinationsTrie = new PhoneNumber(dict).letterCombinationsTrie("3767269");
        for(String comb: combinations) {
            System.out.println(comb);
        }
        for(String comb: combinationsTrie) {
            System.out.println(comb);
        }
    }
}
