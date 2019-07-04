package leetcode.trie;

import java.util.*;

public class WordFilter {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        Set<Integer> set;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.set = new HashSet<>();
        }
    }

    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s, int index) {
            TrieNode node = root;
            for (int i = 0;i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node.children[pos].set.add(index);
                node = node.children[pos];
            }
            node.isWord = true;
        }

        public Set<Integer> search(String s) {
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    return new HashSet<>();
                }
                node = node.children[pos];
            }
            return node.set;
        }
    }
    Trie trie;
    public WordFilter(String[] words) {
        this.trie = new Trie();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            trie.insert(word, i);
            String revWord = new StringBuilder(word).reverse().toString();
//            System.out.println(revWord);
            trie.insert(revWord, i);
        }
    }

    public int f(String prefix, String suffix) {
        Set<Integer> preList = trie.search(prefix);
        Set<Integer> sufList = trie.search(suffix);
//        System.out.println(preList.toString() + " pre");
//        System.out.println(sufList.toString() + " suf");
        if (preList.isEmpty() || sufList.isEmpty()) return -1;
        preList.retainAll(sufList);
//        System.out.println(preList.toString() + " pre 2");

        if (preList.isEmpty()) return -1;
        int result = Integer.MAX_VALUE;
        for (int i : preList) {
            result = Math.min(i, result);
        }
        return result;
    }

    public static void main(String[] args) {
//        WordFilter sovler = new WordFilter(new String[]{"apple"});
//        System.out.println(sovler.f("a", "e"));
//        System.out.println(sovler.f("b", ""));
        Random random = new Random();
        System.out.println(random.nextInt(500));
    }

}
