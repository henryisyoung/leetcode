package leetcode.union;

import java.util.ArrayList;
import java.util.List;

public class NumberOfIslands2 {
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        if (positions == null || positions.length == 0 ||
                positions[0] == null || positions[0].length == 0 || m * n == 0) {
            return result;
        }
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        Union union = new Union(m * n);
        int count = 0;
        int[][] islands = new int[m][n];
        for (int[] pos : positions) {
            int row = pos[0], col = pos[1];
            islands[row][col] = 1;
            count++;
            for (int[] dir : dirs) {
                int nRow = row + dir[0];
                int nCol = col + dir[1];
                if (nRow >= 0 && nRow < m && nCol >= 0 && nCol < n && islands[nRow][nCol] == 1) {
                    int oldFather = union.find(row * n + col);
                    int newFather = union.find(nRow * n + nCol);
                    if (oldFather != newFather) {
                        union.union(oldFather, newFather);
                        count--;
                    }
                }
            }
            result.add(count);
        }

        return result;
    }
}
