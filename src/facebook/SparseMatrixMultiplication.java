package facebook;

import java.util.*;

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
        int Arows = A.length, Acols = A[0].length, Bcols = B[0].length;
        Map<Integer, Map<Integer, Integer>> mapA = new HashMap<>();

        for (int i = 0; i < Arows; i++) {
            for (int j = 0; j < Acols; j++) {
                if (A[i][j] == 0) continue;
                mapA.putIfAbsent(i, new HashMap<>());
                mapA.get(i).put(j, A[i][j]);
            }
        }
        int[][] result = new int[Arows][Bcols];

        for (int ar : mapA.keySet()) {
            for(int j = 0; j < Bcols; j++) {
                for (int c : mapA.get(ar).keySet()) {
                    result[ar][j] += mapA.get(ar).get(c) * B[c][j];
                }
            }
        }
        return result;
    }

    public static List<int[]> multiply3(List<int[]> A, List<int[]> B) {
        // int[] arr ; arr[0] is pos in the vector and arr[1] is the val
        List<int[]> result = new ArrayList<>();
        Collections.sort(A, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        Collections.sort(B, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        int i = 0, j = 0;
        while (i < A.size() && j < B.size()) {
            int[] a = A.get(i), b = B.get(j);
            if (a[0] == b[0]) {
                result.add(new int[]{a[0], a[1] * b[1]});
            } else if (a[0] > b[0]) {
                j++;
            } else {
                i++;
            }
        }
        return result;
    }

    public static int multiply4(List<int[]> A, List<int[]> B) {
        // int[] arr ; arr[0] is pos in the vector and arr[1] is the val
        int result = 0;
        Collections.sort(A, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        Collections.sort(B, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        List<int[]> s = A.size() < B.size() ? A : B;
        List<int[]> l = A.size() > B.size() ? A : B;
        int left = 0;
        for (int i = 0; i < s.size(); i++) {
            int sPos = s.get(i)[0], sVal = s.get(i)[1];
            int j = binarySearchHelper(left, l.size() - 1, sPos, l);
            int lPos = B.get(j)[0];

            if (sPos == lPos) {
                result += sVal * l.get(j)[1];
            }
            left = j;
        }
        return result;
    }

    private static int binarySearchHelper(int left, int right, int sPos, List<int[]> l) {
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if(l.get(mid)[0] == sPos) {
                return mid;
            } else if (l.get(mid)[0] > sPos) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (l.get(left)[0] == sPos || l.get(left)[0] > sPos) return left;
        return right;
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
        List<int[]> a = Arrays.asList(new int[]{1,4}, new int[]{22,14});
        List<int[]> b = Arrays.asList(new int[]{1,2}, new int[]{2,14}, new int[]{12,124}, new int[]{22,4}, new int[]{32,14});
        System.out.println(multiply4(a, b));
    }
}
