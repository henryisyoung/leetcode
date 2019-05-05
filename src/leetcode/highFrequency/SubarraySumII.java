package leetcode.highFrequency;

public class SubarraySumII {
//    其实需要三根指针, 因为需要额外记录一下从哪个位置开始的加和已经 >= start 了.
//    对于每一个左端点 left, 求出对应的两个右端点 right_start, right_end. 前者表示最左边的使得 [left, right_start] 子数组的和不小于 start 的点,
//    而后者表示最右边的使得 [left, right_end] 子数组的和不大于 end 的点.
//    right_end - right_start + 1 就是以 left 为左端点的合法子数组的数量.
//    从左到右枚举 left, 而 right_start, right_end 随着left的增长也是只增不减的, 所以时间复杂度是 O(N)
    public int subarraySumII(int[] A, int start, int end) {
        int count = 0 ;
        if (A == null || A.length == 0){
            return count;
        }
        int n = A.length;
        int[] sum = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            sum[i] = sum[i - 1] + A[i - 1];
        }
        int left = 0, right = 0;
        for (int i = 1; i <= n; i++) {
            while (left < i && sum[i] - sum[left] > end) {
                left++;
            }
            while (right < i && sum[i] - sum[right] >= start) {
                right++;
            }
            count += right - left;
        }
        return count;
    }
}
