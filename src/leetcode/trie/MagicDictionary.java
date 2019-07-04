package leetcode.trie;

import java.util.HashSet;
import java.util.Set;

public class MagicDictionary {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        String word;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.word = "";
        }
    }

    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode node = root;
            for (int i = 0;i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.isWord = true;
            node.word = s;
        }

        public boolean search(String s) {
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    return false;
                }
                node = node.children[pos];
            }
            return node.isWord;
        }
    }
    Trie trie;
    Set<String> set;
    /** Initialize your data structure here. */
    public MagicDictionary() {
        this.trie = new Trie();
        this.set = new HashSet<>();
    }

    /** Build a dictionary through a list of words */
    public void buildDict(String[] dict) {
        for (String str : dict) {
            set.add(str);
            for (int i = 0; i < str.length(); i++) {
                for (char c = 'a'; c <= 'z'; c++) {
//                    System.out.println(str.substring(0, i) + c + str.substring(i + 1));
                    trie.insert(str.substring(0, i) + c + str.substring(i + 1));
                }
            }
        }
    }

    /** Returns if there is any word in the trie that equals to the given word after modifying exactly one character */
    public boolean search(String word) {
        return !set.contains(word) && trie.search(word);
    }

    public static void main(String[] args) {
        String[] dict = {"hello", "leetcode"};
        MagicDictionary solver = new MagicDictionary();
        solver.buildDict(dict);
        System.out.println(solver.search("hell"));
    }
}
