package snap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SubarraySumEqualsK {
    public static int subarraySum(int[] nums, int k) {
        int count = 0, sum = 0;
        HashMap< Integer, Integer > map = new HashMap < > ();
        map.put(0, 1);
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (map.containsKey(sum - k)) count += map.get(sum - k);
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return count;
    }

    public static boolean subarraySum2(int[] nums, int k) {
        Set<Integer> set = new HashSet<>();
        set.add(0);
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (set.contains(sum - k)) return true;
            set.add(sum);
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {1,1,1};
        System.out.println(subarraySum(nums, 2));
        System.out.println(subarraySum2(new int[]{}, 3));
    }
}
