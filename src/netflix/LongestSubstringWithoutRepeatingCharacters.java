package netflix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
LC 3. Longest substring with all distinct characters.

Sliding window with last-seen index — single pass, no inner loop.

Invariant: s[l..r] holds only distinct chars. `lastIndex[c]` is the
most recent index of c we've ever seen (may be outside the window).
When we land on a duplicate INSIDE the window (prev >= l), we jump
l past it in O(1) — no per-char shrink, no stale-entry eviction.

Followup (LC 340): "at most K distinct chars" → keep a count map,
shrink while map.size() > k. Same window scaffold.

If the alphabet is bounded (ASCII), replace the HashMap with
`int[128]` filled with -1 — same logic, no hashing or boxing.

The template (one more time, for reference)

for r in 0..n:
    add s[r] to window
    while (window invalid):
        remove s[l]; l++
    record best with current window
*/
public class LongestSubstringWithoutRepeatingCharacters {

    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int n = s.length();

        Map<Character, Integer> map = new HashMap<>();
        int l = 0, r = 0, max = 0;
        while (l < n){
            while (r < n && !map.containsKey(s.charAt(r))) {
                char right = s.charAt(r);

                map.put(right, 1);
                r++;
                max = Math.max(max, r - l);
            }
            char left = s.charAt(l);
            if (map.get(left) == 1) {
                map.remove(left);
            } else {
                map.put(left,map.get(left) - 1);
            }
            l++;
        }

        return max;
    }

    public int lengthOfLongestSubstring2(String s) {
        Map<Character, Integer> lastIndex = new HashMap<>();
        int best = 0, l = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            Integer prev = lastIndex.get(c);
            if (prev != null && prev >= l) l = prev + 1;
            lastIndex.put(c, r);
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    /*
    v1.5 — Set + shrink loop. Same invariant (s[l..r] distinct), but
    instead of jumping l in O(1) we shrink it one step at a time until
    the duplicate is gone. Each pointer still moves forward only, so
    total work is O(n) amortized. Cleaner than v1, less clever than v2 —
    the version to write if the last-seen-index trick won't come.
    */
    public int lengthOfLongestSubstring15(String s) {
        Set<Character> window = new HashSet<>();
        int best = 0, l = 0;
        for (int r = 0; r < s.length(); r++) {
            // Set.add returns false if already present → shrink until it doesn't.
            while (!window.add(s.charAt(r))) window.remove(s.charAt(l++));
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    /*
    LC 340. Longest substring with at most K distinct characters.

    Canonical sliding window: grow `r`, shrink `l` while invalid, record.
    Invariant: count = char frequencies in s[l..r], count.size() <= k.
    */
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (k <= 0) return 0;
        Map<Character, Integer> count = new HashMap<>();
        int best = 0, l = 0;
        for (int r = 0; r < s.length(); r++) {
            count.merge(s.charAt(r), 1, Integer::sum);
            while (count.size() > k) {
                char c = s.charAt(l++);
                if (count.merge(c, -1, Integer::sum) == 0) count.remove(c);
            }
            best = Math.max(best, r - l + 1);
        }
        return best;
    }
}
