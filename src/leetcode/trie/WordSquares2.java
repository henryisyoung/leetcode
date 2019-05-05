package leetcode.trie;

import java.util.ArrayList;
import java.util.List;

public class WordSquares2 {
    class Trie {
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
                node.words.add(s);
            }
            node.isWord = true;
        }

        public List<String> findPrefixWords(String prefix) {
            List<String> result = new ArrayList<>();
            TrieNode node = root;
            for (int i = 0; i < prefix.length(); i++) {
                int pos = prefix.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    return result;
                }
                node = node.children[pos];
            }
            result.addAll(node.words);
            return result;
        }
    }

    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        List<String> words;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.words = new ArrayList<>();
        }
    }

    public List<List<String>> wordSquares(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        List<String> list = new ArrayList<>();
        for (String word : words) {
            list.add(word);
            dfsSearchAll(result, list, trie, word.length());
            list.remove(list.size() - 1);
        }
        return result;
    }

    private void dfsSearchAll(List<List<String>> result, List<String> list, Trie trie, int length) {
        if (list.size() == length) {
            result.add(new ArrayList<>(list));
            return;
        }
        int size = list.size();
        StringBuilder prefix = new StringBuilder();
        for (String s : list) {
            prefix.append(s.charAt(size));
        }
        List<String> allPrefixWords = trie.findPrefixWords(prefix.toString());
        for (String word : allPrefixWords) {
            list.add(word);
            dfsSearchAll(result, list, trie, length);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        String[] words = {"area","lead","wall","lady","ball"};
        WordSquares2 solver = new WordSquares2();
        List<List<String>> result =  solver.wordSquares(words);
        for (List<String> list : result) {
            System.out.println(list.toString());
        }
    }
}
