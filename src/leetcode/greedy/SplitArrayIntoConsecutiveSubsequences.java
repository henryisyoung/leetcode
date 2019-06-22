package leetcode.greedy;

import java.util.*;

public class SplitArrayIntoConsecutiveSubsequences {
    public boolean isPossible(int[] nums) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        Map<Integer, Integer> needMap = new HashMap<>();

        for (int i : nums) {
            freqMap.put(i, freqMap.getOrDefault(i , 0) + 1);
        }
        for (int i : nums) {
            if (freqMap.get(i) == 0) {
                continue;
            } else if (needMap.get(i) != null && needMap.get(i) > 0) {
                needMap.put(i, needMap.get(i) - 1);
                needMap.put(i + 1, needMap.getOrDefault(i + 1, 0) + 1);
            } else if (freqMap.get(i + 1) != null && freqMap.get(i + 1) > 0 &&
                    freqMap.get(i + 2) != null && freqMap.get(i + 2) > 0){
                freqMap.put(i + 1, freqMap.get(i + 1) - 1);
                freqMap.put(i + 2, freqMap.get(i + 2) - 1);
                needMap.put(i + 3, needMap.getOrDefault(i + 3, 0) + 1);
            } else {
                return false;
            }
            freqMap.put(i, freqMap.get(i) - 1);
        }
        return true;
    }
}
