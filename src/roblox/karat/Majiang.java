package roblox.karat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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
        int[] pos = {1,2};
        System.out.println(floodFill(board, pos));
    }
}
