package uber.phone.leetcode;

import java.util.ArrayList;
import java.util.List;

public class WordSearchII {
    class Trie {
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode n = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (n.children[pos] == null) {
                    n.children[pos] = new TrieNode();
                }
                n = n.children[pos];
            }
            n.isWord = true;
            n.word = s;
        }

        public boolean find(String s) {
            TrieNode n = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (n.children[pos] == null) {
                    return false;
                }
                n = n.children[pos];
            }
            return n.isWord;
        }
    }

    class TrieNode{
        TrieNode[] children;
        boolean isWord;
        String word;

        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
        }
    }
    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) return result;
        Trie trie = new Trie();
        for (String w : words) trie.insert(w);

        int rows = board.length, cols = board[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean[][] visited = new boolean[rows][cols];
                findWord(i,j, trie.root, result, visited, board);
            }
        }
        return result;
    }

    private void findWord(int r, int c, TrieNode node, List<String> result, boolean[][] visited, char[][] board) {
        if (node.isWord && !result.contains(node.word)) {
            result.add(node.word);
            return;
        }
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || visited[r][c]) {
            return;
        }
        int pos = board[r][c] - 'a';
        if (node.children[pos] == null) return;

        visited[r][c] = true;
        int[][] dirs = {{1,0},{0,1},{-1,0},{0,-1}};
        node = node.children[pos];
        for (int[] dir : dirs) {
            findWord(r + dir[0], c + dir[1], node, result, visited, board);
        }
        visited[r][c] = false;
    }
}
