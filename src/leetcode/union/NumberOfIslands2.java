package leetcode.union;

import java.util.ArrayList;
import java.util.List;

public class NumberOfIslands2 {
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        if (positions == null || positions.length == 0 || m <= 0 || n <= 0) {
            return result;
        }
        int[][] islands = new int[m][n];
        Union union = new Union(m * n);
        int count = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] pos : positions) {
            count++;
            int r = pos[0], c = pos[1];
            islands[r][c] = 1;
            for (int[] dir : dirs) {
                int nRow = r + dir[0], nCol = c + dir[1];
                if (nRow >= 0 && nCol >= 0 && nRow < m && nCol < n && islands[nRow][nCol] == 1) {
                    int curFather = union.find(r * n + c);
                    int oldFather = union.find(nRow * n + nCol);
                    if (curFather != oldFather) {
                        count--;
                        union.union(r * n + c, nRow * n + nCol);
                    }
                }
            }
            result.add(count);
        }
        return result;
    }
}
