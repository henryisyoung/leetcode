package airbnb.New2026;

import java.util.*;

/*
================================================================================
  Shortest Uncommon Substring in an Array  (LC 3076)
================================================================================

  Given an array `arr` of n non-empty strings, return an array `answer` of
  length n where `answer[i]` is the SHORTEST substring of `arr[i]` that does
  NOT appear as a substring of any other `arr[j]` (j != i).

  Tie-break: among multiple shortest candidates, return the LEX SMALLEST.
  If no such substring exists, return "" for that index.

  Spec examples
    arr = ["cab","ad","bad","c"]      →  ["ab", "",   "ba", ""  ]
    arr = ["abc","bcd","abcd"]        →  ["",   "",   "abcd"    ]

  Constraints (per LC)
    2 <= n <= 100,  1 <= |arr[i]| <= 20,  lowercase letters only.

  Algorithm — global substring → owner-set map

    1. Build  idxOf : substring -> Set<Integer>  of all arr-indices that
       contain that substring.  (Using a SET per index avoids inflating
       counts when the same substring occurs multiple times in one string.)

    2. For each i, enumerate substrings of arr[i] in (length asc, lex asc)
       order. A substring is "uncommon to i" iff  idxOf[sub].size() == 1
       (the only owner is i itself).  Return the first such substring.

       (We don't need to verify that the single owner *is* i — by
       construction, every substring of arr[i] has i in its owner set,
       so size == 1 already implies the owner is i.)

  Complexity
    Let n = arr.length, L = max |arr[i]|.
    Time:   O(n · L^3)   for the map build  (n strings × O(L^2) substrings
                          × O(L) hashing/storage per substring).
            O(n · L^3)   for the per-i lookup loop in the worst case.
    Memory: O(n · L^2 · L) = O(n · L^3) for the map (overestimate).
    For LC limits (n=100, L=20) this is < 1M ops — instant.

  Why size==1, not "j != i" probe per candidate
    Naive: for each candidate, scan all OTHER strings checking containment.
    That's O(n · L) per candidate × O(L^2) candidates per i × n strings =
    O(n^2 · L^4). Switching to the prebuilt map saves a full factor of n.

  Follow-ups worth mentioning
    F1. Streaming / very long strings — switch to a suffix automaton or
        generalized suffix tree of the whole array, then walk it once per i.
        O(total length) build, O(L) query. Overkill for L=20 but standard
        for L >> 1000.
    F2. Top-K shortest uncommon substrings instead of just one — same scan,
        keep a TreeSet of size K.
    F3. "Uncommon to ANY j subset" generalisation — same map, change the
        owner-set predicate (e.g. `owners.equals(subset)`).
    F4. Output the substring's POSITION in arr[i], not just the string —
        store (substring, startIndex) pairs alongside the owner set.
================================================================================
*/
public class ShortestUncommonSubstring {

    public static String[] shortestSubstrings(String[] arr) {
        int n = arr.length;
        String[] ans = new String[n];

        Map<String, Set<Integer>> idxOf = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String s = arr[i];
            for (int a = 0; a < s.length(); a++) {
                for (int b = a + 1; b <= s.length(); b++) {
                    idxOf.computeIfAbsent(s.substring(a, b), k -> new HashSet<>()).add(i);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            String s = arr[i];
            String best = "";
            for (int len = 1; len <= s.length(); len++) {
                // One linear pass: track the lex-smallest substring of this length
                // that is uncommon to i. No sorted set, no dedupe needed -- duplicate
                // substrings hit the same min and are harmless.
                String minUncommon = null;
                for (int a = 0; a + len <= s.length(); a++) {
                    String c = s.substring(a, a + len);
                    if (idxOf.get(c).size() == 1                              // only owner is i
                            && (minUncommon == null || c.compareTo(minUncommon) < 0)) {
                        minUncommon = c;
                    }
                }
                if (minUncommon != null) { best = minUncommon; break; }
            }
            ans[i] = best;
        }
        return ans;
    }

    /* --------------------------- Brute-force oracle --------------------------- */
    /*
     * Reference implementation for the cross-check tests. For each (i, candidate)
     * scans every other arr[j] for containment. O(n^2 * L^4) — fine for n,L <= 20
     * in unit tests, used solely to validate the optimized version.
     */
    public static String[] shortestSubstringsBrute(String[] arr) {
        int n = arr.length;
        String[] ans = new String[n];
        for (int i = 0; i < n; i++) {
            String s = arr[i];
            String best = "";
            for (int len = 1; len <= s.length() && best.isEmpty(); len++) {
                String minUncommon = null;
                for (int a = 0; a + len <= s.length(); a++) {
                    String c = s.substring(a, a + len);
                    boolean inOther = false;
                    for (int j = 0; j < n && !inOther; j++) {
                        if (j != i && arr[j].contains(c)) inOther = true;
                    }
                    if (!inOther && (minUncommon == null || c.compareTo(minUncommon) < 0)) {
                        minUncommon = c;
                    }
                }
                if (minUncommon != null) best = minUncommon;
            }
            ans[i] = best;
        }
        return ans;
    }

    /* --------------------------- Demos / tests --------------------------- */

    public static void main(String[] args) {
        check("LC example 1",
                new String[]{"cab","ad","bad","c"},
                new String[]{"ab","","ba",""});

        check("LC example 2",
                new String[]{"abc","bcd","abcd"},
                new String[]{"","","abcd"});

        check("two identical strings",
                new String[]{"abc","abc"},
                new String[]{"",""});

        check("two completely disjoint strings",
                new String[]{"abc","xyz"},
                new String[]{"a","x"});

        check("single-char strings, all distinct",
                new String[]{"a","b","c"},
                new String[]{"a","b","c"});

        check("single-char strings, duplicates",
                new String[]{"a","a","b"},
                new String[]{"","","b"});

        check("one string is substring of the other",
                new String[]{"abc","abcdef"},
                new String[]{"","d"});                 // "d" is shortest lex-smallest unique to abcdef

        check("lex tie-break within same length",
                new String[]{"cba","xyz"},
                new String[]{"a","x"});                // a < b < c, x < y < z

        check("repeated chars in one string",
                new String[]{"aaaa","bbbb"},
                new String[]{"a","b"});

        check("all identical, n=3",
                new String[]{"abc","abc","abc"},
                new String[]{"","",""});

        check("mid-only uniqueness",
                new String[]{"axb","ayb","azb"},
                new String[]{"x","y","z"});            // 'a','b' shared; middle char unique

        // Cross-validation: optimized vs brute-force on randomized inputs.
        Random rng = new Random(7);
        int passes = 200;
        int fails = 0;
        for (int t = 0; t < passes; t++) {
            int n = 2 + rng.nextInt(5);
            String[] arr = new String[n];
            for (int i = 0; i < n; i++) {
                int L = 1 + rng.nextInt(6);
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < L; k++) sb.append((char) ('a' + rng.nextInt(3)));
                arr[i] = sb.toString();
            }
            String[] fast  = shortestSubstrings(arr);
            String[] brute = shortestSubstringsBrute(arr);
            if (!Arrays.equals(fast, brute)) {
                fails++;
                if (fails <= 3) {
                    System.out.println("MISMATCH on " + Arrays.toString(arr)
                            + "\n  fast =" + Arrays.toString(fast)
                            + "\n  brute=" + Arrays.toString(brute));
                }
            }
        }
        System.out.println((fails == 0 ? "OK   " : "FAIL ")
                + "stress test (" + passes + " random cases, fails=" + fails + ")");
    }

    /* --------------------------- helpers --------------------------- */

    private static void check(String label, String[] arr, String[] expected) {
        String[] got = shortestSubstrings(arr);
        boolean ok = Arrays.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + "  arr=" + Arrays.toString(arr)
                + "  got="     + Arrays.toString(got)
                + (ok ? "" : "  expected=" + Arrays.toString(expected)));
    }
}
