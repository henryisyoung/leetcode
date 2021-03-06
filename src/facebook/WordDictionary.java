package facebook;

public class WordDictionary {
    class Trie{
        TrieNode root;
        public Trie(){
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode n = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if(n.children[pos] == null) {
                    n.children[pos] = new TrieNode();
                }
                n = n.children[pos];
            }
            n.isWord = true;
        }

        public boolean search(String s) {
            TrieNode n = root;

            return searchHelper(s, 0, n);
        }

        private boolean searchHelper(String s, int pos, TrieNode n) {
            if (pos == s.length()) return  n.isWord;
            char c  = s.charAt(pos);
            if (c == '.') {
                for (int i = 0; i < 26; i++) {
                    if (n.children[i] != null) {
                        if (searchHelper(s, pos + 1, n.children[i])) return true;
                    }
                }
                return false;
            } else {
                int index = c - 'a';
                if (n.children[index] == null) return false;
                return searchHelper(s, pos + 1, n.children[index]);
            }
        }
    }

    class TrieNode{
        TrieNode[] children;
        boolean isWord;
        public TrieNode() {
            this.children = new TrieNode[26];
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
