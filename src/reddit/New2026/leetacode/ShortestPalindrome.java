package reddit.New2026.leetacode;

import java.util.*;

/*
================================================================================
  ShortestPalindrome — add characters IN FRONT of s to make a palindrome
================================================================================

  GIVEN
    A string s. You may only prepend characters. Return the SHORTEST palindrome
    obtainable this way.

        "aacecaaa"  ->  "aaacecaaa"      (prepend "a")
        "abcd"      ->  "dcbabcd"        (prepend "dcb")
        "racecar"   ->  "racecar"        (already a palindrome, prepend nothing)

  --------------------------------------------------------------------------
  THE WHOLE PROBLEM IN ONE LINE
  --------------------------------------------------------------------------
    Find k = length of the LONGEST PALINDROMIC PREFIX of s. Then

        answer = reverse(s.substring(k)) + s          length 2n - k

    Why: whatever we prepend must mirror a suffix of s, so the part of s that
    stays un-mirrored is exactly a palindromic prefix. Keeping the longest one
    minimises what we have to prepend. Nothing shorter can work, because the
    prepended block is FORCED — see the "why it's minimal" note below.

    ⚠ PREFIX, NOT SUFFIX. This is the single easiest thing to get backwards.
      We prepend, so the surviving palindrome must sit at the FRONT.
      On "aacecaaa": longest palindromic prefix is "aacecaa" (7) → prepend "a".
      The longest palindromic SUFFIX is "aaa" (3), which would have us
      prepend "reverse(aacec)" = "cecaa" — a palindrome, but 13 chars, not 9.
      Same string, both readings produce A palindrome; only one is shortest.

    ⚠ Don't reverse the whole string and concatenate. reverse(s) + s is always
      a palindrome and always length 2n, so it is "correct" and never optimal
      except when s has no palindromic prefix at all — which cannot happen,
      since any single character is a palindrome, so k >= 1 for non-empty s.

  --------------------------------------------------------------------------
  Route A — KMP prefix function on s + '#' + reverse(s)          (solution)
  --------------------------------------------------------------------------
    Build  t = s + '#' + reverse(s)  and take the prefix function of t. The
    last entry is the longest proper prefix of t that is also a suffix of t.

    A prefix of t of length L is s[0..L). The last L characters of reverse(s)
    are s[L-1], s[L-2], …, s[0], i.e. reverse(s[0..L)). So
          prefix == suffix   <=>   s[0..L) == reverse(s[0..L))
                             <=>   s[0..L) is a palindrome
    and the prefix function hands us the LARGEST such L for free. That is k.

    ⚠ THE '#' IS LOAD-BEARING, not decoration. It is a character that appears
      in neither half, so no border can straddle the seam and k <= n is
      guaranteed. Two ways it bites if you drop it, both measured over all
      282,114 strings of length <= 13 on alphabets of size <= 3:

        crash   2,222 strings give k > n. s = "aa" gives t = "aaaa" whose
                prefix function ends at 3 > n = 2, so s.substring(3) throws
                StringIndexOutOfBoundsException.

        silent  2,094 strings are STILL wrong after clamping with min(k, n),
                so the clamp is not a fix. Smallest case s = "aaba":
                t = "aabaabaa" has border "aabaa" of length 5, clamped to 4,
                which claims all of "aaba" is a palindrome. It isn't, and the
                returned "aaba" is not a palindrome at all — a wrong answer
                with no exception to warn you. True k is 2, answer "abaaba".
                "aabba", "bbaab" and "baaba" fail the same way.

        empty   s = "" makes t = "" too, so pi is empty and pi[t.length()-1]
                indexes pi[-1]. With the sentinel, t = "#" and k = 0 falls
                out correctly with no special case.

      If s itself can contain '#', pick any sentinel outside the alphabet.

  --------------------------------------------------------------------------
  Route B — scan prefixes longest-first                     (solutionScan)
  --------------------------------------------------------------------------
    Test s[0..k) for k = n, n-1, … until one is a palindrome. Θ(n²) worst
    case ("abbbb…b" tests every length), but it is three lines, obviously
    correct, and makes a good cross-check for Route A. Write this first at a
    whiteboard, then say "the prefix-function trick makes it linear".

  --------------------------------------------------------------------------
  Route C — greedy two-pointer + recursion            (solutionRecursive)
  --------------------------------------------------------------------------
    The popular short answer (same as leetcode/solution/Solution214). Walk i
    forward and j backward, advancing i only on a match. That loop is really
    the greedy "is A a subsequence of B" scan with A = s, B = reverse(s), so
    i = longest prefix of s that is a SUBSEQUENCE of reverse(s). Hence:

      - i >= k always, because s[0..k) being a palindrome makes it a literal
        suffix of reverse(s). So s[i..n) must be mirrored — commit to it.
      - i == n only for a palindrome (equal lengths force equality), so the
        early return is exact.
      - i <= n, and n increments need all n iterations, so the unguarded
        s.charAt(i) can only reach n on the last step. Cannot throw.

    Correct: matches Route A on all 331,271 strings of length <= 15 over
    alphabets <= 3, and the invariants hold over 1,154,890 strings.

    ⚠ THE RECURSION IS THE CORRECTNESS FIX, NOT AN OPTIMISATION. Subsequence
      is far weaker than palindrome, so i overshoots k on 642,142 of those
      1,154,890. One pass — reverse(s.substring(i)) + s — is a palindrome
      ONLY when s[0..i) is one, i.e. only when i happens to equal k, so the
      outcome is all-or-nothing: either exactly right or not a palindrome at
      all. Of 282,117 strings, 152,176 come out NOT PALINDROMIC (smallest
      "aababa" -> "abaababa") and 0 come out merely longer. Recursing re-runs
      the weak test on s[0..i), whose longest palindromic prefix is still k,
      squeezing i down to k without ever cutting past it.

    ⚠ COST Θ(n²) time and n/2 STACK FRAMES; "aa" + "ba"*m is the worst case,
      with comparisons converging to n²/4 (2018x slower than Route A at
      n = 50,000). The stack is the real hazard: n = 128,002 overflows, and
      a 256 KB stack dies at n = 16,002 — inside LeetCode's own n <= 5·10^4.
      Random input recurses ~2 deep and hides it. recursionCeiling() in
      main() provokes the overflow rather than just claiming it.

    Two things to drop from the compact form: "if (s.length() < 2) return s"
    is redundant (n = 0 and n = 1 both exit via the palindrome branch), and
    reverse(s.substring(i)) and s.substring(i) are the same substring twice.

  --------------------------------------------------------------------------
  Why the answer is minimal (worth saying out loud)
  --------------------------------------------------------------------------
    Suppose the answer is x + s with |x| = m, total length L = n + m. Every
    character is then pinned: position i < m must mirror position L-1-i, which
    lands inside s, so x[i] = s[n-1-i]. In other words x is FORCED to be the
    first m characters of reverse(s) — there is no choice to make. So the only
    question is the smallest m for which that forced candidate is a palindrome,
    and m = n - k. This also gives a completely independent way to compute the
    answer (construct and test each m), which is what the randomised check in
    main() uses as an oracle.

  --------------------------------------------------------------------------
  Other routes, and why they aren't here
  --------------------------------------------------------------------------
    Rolling hash      compare hash(s[0..k)) with hash(reverse) as k shrinks,
                      O(n) expected — but it is a probabilistic answer, and
                      an interviewer may ask about collisions.
    Manacher          computes every palindromic radius in O(n); we only need
                      the ones anchored at index 0, so it is strictly more
                      machinery than the problem needs.

  --------------------------------------------------------------------------
  Complexity   (n = s.length())
  --------------------------------------------------------------------------
    solution          O(n) time,  O(n) space   prefix function over 2n+1 chars
    solutionScan      O(n²) time, O(n) space   worst case tests every prefix
    solutionRecursive O(n²) time, O(n) STACK   depth n/2, ~n²/4 comparisons
    All three allocate the result, Θ(2n - k) — so O(n) output either way.
    Prefer solution(); Route C is here because it is the popular answer and
    the stack ceiling is worth having seen.

    Edge cases that need no special-casing: "" gives t = "#", k = 0 and an
    empty result; a 1-char string gives k = 1 and returns itself; an
    already-palindromic s gives k = n and returns s untouched.
================================================================================
*/
public class ShortestPalindrome {

    /** Route A — O(n) via the KMP prefix function. */
    public static String solution(String s) {
        int n = s.length();
        // '#' cannot occur in either half, so no border straddles the seam
        // and the resulting k is guaranteed to be <= n.
        String t = s + '#' + new StringBuilder(s).reverse();

        int[] pi = prefixFunction(t);
        int k = pi[t.length() - 1];                 // longest palindromic prefix of s

        return new StringBuilder(s.substring(k)).reverse() + s;
    }

    /** pi[i] = length of the longest proper prefix of t[0..i] that is also its suffix. */
    private static int[] prefixFunction(String t) {
        int[] pi = new int[t.length()];
        for (int i = 1; i < t.length(); i++) {
            int j = pi[i - 1];
            while (j > 0 && t.charAt(i) != t.charAt(j)) j = pi[j - 1];
            if (t.charAt(i) == t.charAt(j)) j++;
            pi[i] = j;
        }
        return pi;
    }

    /** Route B — O(n²) longest-first prefix scan. Same answer, no cleverness. */
    public static String solutionScan(String s) {
        for (int k = s.length(); k >= 0; k--) {     // k = 0 is the empty prefix, always a palindrome
            if (isPalindrome(s, 0, k - 1)) {
                return new StringBuilder(s.substring(k)).reverse() + s;
            }
        }
        throw new IllegalStateException("unreachable: the empty prefix is a palindrome");
    }

    /**
     * Route C — greedy two-pointer, then recurse on the prefix it lands on.
     * O(n²) time and n/2 stack frames; see the header before reaching for it.
     */
    public static String solutionRecursive(String s) {
        int n = s.length();

        int i = 0;
        for (int j = n - 1; j >= 0; j--) {
            // Unguarded on purpose: i rises at most once per iteration and there
            // are exactly n of them, so i only reaches n on the final step.
            if (s.charAt(i) == s.charAt(j)) i++;
        }
        if (i == n) return s;                       // i == n exactly when s is a palindrome

        // i is an upper bound on k, never below it, so s[i..n) must be mirrored
        // and recursing on s[0..i) still sees the same longest palindromic prefix.
        String tail = s.substring(i);
        return new StringBuilder(tail).reverse()
                + solutionRecursive(s.substring(0, i))
                + tail;
    }

    private static boolean isPalindrome(String s, int lo, int hi) {
        while (lo < hi) {
            if (s.charAt(lo++) != s.charAt(hi--)) return false;
        }
        return true;
    }

    /* ============================== Tests =============================== */

    public static void main(String[] args) {

        /* ------------------------- worked examples ------------------------- */
        expect("the classic example",      "aacecaaa", "aaacecaaa");
        expect("nothing palindromic",      "abcd",     "dcbabcd");
        expect("already a palindrome",     "racecar",  "racecar");
        expect("even-length palindrome",   "abba",     "abba");

        /* --------------------------- degenerate --------------------------- */
        expect("empty string",             "",   "");
        expect("single char",              "a",  "a");
        expect("two identical",            "aa", "aa");
        expect("two different",            "ab", "bab");

        /* ---- the case that breaks a missing '#': repeated single char ---- */
        // Without the separator, t = "aaaa" and the prefix function ends at 3 > n,
        // so s.substring(3) throws. These pin the guard.
        expect("aaa",  "aaa",  "aaa");
        expect("aaaa", "aaaa", "aaaa");
        expect("all-same, long", "aaaaaaaaaa", "aaaaaaaaaa");

        // The smallest string where clamping min(k, n) instead of using the
        // sentinel returns a NON-palindrome ("aaba") with no exception.
        expect("clamping counterexample", "aaba", "abaaba");
        expect("...and its mirror",       "abaa", "aabaa");

        /* -------------------- prefix vs suffix confusion ------------------- */
        // Longest palindromic PREFIX is "aa" (2); the longest palindromic
        // SUFFIX is "abba" (4) and would give a longer, wrong answer.
        expect("prefix beats suffix", "aabba", "abbaabba");
        expect("palindromic prefix, junk tail", "abbacd", "dcabbacd");
        expect("junk head, palindromic tail",   "cdabba", "abbadcdabba");

        /* ----------------------------- shapes ----------------------------- */
        expect("one repeated pair",   "aabaa",   "aabaa");
        expect("near-miss palindrome","abcba",   "abcba");
        expect("off by one char",     "abcbaa",  "aabcbaa");
        // Odd-length palindromic prefix inside an even-length string: the prefix
        // is "aba" (3), not "a" (1), so only one character gets prepended.
        expect("odd prefix, even string", "abab", "babab");
        expect("long tail to mirror", "abcdefg", "gfedcbabcdefg");

        /* ------------------------- invariants ----------------------------- */
        for (String s : new String[]{"", "a", "ab", "aacecaaa", "abbacd", "aabba", "abcdefg"}) {
            String got = solution(s);
            expectBool("result is a palindrome: \"" + s + "\"", isPalindrome(got, 0, got.length() - 1), true);
            expectBool("result ends with s: \"" + s + "\"",      got.endsWith(s), true);
            expectBool("only prepended, never edited: \"" + s + "\"",
                    got.substring(got.length() - s.length()).equals(s), true);
        }

        randomChecks();
        recursionCeiling();
    }

    /**
     * Provokes Route C's stack limit instead of just claiming it in a comment.
     * "aa" + "ba"*m is its worst-case family — depth is exactly n/2 — so
     * doubling n walks straight into the ceiling. Catching StackOverflowError
     * is only defensible here because causing it IS the measurement.
     */
    private static void recursionCeiling() {
        int lastOk = 0;
        for (int m = 8000; m <= 512000; m *= 2) {
            String s = "aa" + "ba".repeat(m);
            try {
                if (!solutionRecursive(s).equals(solution(s))) {
                    System.out.println("FAIL Route C disagrees with Route A at n=" + s.length());
                    return;
                }
                lastOk = s.length();
            } catch (StackOverflowError e) {
                System.out.printf("recursion ceiling: Route C ok to n=%d, StackOverflowError at "
                                + "n=%d; Route A returns length %d for the same input%n",
                        lastOk, s.length(), solution(s).length());
                return;
            }
        }
        System.out.println("recursion ceiling: no overflow up to n=" + lastOk);
    }

    /* --------- randomised cross-check + an independent oracle --------- */

    private static void randomChecks() {
        Random rnd = new Random(214);
        int trials = 200000, mismatch = 0, badRecursive = 0, badOracle = 0, notPalindrome = 0, badSuffix = 0;
        long totalLen = 0, alreadyPalindrome = 0, worstCase = 0;
        Set<String> distinct = new HashSet<>();

        for (int t = 0; t < trials; t++) {
            // Small alphabets make palindromic prefixes common; size 1 and 2 are
            // where the separator bug and the prefix/suffix mix-up actually bite.
            int alphabet = 1 + rnd.nextInt(3);
            int n = rnd.nextInt(14);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alphabet)));
            String s = sb.toString();

            String fast = solution(s), scan = solutionScan(s), oracle = forcedCandidateOracle(s);
            String rec = solutionRecursive(s);
            totalLen += fast.length();
            if (fast.length() < 40) distinct.add(fast);
            if (fast.equals(s)) alreadyPalindrome++;
            if (fast.length() == 2 * n - 1 && n > 0) worstCase++;

            if (!fast.equals(scan) && mismatch++ < 4)
                System.out.println("KMP vs SCAN  s=\"" + s + "\" kmp=\"" + fast + "\" scan=\"" + scan + "\"");
            if (!fast.equals(rec) && badRecursive++ < 4)
                System.out.println("KMP vs REC   s=\"" + s + "\" kmp=\"" + fast + "\" rec=\"" + rec + "\"");
            if (!fast.equals(oracle) && badOracle++ < 4)
                System.out.println("KMP vs ORACLE s=\"" + s + "\" kmp=\"" + fast + "\" oracle=\"" + oracle + "\"");
            // Checked on every route, not just KMP: a one-pass Route C returns
            // non-palindromes, so the property has to be asserted where it can fail.
            for (String got : new String[]{fast, scan, rec, oracle}) {
                if (!isPalindrome(got, 0, got.length() - 1) && notPalindrome++ < 4)
                    System.out.println("NOT A PALINDROME s=\"" + s + "\" -> \"" + got + "\"");
                if (!got.endsWith(s) && badSuffix++ < 4)
                    System.out.println("DOES NOT END WITH s: \"" + s + "\" -> \"" + got + "\"");
            }
        }

        System.out.printf("%n%d random strings: %d KMP/scan mismatches, %d KMP/recursive mismatches, "
                        + "%d oracle mismatches, %d non-palindromes, %d bad suffixes%n",
                trials, mismatch, badRecursive, badOracle, notPalindrome, badSuffix);
        System.out.printf("coverage: %d distinct results, %d already palindromic, "
                        + "%d needed the maximum n-1 prepends, mean length %.2f%n",
                distinct.size(), alreadyPalindrome, worstCase, totalLen / (double) trials);

        // A size the O(n^2) route would not enjoy, to show the linear one holds up.
        StringBuilder big = new StringBuilder("b");
        for (int i = 0; i < 200000; i++) big.append('a');
        String stress = big.toString();                     // "baaa…a": palindromic prefix is just "b"
        String out = solution(stress);
        System.out.printf("stress: n=%d -> length %d, palindrome=%b, ends with s=%b%n",
                stress.length(), out.length(),
                isPalindrome(out, 0, out.length() - 1), out.endsWith(stress));
    }

    /**
     * Independent oracle by a different method: never looks for a palindromic
     * prefix. The prepended block is forced to be a prefix of reverse(s), so
     * just try each length and keep the first candidate that is a palindrome.
     */
    private static String forcedCandidateOracle(String s) {
        String rev = new StringBuilder(s).reverse().toString();
        for (int m = 0; m <= s.length(); m++) {
            String candidate = rev.substring(0, m) + s;
            if (isPalindrome(candidate, 0, candidate.length() - 1)) return candidate;
        }
        throw new IllegalStateException("unreachable: reverse(s) + s is always a palindrome");
    }

    /* --------------------------- helpers --------------------------- */

    /** Runs all three routes so every literal expectation covers each implementation. */
    private static void expect(String label, String input, String expected) {
        report(label + " (KMP)",       solution(input),          expected);
        report(label + " (scan)",      solutionScan(input),      expected);
        report(label + " (recursive)", solutionRecursive(input), expected);
    }

    private static void expectBool(String label, boolean got, boolean expected) {
        report(label, got, expected);
    }

    private static <T> void report(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
