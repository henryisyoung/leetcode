package snowflake.mianjing;

import java.util.HashMap;
import java.util.Map;

/*
Check if an Original String Exists Given Two Encoded Strings
Problem Description
Imagine we have an original string. We encode it by hiding some parts and replacing them with numbers representing their length. You are given two of these encoded strings, s1 and s2. They contain lowercase letters and digits from 1 to 9.

The digits tell us how many characters are hidden. However, when digits appear next to each other, it can get tricky. You can interpret consecutive digits as one single number or as a sequence of smaller numbers.

For example, the string "123" could mean:

One hidden segment of length 123.
A segment of length 12 followed by a segment of length 3.
A segment of length 1 followed by a segment of length 23.
Three separate segments of lengths 1, 2, and 3.
Your goal is to check if s1 and s2 could both describe the same original string. Return true if this is possible. Otherwise, return false.

Sample Cases
Case 1:

Input: s1 = "internationalization", s2 = "i18n"

Output: true

Case 2:

Input: s1 = "l123e", s2 = "44"

Output: true

Case 3:

Input: s1 = "a5b", s2 = "c5b"

Output: false

Input Constraints
The lengths of s1 and s2 are between 1 and 40 characters.
s1 and s2 only contain lowercase letters and digits 1-9.
You will never see more than 3 digits in a row.
 */
/**
 * Decide whether two encoded strings could describe the same original string.
 *
 * Key idea
 * --------
 * Walk both strings with two pointers (i in s1, j in s2) and a single
 * accumulator `diff`:
 *
 *     diff = (chars s1 has emitted so far) - (chars s2 has emitted so far)
 *
 * Each digit run on the s1 side can be parsed as one number (1..999), which
 * adds that many "wildcard" characters to s1's output → diff += num.
 * Same on the s2 side, but it subtracts.  A literal letter emits 1 fixed char.
 *
 * At every state (i, j, diff) we have exactly one of these moves:
 *
 *   1. s1[i] is a digit  → MUST consume it now. Try every digit-run length
 *      (1..3 digits, since the prompt guarantees no more than 3 in a row).
 *
 *   2. s2[j] is a digit  → same on the s2 side.
 *
 *   3. Both are letters and diff == 0
 *      → letters must match exactly; advance both. (If they mismatch, dead end.)
 *
 *   4. diff > 0 (s1 ran ahead with wildcards)
 *      → s2's next letter is "covered" by one of those wildcards.
 *        Advance j, diff -= 1.
 *
 *   5. diff < 0 (s2 ran ahead with wildcards)
 *      → symmetric; advance i, diff += 1.
 *
 * Goal: reach (n1, n2, 0).
 *
 * Memoize on (i, j, diff). Word length <= 40 and digit values <= 999 keep the
 * state space well-bounded; in practice only a few thousand states are visited.
 *
 * Time:  O(n1 * n2 * D) in the worst case, where D is the reachable diff range.
 * Space: same, for the memo table.
 */
public class OriginalStringExists {

    public boolean possiblyEquals(String s1, String s2) {
        return dfs(s1, s2, 0, 0, 0, new HashMap<>());
    }

    private boolean dfs(String s1, String s2, int i, int j, int diff,
                        Map<Long, Boolean> memo) {
        int n1 = s1.length(), n2 = s2.length();

        // Base case: both strings consumed, diff must net out to 0.
        if (i == n1 && j == n2) return diff == 0;

        long key = encode(i, j, diff);
        Boolean cached = memo.get(key);
        if (cached != null) return cached;

        boolean ok = false;

        if (i < n1 && Character.isDigit(s1.charAt(i))) {
            // Case 1: peel a digit-run from s1 (1..3 digits).
            int num = 0;
            for (int k = i; k < Math.min(i + 3, n1) && Character.isDigit(s1.charAt(k)); k++) {
                num = num * 10 + (s1.charAt(k) - '0');
                if (dfs(s1, s2, k + 1, j, diff + num, memo)) { ok = true; break; }
            }
        } else if (j < n2 && Character.isDigit(s2.charAt(j))) {
            // Case 2: peel a digit-run from s2.
            int num = 0;
            for (int k = j; k < Math.min(j + 3, n2) && Character.isDigit(s2.charAt(k)); k++) {
                num = num * 10 + (s2.charAt(k) - '0');
                if (dfs(s1, s2, i, k + 1, diff - num, memo)) { ok = true; break; }
            }
        } else if (diff == 0) {
            // Case 3: both letters lined up with no wildcard imbalance — must match.
            if (i < n1 && j < n2 && s1.charAt(i) == s2.charAt(j)) {
                ok = dfs(s1, s2, i + 1, j + 1, 0, memo);
            }
        } else if (diff > 0) {
            // Case 4: s1 is ahead — let s2's next letter consume one wildcard char.
            if (j < n2) ok = dfs(s1, s2, i, j + 1, diff - 1, memo);
        } else { // diff < 0
            // Case 5: symmetric.
            if (i < n1) ok = dfs(s1, s2, i + 1, j, diff + 1, memo);
        }

        memo.put(key, ok);
        return ok;
    }

    // i, j ≤ 40; diff is small in practice but pad generously to avoid collisions.
    private long encode(int i, int j, int diff) {
        return ((long) i * 64L + j) * 200_000L + (diff + 100_000);
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    public static void main(String[] args) {
        OriginalStringExists s = new OriginalStringExists();

        check(s.possiblyEquals("internationalization", "i18n"), true,  "case 1");
        check(s.possiblyEquals("l123e", "44"),                  true,  "case 2");
        check(s.possiblyEquals("a5b",  "c5b"),                  false, "case 3");

        // Both encodings have no digits → must be literally equal.
        check(s.possiblyEquals("abc", "abc"), true,  "literal equal");
        check(s.possiblyEquals("abc", "abd"), false, "literal mismatch");

        // Pure wildcard strings: any same-length representations work.
        check(s.possiblyEquals("3", "3"),    true, "3 vs 3");
        check(s.possiblyEquals("12", "21"),  true, "12 == 21 wildcards");
        // "12" can be parsed as 1+2 = 3 wildcards, equal to "3".
        check(s.possiblyEquals("12", "3"),   true, "12 (=1+2) == 3");

        // Tricky: "1+1" segments vs "2"  → both 2 wildcards, OK.
        check(s.possiblyEquals("11", "2"),   true, "1+1 == 2");

        // a1b ↔ 1c1: both can describe "acb" (a at pos0, c at pos1, b at pos2).
        check(s.possiblyEquals("a1b", "1c1"), true, "a?b vs ?c? both describe acb");

        // Real conflict: 'a' fixed at pos0 in s1, 'b' fixed at pos0 in s2.
        check(s.possiblyEquals("a1b", "b1a"), false, "true position conflict");

        // Letter pinned compatibly via wildcards.
        check(s.possiblyEquals("a1b", "2b"),  true, "a + 1 + b  vs  2 + b");

        // Trailing wildcard.
        check(s.possiblyEquals("ab2", "abcd"), true,  "ab + 2 vs ab + cd");
        check(s.possiblyEquals("ab2", "abcde"), false, "ab + 2 vs ab + cde");
    }

    private static void check(boolean got, boolean expected, String label) {
        System.out.println(label + ": " + got + (got == expected ? "  OK" : "  FAIL (expected " + expected + ")"));
    }
}
