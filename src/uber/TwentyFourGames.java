package uber;

public class TwentyFourGames {
    public boolean judgePoint24(int[] nums) {
        double[] nums2 = new double[4];
        for (int i = 0; i < 4; i++) {
            nums2[i] = (double) nums[i];
        }
        return dfsSearchAll(nums2, 4);
    }

    private boolean dfsSearchAll(double[] nums2, int n) {
        if (n == 1) {
            return Math.abs(nums2[0] - 24) < 1e-10;
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double a = nums2[i], b = nums2[j];
                nums2[j] = nums2[n - 1];
                nums2[i] = a + b;
                if (dfsSearchAll(nums2, n - 1)) {
                    return true;
                }
                nums2[i] = a - b;
                if (dfsSearchAll(nums2, n - 1)) {
                    return true;
                }
                nums2[i] = b - a;
                if (dfsSearchAll(nums2, n - 1)) {
                    return true;
                }
                nums2[i] = a * b;
                if (dfsSearchAll(nums2, n - 1)) {
                    return true;
                }
                if (b != 0) {
                    nums2[i] = a / b;
                    if (dfsSearchAll(nums2, n - 1)) {
                        return true;
                    }
                }
                if (a != 0) {
                    nums2[i] = b / a;
                    if (dfsSearchAll(nums2, n - 1)) {
                        return true;
                    }
                }
                nums2[i] = a;
                nums2[j] = b;
            }
        }
        return false;
    }

    private static double EPS = 1e-10;

    public static void main(String[] args) {
        TwentyFourGames solver = new TwentyFourGames();
        int[] nums = {5,4,5,7};
        System.out.println(solver.judgePoint24(nums));
    }
}
