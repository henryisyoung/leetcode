package facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagonalTraverse {
    public static int[] findDiagonalOrder(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) return new int[]{};
        int m = matrix.length, n = matrix[0].length;
        int[] result = new int[m * n];
        int r = 0, c = 0;

        int d = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = matrix[r][c];
            if (d % 2 == 0) {
                if (c == n - 1) {
                    r++;
                    d++;
                } else if (r == 0) {
                    c++;
                    d++;
                }  else {
                    r--;
                    c++;
                }
            } else {
                if (r == m - 1) {
                    c++;
                    d++;
                } else if (c == 0) {
                    r++;
                    d++;
                } else {
                    r++;
                    c--;
                }
            }
        }

        return result;
    }

    public static List<List<Integer>>  findDiagonalOrder2(int[][] matrix) {
        List<List<Integer>> result = new ArrayList<>();
        if(matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) return result;
        int rows = matrix.length, cols = matrix[0].length;
        List<Integer> list = new ArrayList<>();
        int d = 0;
        int r = 0, c = 0;
        while(r * cols + c  < rows * cols) {
            if(d % 2 == 0) {
                list.add(0,matrix[r][c]);
                if(c == cols - 1) {
                    r++;
                    d++;
                    result.add(new ArrayList<>(list));
                    list = new ArrayList<>();
                } else if (r == 0) {
                    c++;
                    d++;
                    result.add(new ArrayList<>(list));
                    list = new ArrayList<>();
                } else {
                    r--;
                    c++;
                }
            } else {
                list.add(matrix[r][c]);
                if(r == rows - 1) {
                    c++;
                    d++;
                    result.add(new ArrayList<>(list));
                    list = new ArrayList<>();
                } else if(c == 0) {
                    r++;
                    d++;
                    result.add(new ArrayList<>(list));
                    list = new ArrayList<>();
                } else {
                    r++;
                    c--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[][] matrix = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };
//        int[] result= findDiagonalOrder(matrix);
//        System.out.println(Arrays.toString(result));
        System.out.println(findDiagonalOrder2(matrix));
    }
}
