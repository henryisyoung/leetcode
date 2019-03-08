package airbnb;

import java.util.ArrayList;
import java.util.List;

public class BoggleGame {
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
        // Write your code here
        Trie trie = new Trie();
        for(String word : words) {
            trie.insert(word);
        }

        int m = board.length;
        int n = board[0].length;
        List<String> result = new ArrayList<>();
        boolean[][] visited = new boolean[m][n];
        List<String> path = new ArrayList<>();
        findWords(result, board, visited, path, 0, 0, trie.root);
        return result;
    }

    public static void findWords(List<String> result, char[][] board, boolean[][] visited, List<String> words, int x, int y, TrieNode root) {

        int m = board.length;
        int n = board[0].length;
        for (int i = x; i < m; i++) {
            for (int j = y; j < n; j++) {
                List<List<Integer>> nextWordIndexes = new ArrayList<>();
                getNextWords(nextWordIndexes, board, visited, new ArrayList<Integer>(), i, j, root);
                for (List<Integer> indexes : nextWordIndexes) {
                    String word = "";
                    for (int index : indexes) {
                        int row = index / n;
                        int col = index % n;
                        visited[row][col] = true;
                        word += board[row][col];
                    }
//                    System.out.println("word:" + word);
//                    System.out.println("i:" + i + " j:" + j);
                    words.add(word);
//                    System.out.println("words list:" + words.toString());
                    if (words.size() > result.size()) {
                        result.clear();
                        result.addAll(words);
                    }
                    findWords(result, board, visited, words, i, j, root);
                    for (int index : indexes) {
                        int row = index / n;
                        int col = index % n;
                        visited[row][col] = false;
                    }
                    words.remove(words.size() - 1);
                }
            }
        }
    }

    private static void getNextWords(List<List<Integer>> words, char[][] board,
                              boolean[][] visited, List<Integer> path, int i, int j, TrieNode root) {
        if(i < 0 | i >= board.length || j < 0 || j >= board[0].length
                || visited[i][j] == true || root.children[board[i][j] - 'a'] == null) {
            return;
        }

        root = root.children[board[i][j] - 'a'];
        if(root.isWord) {
            List<Integer> newPath = new ArrayList<>(path);
            newPath.add(i * board[0].length + j);
            words.add(newPath);
            return;
        }
        int []dx = {0, 1, 0, -1};
        int []dy = {1, 0, -1, 0};
        visited[i][j] = true;
        path.add(i * board[0].length + j);
        for (int k = 0; k < 4; k ++) {
            getNextWords(words, board, visited, path, i + dx[k], j + dy[k], root);
        }
        path.remove(path.size() - 1);
        visited[i][j] = false;
    }

    public static void main(String[] args) {
        char[][] board = {{'a','b','c'},{'d','e','f'},{'g','h','i'}};
        String[] words = {"abc","cfi","beh","defi","gh","deh", "dg"};
        List<String> list = boggleGame(board, words);
        System.out.println(list.toString());
    }
}
