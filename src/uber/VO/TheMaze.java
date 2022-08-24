package uber.VO;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class TheMaze {
    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        int rows = maze.length, cols = maze[0].length;
        if (destination[0] < 0 || destination[0] >= rows || destination[1] < 0 || destination[1] >= cols) {
            return false;
        }
        if (maze[destination[0]][destination[1]] == 1) {
            return false;
        }
        Set<Integer> visited = new HashSet<>();
        int startIndex = start[0] * cols + start[1];
        int endIndex = destination[0] * cols + destination[1];
        if (startIndex == endIndex) {
            return true;
        }
        visited.add(startIndex);
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        while (!queue.isEmpty()) {
            int[] index = queue.poll();
            int r = index[0], c = index[1];
            int curIndex = index[0] * cols + index[1];
            if (curIndex == endIndex) {
                return true;
            }
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];

                if (isValid(nr, nc, maze, dir)) {
                    while (isValid(nr + dir[0], nc + dir[1], maze, dir)) {
                        nr = nr + dir[0];
                        nc = nc + dir[1];
                    }
                    int nextIndex = nr * cols + nc;

                    if (!visited.contains(nextIndex)) {
                        queue.add(new int[]{nr, nc});
                        visited.add(nextIndex);
                    }
                }
            }
        }

        return false;
    }

    private boolean isValid(int r, int c, int[][] maze, int[] dir) {
        int rows = maze.length, cols = maze[0].length;

        return r >= 0 && r < rows && c  >= 0 && c  < cols  && maze[r ][c] == 0;
    }
}
