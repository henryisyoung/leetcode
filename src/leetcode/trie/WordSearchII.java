package leetcode.trie;

import java.util.ArrayList;
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
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        int rows = board.length, cols = board[0].length;
        boolean[][] visited = new boolean[rows][cols];
        TrieNode root = trie.root;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String str = "";
                dfsFindWord(i, j, root, str, visited, board, result);
            }
        }
        return result;
    }

    private void dfsFindWord(int r, int c, TrieNode root, String str, boolean[][] visited, char[][] board, List<String> result) {
        if (root.isWord && !result.contains(str)) {
            result.add(str);
            return;
        }
        int rows = board.length, cols = board[0].length;
        if (r < 0 || c < 0 || r >= rows || c >= cols || visited[r][c] || root.children[board[r][c] - 'a'] == null) {
            return;
        }

        TrieNode cur = root.children[board[r][c] - 'a'];
        visited[r][c] = true;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            dfsFindWord(nr, nc, cur, str + board[r][c], visited, board, result);
        }
        visited[r][c] = false;
    }

    public static void main(String[] args) {
        char[][] board = {
                {'o','a','a','n'},
                {'e','t','a','e'},
                {'i','h','k','r'},
                {'i','f','l','v'}
        };
        String[] words = {"oath","pea","eat","rain"};
        WordSearchII solver = new WordSearchII();
        List<String> result = solver.findWords(board, words);
        System.out.println(result.toString());
    }
}
