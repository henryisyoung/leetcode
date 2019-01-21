package leetcode;

public class Question80_Sorted_Array {
    public int removeDuplicates(int[] nums) {
        if(nums == null) return 0;
        int lens = nums.length;
        if(lens < 3) return lens;
        int p = 1, c =2;
        while(c < lens){
            if(nums[p] == nums[c] && nums[p - 1] == nums[c]){
                c++;
            }else {
                p++;
                nums[p] = nums[c];
                c++;
            }
        }
        return p + 1;
    }
}
