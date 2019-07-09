package leetcode.trie;

import java.math.BigInteger;
import java.util.Random;

public class WordDictionary {
    private class TrieNode {
        public TrieNode[] children;
        public boolean isWord;

        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
        }

        public void insert(String s, int index) {
            if (s.length() == index) {
                isWord = true;
                return;
            }
            int pos = s.charAt(index) - 'a';
            if (children[pos] == null) {
                children[pos] = new TrieNode();
            }
            children[pos].insert(s, index + 1);
        }

        public TrieNode find(String s, int index) {
            if (s.length() == index) {
                return this;
            }
            int pos = s.charAt(index) - 'a';
            if (children[pos] == null) {
                return null;
            }
            return children[pos].find(s, index + 1);
        }
    }

    TrieNode root;

    /**
     * Initialize your data structure here.
     */
    public WordDictionary() {
        this.root = new TrieNode();
    }

    /**
     * Adds a word into the data structure.
     */
    public void addWord(String word) {
        TrieNode now = root;
        for (int i = 0; i < word.length(); i++) {
            int pos = word.charAt(i) - 'a';
            if (now.children[pos] == null) {
                now.children[pos] = new TrieNode();
            }
            now = now.children[pos];
        }
        now.isWord = true;
    }

    /**
     * Returns if the word is in the data structure. A word could contain the dot character '.' to represent any one letter.
     */
    public boolean search(String word) {
        return find(word, 0, root);
    }

    private boolean find(String word, int index, TrieNode root) {
        if (index == word.length()) return root.isWord;
        char c = word.charAt(index);
        if (c == '.') {
            for (int i = 0; i < 26; i++) {
                if (root.children[i] != null) {
                    if (find(word, index + 1, root.children[i])) {
                        return true;
                    }
                }
            }
            return false;
        } else if (root.children[c - 'a'] != null) {
            return find(word, index + 1, root.children[c - 'a']);
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
//        System.out.println(random.nextInt(500));

        String input1 = "456216545"
                + "452133155";

        String input2 = "0";

        // Convert the string input to BigInteger
        BigInteger a = new BigInteger(input1);
        BigInteger b = new BigInteger(input2);
        BigInteger div = a.divide(b);
    }

}
