package airbnb;

import java.util.ArrayList;
import java.util.List;

public class BoggleGameII {
    static class Trie {
        TrieNode root;

        Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            if(word == null || word.length() == 0) {
                return;
            }
            TrieNode node = root;
            for(int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                if(node.children[ch - 'a'] == null) {
                    node.children[ch - 'a'] = new TrieNode();
                }
                node = node.children[ch - 'a'];
            }
            node.isWord = true;
        }
    }

    static class TrieNode {
        boolean isWord;
        TrieNode[] children;

        TrieNode() {
            isWord = false;
            children = new TrieNode[26];
        }
    }

    public static List<String> boggleGame(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0 ||
                words == null || words.length == 0) {
            return result;
        }
        int rows = board.length, cols = board[0].length;
        boolean[][] visited= new boolean[rows][cols];
        List<String> path = new ArrayList<>();
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        dfsSearch(0, 0, visited, trie.root, result, path, board);

        return result;
    }

    private static void dfsSearch(int r, int c, boolean[][] visited, TrieNode root, List<String> result,
                                  List<String> path, char[][] board) {
        int rows = board.length, cols = board[0].length;
        for (int i = r; i < rows; i++) {
            for (int j = c; j < cols; j++) {
                List<List<Integer>> words = new ArrayList<>();
                dfsFindWordsFromHere(words, i, j, visited, root, board, new ArrayList<Integer>());
                for (List<Integer> word : words) {
                    StringBuilder sb = new StringBuilder();
                    for (int pos : word) {
                        int row = pos / cols, col = pos % cols;
                        visited[row][col] = true;
                        sb.append(board[row][col]);
                    }
                    path.add(sb.toString());
                    if (path.size() > result.size()) {
                        result.clear();
                        result.addAll(path);
                    }
                    dfsSearch(i, j, visited, root, result, path, board);
                    for (int pos : word) {
                        int row = pos / cols, col = pos % cols;
                        visited[row][col] = false;
                    }
                    path.remove(path.size() - 1);
                }
            }
        }
    }

    private static void dfsFindWordsFromHere(List<List<Integer>> words, int r, int c, boolean[][] visited,
                                             TrieNode root, char[][] board, ArrayList<Integer> list) {
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || visited[r][c] || root.children[board[r][c] - 'a'] == null) {
            return;
        }
        root = root.children[board[r][c] - 'a'];
        if (root.isWord) {
            List<Integer> copy = new ArrayList<>(list);
            copy.add(r * cols + c);
            words.add(copy);
            return;
        }
        visited[r][c] = true;
        list.add(r * cols + c);
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nRow = dir[0] + r, nCol = dir[1] + c;
            dfsFindWordsFromHere(words, nRow, nCol, visited, root, board, list);
        }
        visited[r][c] = false;
        list.remove(list.size() - 1);
    }


    public static void main(String[] args) {
        char[][] board = {{'a','b','c'},{'d','e','f'},{'g','h','i'}};
        String[] words = {"abc","cfi","beh","defi","gh","deh", "dg"};
        List<String> list = boggleGame(board, words);
        System.out.println(list.toString());
    }
}
