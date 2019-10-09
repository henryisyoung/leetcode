package lyft;

import java.util.*;

public class FindAllDuplicatesInAnArray {
    public List<Integer> findDuplicates(int[] nums) {
        List<Integer> result = new ArrayList<>();
        int n = nums.length;
        BitSet bitSet = new BitSet(n + 1);
        for (int i : nums) {
            if (bitSet.get(i)) {
                result.add(i);
            }
            bitSet.set(i);
        }
        return result;
    }

    public List<Integer> findDuplicates2(int[] nums) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;
            if (nums[index] < 0) {
                result.add(Math.abs(nums[i]));
            } else {
                nums[index] *= -1;
            }
        }
        return result;
    }
}