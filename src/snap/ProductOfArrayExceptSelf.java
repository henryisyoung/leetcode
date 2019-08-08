package snap;

public class ProductOfArrayExceptSelf {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] left = new int[n], right = new int[n];
        left[0] = 1;
        right[n - 1] = 1;
        for (int i = 1; i < n; i++) {
            left[i] = left[i - 1] * nums[i - 1];
        }
        for (int i = n - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i + 1];
        }
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = left[i] * right[i];
        }
        return result;
    }

    public int[] productExceptSelf2(int[] nums) {
        int n = nums.length;
        int[] rlt = new int[n];
        for(int i = 0; i < n; i++){
            if(i == 0){
                rlt[i] = 1;
            }else{
                rlt[i] = rlt[i - 1] * nums[i - 1];
            }
        }
        int prev = nums[n - 1];
        for(int i = n - 1; i >= 0; i--){
            if(i == n - 1){
                nums[i] = 1;
            }else{
                int local = nums[i];
                nums[i] = prev * nums[i + 1];
                prev = local;
            }
        }
        for(int i = 0; i < n; i++){
            rlt[i] = rlt[i] * nums[i];
        }
        return rlt;
    }
}