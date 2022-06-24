package apple;

public class FirstMissingPositive {
    public static int firstMissingPositive(int[] nums) {
        if(nums == null || nums.length == 0) return 1;
        int n = nums.length;

        for(int i = 0; i < n; i++) {
            while(nums[i] >= 1 && nums[i] <= n && nums[i] != i + 1 && nums[i] != nums[nums[i] - 1]) {
                int tmp = nums[nums[i] - 1];
                nums[nums[i] - 1] = nums[i];
                nums[i] = tmp;
            }
        }

        for(int i = 0; i < n; i++) {
            if(nums[i] != i + 1) return i + 1;
        }
        return n + 1;
    }

    public static void main(String[] args) {
        int[] a1 = {1,2,0};
        System.out.println(firstMissingPositive(a1));
        System.out.println();
        int[] a2 = {3,4,-1,1};
        System.out.println(firstMissingPositive(a2));

    }
}
