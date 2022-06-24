package google.June;

public class BestRoute {
    static int best = Integer.MAX_VALUE;
    public static int bestRoute(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return 0;
        bestRouteHelper(matrix, 0, 0, matrix[0][0]);
        return best;
    }

    private static void bestRouteHelper(int[][] matrix, int i, int j, int maxInPath) {
        int rows = matrix.length, cols = matrix[0].length;
        if (i == rows - 1 && j == cols) {
            best = Math.min(maxInPath, best);
            return;
        }
        if (i >= rows || j >= cols) return;
        maxInPath = Math.max(maxInPath, matrix[i][j]);
        int[][] dirs = {{1, 0}, {0, 1}};
        for (int[] dir : dirs) {
            bestRouteHelper(matrix, i + dir[0], j + dir[1], maxInPath);
        }
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1,2,0,3},
                {4,6,5,1},
                {9,2,5,7},
                {5,4,2,2}
        };
        System.out.println(bestRoute(matrix));
    }
}
