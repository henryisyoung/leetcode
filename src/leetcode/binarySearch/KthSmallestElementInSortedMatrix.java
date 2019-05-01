package leetcode.binarySearch;

public class KthSmallestElementInSortedMatrix {
    public int kthSmallest(int[][] matrix, int k) {
        int n = matrix.length;
        int L = matrix[0][0], R = matrix[n - 1][n - 1];
        while (L < R) {
            int mid = L + ((R - L) >> 1);
            int temp = 0;
            for (int i = 0; i < n; i++) temp += binary_search(matrix[i], n, mid);
            System.out.println( "mid " + mid + " temp " + temp);
            if (temp < k) L = mid + 1;
            else R = mid;
        }
        return L;
    }

    private int binary_search(int[] row,int R,int x){
        int L = 0;
        while (L < R){
            int mid = (L + R) >> 1;
            if(row[mid] <= x) L = mid + 1;
            else R = mid;
        }
        return L;
    }

    public static void main(String[] args) {
        int[][] matrix = {{ 1,  5,  9}, {10, 11, 13}, {12, 13, 15}};
        KthSmallestElementInSortedMatrix solver = new KthSmallestElementInSortedMatrix();
        int result = solver.kthSmallest(matrix, 8);
        System.out.println(result);
    }
}
