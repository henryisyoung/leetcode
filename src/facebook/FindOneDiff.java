package facebook;

public class FindOneDiff {
//    给一个递增array，所有difference都是1，可能有一个位置difference不是1，找出所在index，如果没有return -1
//    例如[1,2,3,4,6,7] -> 4
//    [1,2,3,4,5,6] -> -1
    public static int findIndex(int[] nums) {
        if (nums == null || nums.length < 2) return -1;
        int n = nums.length;
        int diff = 0;
        if (nums[1] - nums[0] == 1) {
            diff = nums[0] - 0;
        } else {
            if (n > 2) {
                diff = nums[2] - nums[1];
            } else {
                return -1;
            }
        }
        int l = 0, r = n - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == mid + diff) {
                l = mid;
            } else {
                r = mid;
            }
        }
        if (nums[l] != l + diff) return l;
        if (nums[r] != r + diff) return r;
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {7,8,19};
        System.out.println(findIndex(arr));
    }
}
