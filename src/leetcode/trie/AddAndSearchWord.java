package leetcode.trie;

public class AddAndSearchWord {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        public TrieNode() {
            this.isWord = false;
            this.children = new TrieNode[26];
        }
    }

    class Trie{
        TrieNode root;
        public Trie(){
            this.root = new TrieNode();
        }

        public void insert(String s) {
            if (s == null) {
                return;
            }
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.isWord = true;
        }

        public boolean search(String s) {
            if (s == null) {
                return false;
            }
            TrieNode node = root;
            return searchAll(s, 0, node);
        }

        private boolean searchAll(String s, int index, TrieNode node) {
            if (index == s.length()) {
                return node.isWord;
            }
            if (s.charAt(index) == '.') {
                for (int i = 0; i < 26; i++) {
                    if (node.children[i] != null && searchAll(s, index + 1, node.children[i])) {
                        return true;
                    }
                }
                return false;
            } else {
                if (node.children[s.charAt(index) - 'a'] == null) {
                    return false;
                }
                return searchAll(s, index + 1, node.children[s.charAt(index) - 'a']);
            }
        }
    }
    /** Initialize your data structure here. */
    Trie trie;
    public AddAndSearchWord() {
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

    public static void main(String[] args) {
        AddAndSearchWord solver = new AddAndSearchWord();
        solver.addWord("word");
        System.out.println(solver.search("w..d"));
    }
}
