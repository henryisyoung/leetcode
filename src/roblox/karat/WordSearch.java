package roblox.karat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordSearch {
    public static boolean searchWord1(String s, List<Character> chars) {
        int[] sTable = new int[256];
        int[] charsTable = new int[256];
        for (char c : chars) charsTable[c]++;
        for (char c : s.toCharArray()) sTable[c]++;
        for (char c : s.toCharArray()) {
            if (sTable[c] > charsTable[c]) return false;
        }
        return true;
    }

    public static List<int[]> searchWord2(String s, char[][] board) {
        if (s == null || s.length() == 0)
            return new ArrayList<>();

        if (board == null || board[0] == null) {
            return new ArrayList<>();
        }
        int rows = board.length, cols = board[0].length;
        if (rows * cols < s.length()) {
            return new ArrayList<>();
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                List<int[]> list = new ArrayList<>();
                if (dfsFind(i, j, s, board, list, 0)) {
                    return list;
                }
            }
        }
        return new ArrayList<>();
    }

    private static boolean dfsFind(int r, int c, String s, char[][] board, List<int[]> list, int pos) {
        if (list.size() == s.length()) {
            return true;
        }
        int rows = board.length, cols = board[0].length;

        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != s.charAt(pos)) {
            return false;
        }
        list.add(new int[]{r, c});
        int[][] dirs = {{1,0},{0,1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (dfsFind(nr, nc, s, board, list, pos + 1)) {
                return true;
            }
        }
        list.remove(list.size() - 1);
        return false;
    }

    public static List<List<int[]>> boggleGame(char[][] board, String[] words)  {
        List<List<int[]>> result = new ArrayList<>();
        if (words == null || words.length == 0)
            return new ArrayList<>();

        if (board == null || board[0] == null) {
            return new ArrayList<>();
        }
        int rows = board.length, cols = board[0].length;
        Trie trie = new Trie();
        for (String s : words)  {
            trie.insert(s);
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean[][] visited = new boolean[rows][cols];
                if (dfsSearchAll(trie.root, result, visited, board, 0, 0, words)) {
                    return result;
                }
            }
        }

        return result;
    }

    private static boolean dfsSearchAll(TrieNode root, List<List<int[]>> result, boolean[][] visited, char[][] board, int r, int c, String[] words) {
        if (result.size() == words.length) {
            return true;
        }
        int rows = board.length, cols = board[0].length;

        for (int i = r; i < rows; r++) {
            for (int j = c; j < cols; j++) {
                List<int[]> nextWordIndex = new ArrayList<int[]>();
                findWord(i, j, root, visited, board, nextWordIndex);
            }
        }
        return false;
    }

    private static void findWord(int i, int j, TrieNode root, boolean[][] visited, char[][] board, List<int[]> list) {
        if (root.isWord) {
            list.add(new int[]{i, j});
            return;
        }
        int rows = board.length, cols = board[0].length;
        if (i < 0 || i >= rows || j < 0 || j >= cols || visited[i][j]) {
            return;
        }
        int pos = board[i][j] -'a';
        if (root.children[pos] == null) return;
        int[][] dirs = {{1,0},{0,1}};
        for (int[] dir : dirs) {
            int nr = i + dir[0], nc = j + dir[1];

        }
    }

    static class TrieNode {
        TrieNode[] children;
        boolean isWord;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
        }
    }

    static class Trie {
        TrieNode root;
        public Trie(){
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode cur = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (cur.children[pos] == null) {
                    cur.children[pos] = new TrieNode();
                }
                cur = cur.children[pos];
            }
            cur.isWord = true;
        }
    }

    public static void main(String[] args) {
//        String s = "hello";
//        List<Character> list = Arrays.asList('h', 'e', 'l', 'l', 'o');
//        System.out.println(searchWord1(s, list));

        char[][] board = {
                {'a','b','c'},
                {'d','e','f'},
                {'g','h','i'}
        };
        String s1 = "abefih";
        for (int[] pos : searchWord2(s1, board)) {
            System.out.println(Arrays.toString(pos));
        }

    }
}
