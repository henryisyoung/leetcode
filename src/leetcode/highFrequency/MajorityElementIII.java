package leetcode.highFrequency;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MajorityElementIII {
    public int majorityNumber(List<Integer> nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            if(map.containsKey(i)) {
                map.put(i, map.get(i) + 1);
            } else {
                if(map.size() < k) {
                    map.put(i, 1);
                } else {
                    List<Integer> removeKey = new ArrayList<>();
                    for (Integer key : map.keySet()) {
                        int val = map.get(key);
                        val--;
                        if (val == 0) {
                            removeKey.add(key);
                        }
                    }
                    map.put(i, 1);
                    for (Integer num : removeKey) {
                        map.remove(num);
                    }
                }
            }
        }

        for (int key : map.keySet()) {
            map.put(key, 0);
        }
        int num = 0, count = 0;
        for (int i=0;i < nums.size() ;i++) {
            if (map.containsKey(nums.get(i))) {
                int val = map.get(nums.get(i)) + 1;
                if (val > count) {
                    num = nums.get(i);
                    count = val;
                }
                map.put(nums.get(i), val);
            }
        }
        return num;
    }
    public int majorityNumber2(List<Integer> nums, int k) {
        if (nums == null || nums.size() == 0) {
            return 0;
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            if (map.containsKey(i)) {
                map.put(i, 1 + map.get(i));
            } else {
                if (map.size() < k) {
                    map.put(i, 1);
                } else {
                    List<Integer> removeKey = new ArrayList<>();
                    for (int key : map.keySet()) {
                        int val = map.get(key) - 1;
                        map.put(key, val);
                        if (val == 0) {
                            removeKey.add(key);
                        }
                    }
                    for (int key : removeKey) {
                        map.remove(key);
                    }
                    map.put(i, 1);
                }
            }
        }
        for (int key : map.keySet()) {
            map.put(key, 0);
        }
        int result = 0, max = 0;
        for (int i : nums) {
            int val = map.get(i) + 1;
            map.put(i, val);
            if (val > max) {
                result = i;
            }
        }
        return result;
    }
}
