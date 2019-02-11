package leetcode.dataStructrue.trie;

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

    private boolean find(String word, int index, TrieNode now) {
        if (word.length() == index) {
            return now.isWord;
        }
        char cur = word.charAt(index);
        if (cur == '.') {
            for (int i = 0; i < 26; i++) {
                if (now.children[i] != null) {
                    if (find(word, index + 1, now.children[i])) {
                        return true;
                    }
                }
            }
            return false;
        } else if (now.children[cur - 'a'] == null) {
            return false;
        } else {
            return find(word, index + 1, now.children[cur - 'a']);
        }
    }

}
