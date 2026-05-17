package netflix;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/*
Count unordered index pairs (i < j) where words[i] and words[j] share
no common characters.

Insight: encode each word as a 26-bit mask of its lowercase letters.
  words share no common char  ⇔  (maskA & maskB) == 0

Many words collapse to the same mask ("apple" and "papel" both →
{a,e,l,p}), so bucket by mask once and then count pairs over the D
distinct masks in O(D²).

Same-bucket pairs:
  - Non-zero mask: a mask is never disjoint with itself → contributes 0.
  - Mask 0 (empty word): disjoint with itself → contributes C(cnt, 2).

Extension (larger alphabet — uppercase, digits, Unicode):
  - ≤ 64 chars (e.g. [a-zA-Z0-9]): swap `int` mask → `long`, same code.
  - arbitrary chars: use `BitSet` as the key (`countPairsAny` below).
    BitSet implements equals/hashCode, so the bucketing structure is
    identical; only the per-pair AND cost grows to O(|Σ|/64).

Followup if D blows up (alphabet bounded but D close to n):
  Sum-Over-Subsets DP on 2^|Σ| — O(|Σ| · 2^|Σ|) preprocess,
  O(1) per word. Only worth it when D ≫ √n.

Complexity
  Time:   O(L + D²)   L = total chars across words, D = distinct masks
  Memory: O(D)
*/
public class CountStringPairsWithNoCommonChars {

    public long countPairs(String[] words) {
        Map<Integer, Long> freq = new HashMap<>();
        for (String w : words) freq.merge(maskOf(w), 1L, Long::sum);

        int[]  masks = new int[freq.size()];
        long[] cnts  = new long[freq.size()];
        int k = 0;
        for (Map.Entry<Integer, Long> e : freq.entrySet()) {
            masks[k]   = e.getKey();
            cnts[k++]  = e.getValue();
        }

        long total = 0;
        for (int i = 0; i < masks.length; i++) {
            // Mask 0 is the only mask disjoint from itself.
            if (masks[i] == 0) total += cnts[i] * (cnts[i] - 1) / 2;
            for (int j = i + 1; j < masks.length; j++) {
                if ((masks[i] & masks[j]) == 0) total += cnts[i] * cnts[j];
            }
        }
        return total;
    }

    private static int maskOf(String w) {
        int m = 0;
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            if (c >= 'a' && c <= 'z') m |= 1 << (c - 'a');
        }
        return m;
    }

    /* --- general-alphabet variant: any chars allowed (uppercase, digits, Unicode) --- */

    public long countPairsAny(String[] words) {
        Map<BitSet, Long> freq = new HashMap<>();
        for (String w : words) freq.merge(charSetOf(w), 1L, Long::sum);

        BitSet[] masks = new BitSet[freq.size()];
        long[]   cnts  = new long[freq.size()];
        int k = 0;
        for (Map.Entry<BitSet, Long> e : freq.entrySet()) {
            masks[k]   = e.getKey();
            cnts[k++]  = e.getValue();
        }

        long total = 0;
        for (int i = 0; i < masks.length; i++) {
            // Empty char-set is the only one disjoint from itself.
            if (masks[i].isEmpty()) total += cnts[i] * (cnts[i] - 1) / 2;
            for (int j = i + 1; j < masks.length; j++) {
                if (!masks[i].intersects(masks[j])) total += cnts[i] * cnts[j];
            }
        }
        return total;
    }

    private static BitSet charSetOf(String w) {
        BitSet b = new BitSet();
        for (int i = 0; i < w.length(); i++) b.set(w.charAt(i));
        return b;
    }
}
