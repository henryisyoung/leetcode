package LinkedIn.phone;

public class FindCelebrity {
    public int findCelebrity(int[][] matrix) {
        int n = matrix.length;
        int l = 0, r = n - 1;
        while (l < r) {
            if (matrix[l][r] == 1) {
                l++;
            } else {
                r--;
            }
        }

        for (int i = 0; i < n; i++) {
            if (i != r) {
                if (matrix[r][i] == 1 || matrix[i][r] != 1) {
                    return -1;
                }
            }
        }
        return r;
    }
}
