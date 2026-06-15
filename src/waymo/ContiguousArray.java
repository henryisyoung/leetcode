package waymo;

/*
LeetCode 525 - Contiguous Array

Given a binary array nums, return the maximum length of a contiguous subarray
with an equal number of 0s and 1s.

Key idea
  Convert 0 -> -1 and 1 -> +1. Then a subarray has equal 0s and 1s exactly when
  its transformed sum is 0.

  This becomes a prefix-sum problem:
    prefix[j] - prefix[i] = 0  =>  prefix[j] == prefix[i]

  So for each running balance, keep the earliest index where that balance first
  appeared. If the same balance appears again at index i, the subarray between
  those two positions has equal 0s and 1s.

Why map.put(0, -1)?
  Balance 0 exists before the array starts. This lets us count subarrays that
  start at index 0.

Example
  nums = [0, 1, 0]
  balance sequence:
    before array: 0 at index -1
    i=0: -1
    i=1:  0  -> seen at -1, length = 2
    i=2: -1  -> seen at 0,  length = 2
  answer = 2

Complexity
  O(n) time, O(n) space.
*/

import java.util.HashMap;
import java.util.Map;

public class ContiguousArray {

    public int findMaxLength(int[] nums) {
        Map<Integer, Integer> firstIndex = new HashMap<>();
        firstIndex.put(0, -1);

        int balance = 0;
        int best = 0;

        for (int i = 0; i < nums.length; i++) {
            balance += nums[i] == 1 ? 1 : -1;

            if (firstIndex.containsKey(balance)) {
                best = Math.max(best, i - firstIndex.get(balance));
            } else {
                firstIndex.put(balance, i);
            }
        }

        return best;
    }

    public static void main(String[] args) {
        ContiguousArray solution = new ContiguousArray();
        System.out.println(solution.findMaxLength(new int[] {0, 1}));       // 2
        System.out.println(solution.findMaxLength(new int[] {0, 1, 0}));    // 2
        System.out.println(solution.findMaxLength(new int[] {0, 0, 1, 1})); // 4
    }
}
