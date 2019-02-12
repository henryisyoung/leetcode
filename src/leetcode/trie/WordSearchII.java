package leetcode.trie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WordSearchII {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        String string;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.string = null;
        }
    }

    class Trie {
        TrieNode root;
        public Trie() {
            root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode now = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (now.children[pos] == null) {
                    now.children[pos] = new TrieNode();
                }
                now = now.children[pos];
            }
            now.isWord = true;
            now.string = s;
        }

        public boolean search(String s) {
            return find(s, 0, root);
        }

        private boolean find(String s, int index, TrieNode now) {
            if (s.length() == index) {
                return now.isWord;
            }
            int pos = s.charAt(index) - 'a';
            if (now.children[pos] == null) {
                return false;
            }
            return find(s, index + 1, now.children[pos]);
        }
    }
    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        if (words == null || words.length == 0 || board == null || board.length == 0 ||
                board[0] == null || board[0].length == 0) {
            return result;
        }
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        TrieNode root = trie.root;

        int rows = board.length, cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dfsSearch(i, j, board, root, result);
            }
        }

        return result;
    }

    private void dfsSearch(int r, int c, char[][] board, TrieNode root, List<String> result) {
        if (root.isWord && !result.contains(root.string)) {
            result.add(root.string);
            return;
        }
        int rows = board.length, cols = board[0].length;

        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] == 0 || root.children[board[r][c] - 'a'] == null) {
            return;
        }
        char cur = board[r][c];
        board[r][c] = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nRow = r + dir[0], nCol = c + dir[1];
            dfsSearch(nRow, nCol, board, root.children[cur - 'a'], result);
        }
        board[r][c] = cur;
    }
}
