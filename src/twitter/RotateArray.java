package twitter;

public class RotateArray {
    public void rotate(int[] nums, int k) {
        if(nums == null || nums.length < 2) return;
        int lens = nums.length;
        k %= lens;
        if(k == 0) return;
        int n = lens - k;
        rotate_from_to(nums, 0, n - 1);
        rotate_from_to(nums, n, lens - 1);
        rotate_from_to(nums,0, lens - 1);
    }

    private void rotate_from_to(int[] nums, int i, int j) {
        while(i < j){
            int tmp = nums[j];
            nums[j] = nums[i];
            nums[i] = tmp;
            i++;
            j--;
        }
    }
}
