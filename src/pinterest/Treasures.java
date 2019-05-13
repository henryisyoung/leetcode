package pinterest;

import java.util.*;

public class Treasures {

    public static void main(String[] args) {
        int[][] board = new int[][] {
                {  1,  0,  0, 0, 0 },
                {  0,  1,  1, 0, 0 },
                {  0, -1,  0, 1, 0 },
                { -1,  0,  0, 0, 0 },
                {  0,  1, -1, 0, 0 },
                {  0,  0,  0, 0, 0 }};
        int[] start = new int[] { 5, 0 };
        int[] end = new int[] { 0, 4 };
        Treasures solver = new Treasures();
        List<int[]> result = solver.treasure(board, start[0], start[1], end[0], end[1]);
        System.out.println("size " + result.size());
        for (int[] arr : result) {
            System.out.print(Arrays.toString(arr) + "-->");
        }
    }

    public List<int[]> treasure(int[][] board, int sr, int sc, int er, int ec) {
        List<int[]> result = new ArrayList<>();
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0){
            return result;
        }
        int count = 0, rows = board.length, cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 1) {
                    count++;
                }
            }
        }
        List<int[]> path = new ArrayList<>();
        boolean[][] visited = new boolean[rows][cols];
        dfsSearchAll(result, path, sr, sc, er, ec, board, count, visited);
        return result;
    }

    private void dfsSearchAll(List<int[]> result, List<int[]> path, int sr, int sc, int er, int ec,
                              int[][] board, int count, boolean[][] visited) {
        if (sr == er && sc == ec && count == 0) {
            if (result.size() == 0 || path.size() < result.size()) {
                result.clear();
                result.addAll(path);
                result.add(new int[]{sr, sc});
                return;
            }
        }
        int rows = board.length, cols = board[0].length;
        if (sr >= rows || sr < 0 || sc >= cols || sc < 0 || visited[sr][sc] || board[sr][sc] == -1) {
            return;
        }
        visited[sr][sc] = true;
        path.add(new int[]{sr,sc});
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nr = sr + dir[0], nc = sc + dir[1];
            if (board[sr][sc] == 1) {
                dfsSearchAll(result, path, nr, nc, er, ec, board, count - 1, visited);
            } else {
                dfsSearchAll(result, path, nr, nc, er, ec, board, count, visited);
            }
        }
        visited[sr][sc] = false;
        path.remove(path.size() - 1);
    }
}
