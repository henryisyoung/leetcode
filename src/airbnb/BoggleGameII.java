package airbnb;

import java.util.ArrayList;
import java.util.List;

public class BoggleGameII {
    static class Trie {
        TrieNode root;
        public Trie () {
            this.root = new TrieNode('*');
        }

        public void insert(String s) {
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode(s.charAt(i));
                }
                node = node.children[pos];
            }
            node.isWord = true;
        }
    }

    static class TrieNode {
        TrieNode[] children;
        boolean isWord;
        char val;

        public TrieNode(char val) {
            this.children = new TrieNode[26];
            this.val = val;
        }
    }

    public static List<String> boggleGame(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        boolean[][] isVisited = new boolean[board.length][board[0].length];
        List<String> wordsList = new ArrayList<>();
        dfsSearchWords(result, trie.root, board, isVisited, 0, 0, wordsList);
        return result;
    }

    private static void dfsSearchWords(List<String> result, TrieNode root, char[][] board, boolean[][] isVisited, int r, int c, List<String> wordsList) {
        int rows = board.length, cols = board[0].length;
        for (int i = r; i < rows; i++) {
            for (int j = c; j < cols; j++) {
                List<List<Integer>> paths = new ArrayList<>();
                List<Integer> singleWordPath =  new ArrayList<>();
                dfsFindWordsFromHere(i, j, paths, root, isVisited, board, singleWordPath);
                for (List<Integer> path : paths) {
                    StringBuilder sb = new StringBuilder();
                    for (Integer pos : path) {
                        int curR = pos / cols, curC = pos % cols;
                        sb.append(board[curR][curC]);
                        isVisited[curR][curC] = true;
                    }
                    wordsList.add(sb.toString());
                    if (wordsList.size() > result.size()) {
                        result.clear();
                        result.addAll(wordsList);
                    }
                    dfsSearchWords(result, root, board, isVisited, i, j, wordsList);
                    for (Integer pos : path) {
                        int curR = pos / cols, curC = pos % cols;
                        isVisited[curR][curC] = false;
                    }
                    wordsList.remove(wordsList.size() - 1);
                }
            }
        }
    }

    private static void dfsFindWordsFromHere(int r, int c, List<List<Integer>> paths, TrieNode root, boolean[][] isVisited, char[][] board, List<Integer> singleWordPath) {
        int rows = board.length, cols = board[0].length;
        if (r < 0 || c < 0 || r >= rows || c >= cols || isVisited[r][c] || root.children[board[r][c] - 'a'] == null) {
            return;
        }
        root = root.children[board[r][c] - 'a'];

        isVisited[r][c] = true;
        if (root.isWord) {
            List<Integer> list = new ArrayList<>(singleWordPath);
            list.add(r * rows + c);
            paths.add(list);
            return;
        }
        singleWordPath.add(r * rows + c);
        int[][] dirs = {{1,0},{-1,0},{0,-1},{0,1}};
        for (int[] dir : dirs) {
            int nR = r + dir[0], nC = c + dir[1];
            dfsFindWordsFromHere(nR, nC, paths, root, isVisited, board, singleWordPath);
        }
        isVisited[r][c] = false;
        singleWordPath.remove(singleWordPath.size() - 1);
    }

    public static void main(String[] args) {
        char[][] board = {{'a','b','c'},{'d','e','f'},{'g','h','i'}};
        String[] words = {"abc","cfi","beh","defi","gh","deh"};
        List<String> list = boggleGame(board, words);
        System.out.println(list.toString());
    }
}
