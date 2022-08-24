package airbnb.Phone;

import java.util.HashMap;
import java.util.Map;

public class SubarraysWithKDifferentIntegers {
    public int subarraysWithKDistinct(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        int count = 0;
        int l = 0, r = 0, size = nums.length;

        while (r < size) {
            while (r < size && map.size() < k) {
                map.put(nums[r], map.getOrDefault(nums[r], 0) + 1);
                r++;
            }
            if (map.size() == k) {
                count++;
            }
            int val = map.get(nums[l]) - 1;
            if (val == 0) {
                map.remove(nums[l]);
            } else {
                map.put(nums[l], val);
            }
            l++;
        }
        return count;
    }


}
