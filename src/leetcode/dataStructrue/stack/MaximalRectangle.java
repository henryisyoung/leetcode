package leetcode.dataStructrue.stack;

import java.util.Stack;

public class MaximalRectangle {
    public int maximalRectangle(char[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return 0;
        }
        int max = 0;
        int rows = matrix.length, cols = matrix[0].length;
        int[][] histograms = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0) {
                    histograms[i][j] = matrix[i][j] - '0';
                } else {
                    if (matrix[i][j] == '1') {
                        histograms[i][j] = 1 + histograms[i - 1][j];
                    }
                }

            }
            max = Math.max(max, largestRectangleArea(histograms[i]));
        }
        return max;
    }

    private int largestRectangleArea(int[] heights) {
        if (heights == null || heights.length == 0) {
            return 0;
        }
        int max = 0;
        Stack<Integer> stack = new Stack<>();
        for(int i = 0; i <= heights.length; i++) {
            int curHeight = i == heights.length ? Integer.MIN_VALUE: heights[i];
            while (!stack.isEmpty() && curHeight <= heights[stack.peek()]) {
                int pos = stack.pop();
                int height = heights[pos];
                int width = stack.isEmpty()? i : (i - stack.peek() - 1);
                max = Math.max(max, height * width);
            }
            stack.push(i);
        }

        return max;
    }
}
