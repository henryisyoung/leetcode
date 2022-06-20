package datastructure.stack;

import java.util.Stack;

public class MaximalRectangle {
    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int[] table = new int[cols];

        int max = 0;
        for (int i = 0; i < cols; i++) {
            table[i] = matrix[0][i] - '0';
            max = Math.max(max, table[i]);
        }

        for (int i= 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '0') {
                    table[j] = 0;
                } else {
                    table[j]++;
                }
            }
            max = Math.max(max, findLargest(table));
        }
        return max;
    }

    private int findLargest(int[] heights) {
        int n = heights.length;
        Stack<Integer> stack = new Stack<>();
        int max = 0;
        for (int i = 0; i <= n; i++) {
            int curH = i == n ? -1 : heights[i];
            while (!stack.isEmpty() && curH < heights[stack.peek()]) {
                int pos = stack.pop();
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                int h = heights[pos];
                max = Math.max(max, h * width);
            }
            stack.push(i);
        }
        return max;
    }
}
