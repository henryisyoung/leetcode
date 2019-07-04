package leetcode.trie;

import java.util.*;

public class MapSum {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        int val;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.val = 0;
        }
    }

    class Trie{
        TrieNode root;
        Map<String, Integer> map;
        public Trie() {
            this.root = new TrieNode();
            this.map = new HashMap<>();
        }

        public void insert(String s, int val) {
            int delta = val - map.getOrDefault(s, 0);
            map.put(s, val);
            TrieNode node = root;
            for (int i = 0;i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node.children[pos].val += delta;
                node = node.children[pos];
            }
            node.isWord = true;
        }

        public int search(String s) {
            TrieNode node = root;

            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    return 0;
                }
                node = node.children[pos];
            }
            return node.val;
        }
    }
    Trie trie;
    public MapSum() {
        this.trie = new Trie();
    }

    public void insert(String key, int val) {
        trie.insert(key, val);
    }

    public int sum(String prefix) {
        return trie.search(prefix);
    }

    public static void main(String[] args) {
        MapSum solver = new MapSum();
        solver.insert("aa", 3);
        System.out.println(solver.sum("a"));
        solver.insert("aa", 2);
        System.out.println(solver.sum("a"));
    }
}
