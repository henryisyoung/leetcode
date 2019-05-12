package pinterest;

public class MaximumProduct4AdjacentElementsInMatrix {
    static final int n = 5;

    // function to find max product
    static int FindMaxProduct(int arr[][], int n) {
        int max = 0, result;
        // iterate the rows.
        for (int i = 0; i < n; i++) {
            // iterate the columns.
            for (int j = 0; j < n; j++) {
                // check the maximum product
                // in horizontal row.
                if ((j - 3) >= 0) {
                    result = arr[i][j] * arr[i][j - 1] * arr[i][j - 2] * arr[i][j - 3];
                    if (max < result) {
                        max = result;
                    }
                }

                // check the maximum product
                // in vertical row.
                if ((i - 3) >= 0) {
                    result = arr[i][j] * arr[i - 1][j] * arr[i - 2][j] * arr[i - 3][j];
                    if (max < result) {
                        max = result;
                    }
                }

                // check the maximum product in
                // diagonal and anti - diagonal
                if ((i - 3) >= 0 && (j - 3) >= 0) {
                    result = arr[i][j] * arr[i - 1][j - 1] * arr[i - 2][j - 2] * arr[i - 3][j - 3];
                    if (max < result) {
                        max = result;
                    }
                }
            }
        }
        return max;
    }
}
