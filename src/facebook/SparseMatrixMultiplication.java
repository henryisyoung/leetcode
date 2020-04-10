package facebook;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SparseMatrixMultiplication {
    public static int[][] multiply(int[][] A, int[][] B) {
        int rows = A.length, cols = B[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = helper(A[i], B, j);
            }
        }

        return result;
    }

    private static int helper(int[] a, int[][] b, int j) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i][j];
        }
        return sum;
    }

    public static int[][] multiply2(int[][] A, int[][] B) {
        int rows = A.length, cols = B[0].length;
        int[][] result = new int[rows][cols];
        Map<Integer, Map<Integer, Integer>> map = new HashMap<>();
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (A[i][j] == 0) continue;
                map.putIfAbsent(i, new HashMap<>());
                map.get(i).put(j, A[i][j]);
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = helper2(map,i, B, j);
            }
        }

        return result;
    }

    private static int helper2(Map<Integer, Map<Integer, Integer>> map, int i, int[][] b, int j) {
        if (map.containsKey(i)) {
            int sum = 0;
            Map<Integer, Integer> rows = map.get(i);
            for (int c : rows.keySet()) {
                sum += rows.get(c) * b[c][j];
            }
            return sum;
        }
        return 0;
    }

    public static int[][] multiply3(int[][] A, int[][] B) {
        int A_Rows = A.length, A_Columns = A[0].length, B_Columns = B[0].length;
        int[][] C = new int[A_Rows][B_Columns];
        HashMap<Integer, HashMap<Integer, Integer>> map1 = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> map2 = new HashMap<>();
        for (int i = 0; i < A_Rows; i++)
            for (int j = 0; j < A_Columns; j++) {
                if (A[i][j] != 0) {
                    if (map1.get(i) == null) {
                        HashMap<Integer, Integer> map = new HashMap<>();
                        map.put(j, A[i][j]);
                        map1.put(i, map);
                    } else {
                        map1.get(i).put(j, A[i][j]);
                    }
                }
            }
        for (int i = 0; i < A_Columns; i++)
            for (int j = 0; j < B_Columns; j++) {
                if (B[i][j] != 0) {
                    if (map2.get(i) == null) {
                        HashMap<Integer, Integer> map = new HashMap<>();
                        map.put(j, B[i][j]);
                        map2.put(i, map);
                    } else {
                        map2.get(i).put(j, B[i][j]);
                    }
                }
            }
        for (int i : map1.keySet())
            for (int j : map1.get(i).keySet()) {
                if (map2.get(j) == null)
                    continue;
                for (int k : map2.get(j).keySet())
                    C[i][k] += A[i][j] * B[j][k];
            }
        return C;
    }

    public static void main(String[] args) {
        int[][] A = {
                { 1, 0, 0},
                {-1, 0, 3}
        },

        B = {
                { 7, 0, 0 },
                { 0, 0, 0 },
                { 0, 0, 1 }
        };

        System.out.println(Arrays.deepToString(multiply3(A, B)));
    }
}
