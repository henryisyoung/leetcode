package waymo;

/*
LeetCode 974 - Subarray Sums Divisible by K

Given an integer array nums and an integer k, return the number of non-empty
subarrays whose sum is divisible by k.

Key idea
  Use prefix sums modulo k.

  A subarray nums[i+1..j] is divisible by k when:
    (prefix[j] - prefix[i]) % k == 0

  That means:
    prefix[j] % k == prefix[i] % k

  So when the current prefix remainder is r, every previous prefix with the same
  remainder forms a valid subarray ending here.

Why count frequencies, not first index?
  Unlike Contiguous Array (max length), this asks for the NUMBER of subarrays.
  Every previous equal remainder is one valid starting boundary, so we store how
  many times each remainder has appeared.

Why freq[0] = 1?
  The empty prefix has remainder 0 once. This counts subarrays starting at index
  0 whose sum is already divisible by k.

Negative numbers
  Java's % can be negative, so normalize:
    rem = ((sum % k) + k) % k

Complexity
  O(n) time, O(k) space.
*/

import java.util.HashMap;
import java.util.Map;

public class SubarraySumsDivisibleByK {

    public int subarraysDivByK(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        freq.put(0, 1);

        int count = 0;
        int sum = 0;

        for (int num : nums) {
            sum += num;
            int rem = ((sum % k) + k) % k;

            count += freq.getOrDefault(rem, 0);
            freq.put(rem, freq.getOrDefault(rem, 0) + 1);
        }

        return count;
    }

    public static void main(String[] args) {
        SubarraySumsDivisibleByK solution = new SubarraySumsDivisibleByK();
        System.out.println(solution.subarraysDivByK(new int[] {4, 5, 0, -2, -3, 1}, 5)); // 7
        System.out.println(solution.subarraysDivByK(new int[] {5}, 9));                  // 0
        System.out.println(solution.subarraysDivByK(new int[] {-1, 2, 9}, 2));            // 2
    }
}
