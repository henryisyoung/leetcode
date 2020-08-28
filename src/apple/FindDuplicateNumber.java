package apple;

import java.util.BitSet;

public class FindDuplicateNumber {
    public static int findDuplicate(int[] nums) {
        int n = nums.length;
        BitSet bitSet = new BitSet(n);
        for (int i : nums) {
            if (bitSet.get(i)) return i;
            bitSet.set(i);
        }
        return 0;
    }

    public static void main(String[] args) {
        int[] nums = {3,1,3,4,2};
        System.out.println(findDuplicate(nums));
    }
}
