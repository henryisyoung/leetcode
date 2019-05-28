package google;

import java.util.ArrayList;
import java.util.List;

public class NumberOfCornerRectangles {
    public int countCornerRectangles(int[][] grid) {
        if (grid == null || grid.length < 2 || grid[0] == null || grid[0].length < 2) {
            return 0;
        }
        int rows = grid.length, cols = grid[0].length;
        int result = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = i + 1; j < rows; j++) {
                int count = 0;
                for (int k = 0; k < cols; k++) {
                    if (grid[i][k] == 1 && grid[j][k] == 1) {
                        count++;
                    }
                }
                result += count * (count - 1) / 2;
            }
        }

        return result;
    }

    public int countCornerRectangles1(int[][] grid) {
        int m = grid.length, n = grid[0].length, res = 0;
        for (int i = 0; i < m - 1; i++) {
            List<Integer> ones = new ArrayList<>();
            for (int k = 0; k < n; k++) {
                if (grid[i][k] == 1) {
                    ones.add(k);
                }
            }
            for (int j = i + 1; j < m; j++) {
                int cnt = 0;
                for (int l = 0; l < ones.size(); l++) {
                    if (grid[j][ones.get(l)] == 1) {
                        cnt++;
                    }
                }
                res += cnt * (cnt - 1) / 2;
            }
        }
        return res;
    }
}