package facebook;

public class WordDictionary {
    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s){
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.val = s;
            node.isWord = true;
        }

        public boolean search(String s) {
            TrieNode node = root;
            return searchHelper(s, 0, node);
        }

        private boolean searchHelper(String s, int pos, TrieNode node) {
            if (pos == s.length()) {
                return node.isWord;
            }
            char c = s.charAt(pos);
            if (c == '.') {
                for (int i = 0; i < 26; i++) {
                    if (node.children[i] != null) {
                        if (searchHelper(s, pos + 1, node.children[i])) return true;
                    }
                }
                return false;
            } else {
                int index = c - 'a';
                if (node.children[index] == null) {
                    return false;
                }
                return searchHelper(s, pos + 1, node.children[index]);
            }
        }
    }

    class TrieNode{
        TrieNode[] children;
        String val;
        boolean isWord;
        public TrieNode(){
            this.children = new TrieNode[26];
            this.val = "";
            this.isWord = false;
        }
    }
    Trie trie;
    /** Initialize your data structure here. */
    public WordDictionary() {
        this.trie = new Trie();
    }

    /** Adds a word into the data structure. */
    public void addWord(String word) {
        trie.insert(word);
    }

    /** Returns if the word is in the data structure. A word could contain the dot character '.' to represent any one letter. */
    public boolean search(String word) {
        return trie.search(word);
    }
}
