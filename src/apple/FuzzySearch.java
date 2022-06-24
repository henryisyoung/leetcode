package apple;

public class FuzzySearch {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        public TrieNode() {
            this.isWord = false;
            this.children = new TrieNode[26];
        }
    }

    class Trie {
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode cur = root;
            for (int i = 0;  i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (cur.children[pos] == null) {
                    cur.children[pos] = new TrieNode();
                }
                cur = cur.children[pos];
            }
            cur.isWord = true;
        }

        public boolean search(String s) {
            TrieNode cur = root;
            return searchHelper(s, 0, cur);
        }

        private boolean searchHelper(String s, int index, TrieNode cur) {
            if (index == s.length()) {
                return cur.isWord;
            }
            if (s.charAt(index) == '?') {
                for (TrieNode next : cur.children) {
                    if (next != null && searchHelper(s, index + 1, next)) {
                        return true;
                    }
                }
            } else {
                int pos = s.charAt(index) - 'a';
                if (cur.children[pos] == null) return false;
                if (searchHelper(s, index + 1, cur.children[pos])) {
                    return true;
                }
            }
            return false;
        }
    }

    Trie trie;
    public FuzzySearch(String[] strs) {
        this.trie = new Trie();
        for (String str : strs) {
            trie.insert(str);
        }
    }

    public boolean search(String word) {
        return trie.search(word);
    }

    public static void main(String[] args) {
        FuzzySearch search = new FuzzySearch(new String[]{"abd", "akaal", "alcc"});
        System.out.println(search.search("a?a?"));
    }
}
