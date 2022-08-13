package uber.phone.leetcode;

import java.util.ArrayList;
import java.util.List;

public class SlideWindowProduct {
    public static List<Long> slideWindowProduct(int[] nums, int k) {
        int countZero = 0;
        List<Long> result = new ArrayList<>();
        long product = 1;
        for (int i = 0; i < k; i++) {
            if (nums[i] == 0) {
                countZero++;
            } else {
                product *= nums[i];
            }
        }
        result.add(countZero > 0 ? 0 : product);
        int n = nums.length;
        for (int i = k; i < n; i++) {
            int add = nums[i];
            int remove = nums[i - k];
            if (remove == 0) {
                countZero--;
            } else {
                product /= remove;
            }
            if (add == 0) {
                countZero++;
            } else {
                product *= add;
            }
            result.add(countZero > 0 ? 0 : product);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1,0,3,5,6,3,3,0,4,1,2,1};
        int k = 4;
        System.out.println(slideWindowProduct(nums, k));
    }
}
