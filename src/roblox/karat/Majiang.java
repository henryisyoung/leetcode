package roblox.karat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
// https://productive-horse-bb0.notion.site/Roblox-Karat-2021-5-2022-2-9b07dcbba3634de080c3854c1293d0dc
public class Majiang {
    public static boolean winMajiang(String s) {
        if (s.length() < 2) return false;
        if ((s.length() - 2) % 3 != 0) return false;
        Map<Character, Integer> map = new HashMap<>();
        for (char c : s.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        int countPair = 0;
        for (Character key : map.keySet()) {
            int count = map.get(key);
            count %= 3;
            if (count == 1) {
                return false;
            } else if (count == 2) {
                countPair++;
            }
        }
        return countPair == 1;
    }
    public static boolean completeHandAdvanced(String s) {
        if (s.length() < 2) return false;
        if ((s.length() - 2) % 3 != 0) return false;

        int[] count = new int[10];
        for (char c : s.toCharArray()) {
            count[c - '0']++;
        }
        for (int i = 1; i < 10; i++) {
            if (count[i] >= 2) {
                count[i] -= 2;
                if (dfsCheckAll(count)) {
                    return true;
                }
                count[i] += 2;
            }
        }
        return false;
    }

    private static boolean dfsCheckAll(int[] count) {
        for (int i = 1; i <= 7; i++) {
            if (count[i] > 0 && count[i + 1] > 0 && count[i + 2] > 0) {
                count[i]--;
                count[i + 1]--;
                count[i + 2]--;
                if (dfsCheckAll(count)) {
                    return true;
                }
                count[i]++;
                count[i + 1]++;
                count[i + 2]++;
            }
        }
        for (int i : count) {
            if (i % 3 != 0) {
                return false;
            }
        }
        return true;
    }

    public static int floodFill(int[][] board, int[] pos) {
        if (board == null || board[0] == null || board.length == 0 || board[0].length == 0) {
            return 0;
        }
        int rows = board.length, cols = board[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(pos);
        boolean[][] visited = new boolean[rows][cols];
        visited[pos[0]][pos[1]] = true;
        int count  = 1;
        int val = board[pos[0]][pos[1]];

        int[][] dirs = {{0,1},{1,0},{0,-1}, {-1,0}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] dir : dirs) {
                int nr = cur[0] + dir[0], nc = cur[1] + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && board[nr][nc] == val) {
                    count++;
                    queue.add(new int[]{nr, nc});
                    visited[nr][nc] = true;
                }
            }
        }

        return count;
    }

    public static int floodFill2(int[][] board, int[] pos) {
        if (board == null || board[0] == null || board.length == 0 || board[0].length == 0) {
            return 0;
        }
        int val = board[pos[0]][pos[1]];
        return dfsFill(val, pos[0], pos[1], board);
    }

    private static int dfsFill(int val, int r, int c, int[][] board) {
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != val) {
            return 0;
        }
        int count = 1;
        board[r][c] = val - 1;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for (int[] dir : dirs) {
            count += dfsFill(val, r + dir[0], c + dir[1], board);
        }

        return count;
    }

    public static void main(String[] args) {
//        String[] strs = {"11122" , "111", "1112233", "00000111"};
//        for (String str : strs) {
//            System.out.println(winMajiang(str));
//        }

        int[][] board = {
                {5,5,5,1,1},
                {1,1,5,2,1},
                {1,5,5,1,1},
        };
        int[] pos = {0,3};
        System.out.println(floodFill(board, pos));
        System.out.println(floodFill2(board, pos));
    }
}
