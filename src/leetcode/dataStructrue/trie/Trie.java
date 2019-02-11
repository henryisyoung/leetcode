package leetcode.dataStructrue.trie;

public class Trie {
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

    public TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(String s) {
        root.insert(s, 0);
    }

    public boolean search(String word) {
        TrieNode cur = root.find(word, 0);
        return cur != null && cur.isWord;
    }

    public boolean startsWith(String prefix) {
        TrieNode cur = root.find(prefix, 0);
        return cur != null;
    }
}