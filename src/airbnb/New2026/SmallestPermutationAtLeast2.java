package airbnb.New2026;
/*
Smallest Permutation >= Lower Bound.

Given a non-negative integer `n` and a lower bound `lb`, return the
smallest integer that can be obtained by permuting the digits of `n`
and that is greater than or equal to `lb`.  Leading zeros in the
permutation are allowed and simply collapse the value (e.g. "0349"
is 349).

Examples
  n = 4139, lb = 200    -> 1349       (smallest perm; all perms are >= 200)
  n = 4039, lb = 400    -> 439        ("0439" = 439 >= 400)
  n = 4039, lb = 9999   -> -1         (max perm is 9430)
  n = 9,    lb = 1      -> 9
  n = 0,    lb = 0      -> 0
  n = 4039, lb = -5     -> 349        (any perm >= -5; pick smallest)

Output convention
  -1 if no permutation of n's digits is >= lb.
  Otherwise the permuted value, interpreted as a base-10 integer
  (leading zeros do not count as significant).

Constraints
  0 <= n  <= Long.MAX_VALUE (up to ~19 digits is fine)
  lb is any long (negative allowed; falls through to "smallest perm")
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
Algorithm: digit DP with a "tight" flag over the digits of lb.

  Let d  = number of digits in n (count[0..9] is its digit multiset).
  Let lb_digits = decimal digits of lb (without leading zeros).

  Three cases on lengths:

    1. d < |lb_digits|
       Any d-digit permutation has at most 10^d - 1 < lb.  Impossible.

    2. d == |lb_digits|
       Compare the d-char permutation string to lb_digits[] directly
       (same length -> lex compare matches numeric compare).

    3. d >  |lb_digits|
       Permutation length is d, but if it starts with leading zeros
       its NUMERIC value can be < 10^d.  Trick: pad lb to width d
       with leading zeros and compare the d-char strings lex.  Same
       length => lex == numeric.  e.g. d=4, lb=200 -> "0200", and
       any d-perm >= "0200" lex is also >= 200 numeric (and vice versa).

  With lb padded to width d, the problem reduces to "smallest d-char
  permutation of count[] that is >= lb_d (lex)".

  Recursion build(pos, tight):
    if pos == d: success.
    for dgt = (tight ? lb_d[pos] : 0) .. 9:
        if count[dgt] == 0: skip.
        take dgt; newTight = tight && dgt == lb_d[pos]
        recurse; if success return.
        untake.
    return failure.

  When `tight == false`, the smallest available digit always works
  (just append remaining digits ascending), so the loop is dominated
  by the tight-chain decisions.

  Complexity
    d = number of digits in n.  Depth is d, branching is at most 10
    per level, but the "loose" subtree is one greedy pick, so total
    work is O(d * 10) digit decisions.  Plus O(d) for parsing and
    O(d) for building the result string.
*/
public class SmallestPermutationAtLeast2 {

    public int smallestAtLeast(int n, int target) {
        if(n < 0) return -1;


        int smallestN = smallest(n);
        if(target <= smallestN) return smallestN;
        int[] counts = countDigits(n);
        int len = Integer.toString(n).length();
        if(len < Integer.toString(target).length()) {
            return -1;
        }
        String padtarget = padTarget(target, len);
        char[] buffer = new char[len];

        if(findNumber(buffer, padtarget, counts, 0, true)) {
            return Integer.parseInt(new String(buffer));
        }

        return -1;
    }

    private boolean findNumber(char[] buffer, String padtarget, int[] counts, int index, boolean tight) {
        if(index == padtarget.length()) {
            return true;
        }

        int min = tight ? (int) (padtarget.charAt(index) - '0') : 0;

        for(int i = min; i <= 9; i++) {
            if(counts[i] == 0) continue;
            counts[i]--;
            boolean newTight = tight && i == (int) (padtarget.charAt(index) - '0');
            if(findNumber(buffer, padtarget, counts, index + 1, newTight)) {
                return true;
            }
            counts[i]++;
        }

        return false;
    }

    private String padTarget(int target, int len) {
        StringBuilder sb = new StringBuilder();
        String str = Integer.toString(target);
        while(len - str.length() > 0) {
            sb.append("0");
            len--;
        }
        sb.append(str);
        return sb.toString();
    }

    private int smallest(int n) {
        char[] arr = Integer.toString(n).toCharArray();
        Arrays.sort(arr);

        return Integer.parseInt(new String(arr));
    }

    private int[] countDigits(int n) {
        int[] counts = new int[10];
        if(n == 0) {
            counts[0]++;
            return counts;
        }

        while(n > 0) {
            int index = n % 10;
            counts[index]++;
            n /= 10;
        }
        return counts;
    }
}
