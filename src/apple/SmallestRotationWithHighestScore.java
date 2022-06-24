package apple;

public class SmallestRotationWithHighestScore {
    public static int bestRotation(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int max = 0, result = 0, k = 0, n = nums.length;
        while (k < n) {
            int[] arr = nums;
            if (k != 0) {
                 rotateArray(k, nums);
            }
            int val = calScore(arr);
            if (val > max) {
                max = val;
                result = k;
            }
            k++;
        }
        return result;
    }

    private static int calScore(int[] arr) {
        int val = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] <= i) {
                val++;
            }
        }
        return val;
    }

    private static int[] rotateArray(int k, int[] nums) {
        int n = nums.length;
        int[] array = nums.clone();
        rotateArrayHelper(0, k - 1, array);
        rotateArrayHelper(k, n - 1, array);
        rotateArrayHelper(0, n - 1, array);
        return array;
    }

    private static void rotateArrayHelper(int l, int r, int[] nums) {
        while (l < r) {
            int tmp = nums[l];
            nums[l] = nums[r];
            nums[r] = tmp;
            l++;
            r--;
        }
    }

    public static void main(String[] args) {
        System.out.println(bestRotation(new int[]{2,3,1,4,0}));
    }
}
