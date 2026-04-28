package recovery;

import java.util.ArrayList;
import java.util.List;

public class WordSearch2 {

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
    }

    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord = false;
        String word = null;
    }

    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        if (board == null || board.length == 0 || board[0].length == 0) {
            return result;
        }

        Trie trie = new Trie();
        for (String w : words) {
            trie.insert(w);
        }

        int rows = board.length, cols = board[0].length;
        boolean[][] visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                findWord(i, j, trie.root, result, visited, board);
            }
        }

        return result;
    }

    private void findWord(int r, int c, TrieNode node, List<String> result, boolean[][] visited, char[][] board) {
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || visited[r][c]) {
            return;
        }

        int pos = board[r][c] - 'a';
        if (node.children[pos] == null) {
            return;
        }

        node = node.children[pos];

        if (node.isWord) {
            result.add(node.word);
            node.isWord = false; // 去重，但继续向下搜索更长的词
        }

        visited[r][c] = true;

        findWord(r + 1, c, node, result, visited, board);
        findWord(r - 1, c, node, result, visited, board);
        findWord(r, c + 1, node, result, visited, board);
        findWord(r, c - 1, node, result, visited, board);

        visited[r][c] = false;
    }
}
