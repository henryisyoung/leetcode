package reddit.New2026.leetacode;

import java.util.ArrayList;
import java.util.List;

public class WordSearch {
    public boolean exist(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(findAll(i,j, word, board, 0, new boolean[rows][cols])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findAll(int r, int c, String word, char[][] board, int pos, boolean[][] visited) {
        int rows = board.length, cols = board[0].length;
        if(pos == word.length()) return true;
        if(r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != word.charAt(pos) || visited[r][c] ){
            return false;
        }
        visited[r][c] = true;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for(int[] dir : dirs) {
            if(findAll(r + dir[0], c + dir[1], word, board, pos + 1,visited)){
                return true;
            }
        }
        visited[r][c] = false;
        return false;
    }

    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord = false;
        String word = "";
    }

    class Trie {
        TrieNode root = new TrieNode();

        public Trie() {

        }

        public void addWord(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                int pos = c - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.isWord = true;
            node.word = word;
        }
    }

    public List<String> findWords(char[][] board, String[] words) {
        Trie trie = new Trie();
        for (String word : words) {
            trie.addWord(word);
        }
        List<String> results = new ArrayList<>();
        int rows = board.length, cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean[][] visited = new boolean[rows][cols];
                dfsFindNode(i, j, trie.root, board, visited, results);
            }
        }
        return results;
    }

    private void dfsFindNode(int r, int c, TrieNode node, char[][] board, boolean[][] visited, List<String> results) {
        if (node.isWord) {
            results.add(node.word);
            node.isWord = false;
        }
        int rows = board.length, cols = board[0].length;

        if (r >= rows || c >= cols || r < 0 || c < 0 || visited[r][c] || node.children[board[r][c] - 'a'] == null) {
            return;
        }

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        visited[r][c] = true;
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            dfsFindNode(nr, nc, node.children[board[r][c] - 'a'], board, visited, results);
        }

        visited[r][c] = false;
    }

}
