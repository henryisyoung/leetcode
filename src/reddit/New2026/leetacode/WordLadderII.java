package reddit.New2026.leetacode;

import java.util.*;

/*
================================================================================
  WordLadderII — every SHORTEST transformation sequence from beginWord to endWord
================================================================================

  GIVEN
  beginWord, endWord, and a wordList. A step replaces characters of the current
  word, and the result must be in wordList. Return ALL shortest sequences.
  endWord must be in wordList or there is no answer; beginWord need not be.

  Part 1  each step changes EXACTLY ONE character          (findLadders)
  Part 2  each step changes ONE or TWO characters          (findLaddersOneOrTwo)

  --------------------------------------------------------------------------
  THE SHAPE OF THE ANSWER (both parts, unchanged)
  --------------------------------------------------------------------------
    1. BFS from beginWord, recording level[w] = fewest steps to reach w.
    2. DFS from beginWord, following ONLY edges where

           level[next] == level[cur] + 1

       and collecting a path whenever it lands on endWord.

    Step 2 is doing two jobs at once, and the second one is easy to miss.

    ⚠ THE LEVEL FILTER IS ALSO THE VISITED CHECK. Neighbourhood is symmetric
      ("hit" ~ "hot" ~ "hit"), so without the filter the DFS bounces between
      two words forever. Deleting it does NOT produce non-shortest paths, it
      produces a StackOverflowError:

          words=[hot,dot,dog,lot,log,cog], hit -> cog
            with the filter    2 paths, 8 DFS calls
            without it         StackOverflowError

      Levels strictly increase along every followed edge, so a cycle is
      impossible for free. That is why the DFS carries no visited set.

    ⚠ KEEP THE "+ 1" INLINE. level.get(next) is an Integer, so == looks like
      the classic reference-comparison bug. It is not: the + 1 makes the
      right side an int, which forces the left side to UNBOX. Hoisting it
      into an Integer breaks that, silently, and only above 127:

          Integer want = level.get(cur) + 1;
          level.get(next) == want      -> false for 201, true for 101
                                          (-128..127 are cached)

      Verified over a 150-long chain: the inline form is correct at levels
      well past the cache. Use .equals(...) or an int if you must hoist.
      That chain is an ASSERTION, not a printout — the hoisted version passes
      every other test in this file, since no other case reaches level 127.

    Writing >= instead of == is harmless, though not for a comforting reason:
    BFS levels of two ADJACENT words differ by at most 1, so level[next] can
    only be level[cur]-1, level[cur] or level[cur]+1, and >= level[cur]+1
    admits exactly the third. It is equivalent here and misleading anywhere
    else, so prefer ==.

  --------------------------------------------------------------------------
  Part 2 — "one OR two characters"
  --------------------------------------------------------------------------
    Only the edge definition changes: neighbours are now words at Hamming
    distance 1 or 2 instead of exactly 1. The BFS/DFS above is untouched,
    which is the point — the level trick never cared what an edge was.

    ⚠ "SHORTEST" MEANS FEWEST STEPS, NOT FEWEST CHARACTERS CHANGED. Those are
      different objectives once a step may change two characters: one 2-char
      step and two 1-char steps both change 2 characters, but the first is
      one step. This code minimises STEPS, which is what "transformation
      sequence" means. Part 2 answers are therefore usually SHORTER and MORE
      numerous than Part 1's, not a superset of them — see the tests, where
      hit -> cog goes from 2 paths of 5 words to 3 paths of 3 words.

    ⚠ Part 1 expectations do not carry over. A dictionary where Part 1 has a
      unique answer can have several in Part 2, and vice versa: allowing the
      bigger jump can bypass the very words Part 1 had to walk through.

    ⚠ THE BEST WAY TO FIND NEIGHBOURS FLIPS BETWEEN THE PARTS, so the Part 1
      habit is the wrong reflex for Part 2.

      by mutation   try every replacement: L*25 candidates for distance 1,
                    plus C(L,2)*625 more for distance 2. Cost does not depend
                    on the dictionary size.
      by scan       compare against every dictionary entry, O(W*L), and most
                    comparisons quit on the first character. Cost does not
                    depend on the alphabet or on maxChange.

      Measured at L = 10, W = 5000, expanding 200 words (main() prints all 12
      rows, including the small-dictionary cases where scan wins both):

                            mutation     scan
            maxChange = 1     0.90 ms   11.38 ms   -> mutation, 13x faster
            maxChange = 2   146.44 ms   11.56 ms   -> scan, 13x faster

      The L²*625 term is what flips it: at L = 10 that is 28,375 candidate
      strings per word, more than the 5,000-word dictionary being searched.
      So Part 1 uses mutation and Part 2 uses scan.

  --------------------------------------------------------------------------
  Other traps
  --------------------------------------------------------------------------
    ⚠ REBUILDING THE DICTIONARY SET PER CALL. Writing
      "Set<String> set = new HashSet<>(wordList)" inside the neighbour helper
      costs O(W) on every word expanded, turning the BFS into O(W² * L).
      Build it once. main() measures the difference.

    ⚠ The BFS must finish the level in which endWord first appears, so that
      every word at a smaller level has its adjacency recorded. Stopping the
      instant endWord is ENQUEUED leaves the last layer's adjacency missing
      and silently loses paths. (Stopping when it is DEQUEUED happens to be
      safe, because that is one full level later — subtle, so this code just
      finishes the level instead of relying on it.)

    ⚠ Words of a different length can never be neighbours; the helpers skip
      them rather than throwing, so a mixed-length list degrades gracefully.

    ⚠ The number of shortest paths can be exponential in the word length, so
      no algorithm returns them all in polynomial time. The BFS+DFS is
      output-sensitive: O(work) + O(total size of the answer).

  --------------------------------------------------------------------------
  Complexity   (W = |wordList|, L = word length, P = number of answers)
  --------------------------------------------------------------------------
    Part 1   BFS O(W * L² * 26) by mutation, DFS O(P * L) to emit
    Part 2   BFS O(W² * L) by scan,          DFS O(P * L) to emit
    Space    O(W * L) for the levels and adjacency, O(L) DFS depth
================================================================================
*/
public class WordLadderII {

    /* ======================= Part 1: exactly one char ======================= */

    /** All shortest ladders where each step changes exactly one character. */
    public static List<List<String>> findLadders(String beginWord, String endWord,
                                                 List<String> wordList) {
        return solve(beginWord, endWord, wordList, 1);
    }

    /* ==================== Part 2: one or two characters ==================== */

    /** All shortest ladders where each step changes one OR two characters. */
    public static List<List<String>> findLaddersOneOrTwo(String beginWord, String endWord,
                                                         List<String> wordList) {
        return solve(beginWord, endWord, wordList, 2);
    }

    /* ============================ shared engine ============================ */

    private static List<List<String>> solve(String beginWord, String endWord,
                                            List<String> wordList, int maxChange) {
        // Measured: mutation wins for one change, scan wins for two. See strategyCrossover().
        return solve(beginWord, endWord, wordList, maxChange, maxChange > 1);
    }

    static List<List<String>> solve(String beginWord, String endWord, List<String> wordList,
                                    int maxChange, boolean useScan) {
        List<List<String>> result = new ArrayList<>();
        if (beginWord == null || endWord == null || wordList == null) return result;

        Set<String> dict = new HashSet<>(wordList);            // built ONCE, not per word
        if (!dict.contains(endWord)) return result;             // unreachable by definition

        Map<String, List<String>> adjacency = new HashMap<>();
        Map<String, Integer> level = new HashMap<>();
        if (!bfsLevels(beginWord, endWord, dict, adjacency, level, maxChange, useScan)) return result;

        List<String> path = new ArrayList<>();
        path.add(beginWord);
        collectPaths(beginWord, endWord, level, adjacency, path, result);
        return result;
    }

    /**
     * Levels by BFS. Finishes the level in which endWord first appears, so every
     * word on a shortest path has its adjacency recorded. Returns whether endWord
     * was reached at all.
     */
    private static boolean bfsLevels(String beginWord, String endWord, Set<String> dict,
                                     Map<String, List<String>> adjacency,
                                     Map<String, Integer> level, int maxChange, boolean useScan) {
        Deque<String> queue = new ArrayDeque<>();
        queue.add(beginWord);
        level.put(beginWord, 0);

        int depth = 0;
        boolean found = false;
        while (!queue.isEmpty() && !found) {
            int size = queue.size();
            depth++;
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                List<String> nexts = useScan ? neighborsByScan(cur, dict, maxChange)
                                             : neighborsByMutation(cur, dict, maxChange);
                adjacency.put(cur, nexts);
                for (String next : nexts) {
                    if (next.equals(endWord)) found = true;
                    if (level.containsKey(next)) continue;      // already reached, at <= depth
                    level.put(next, depth);
                    queue.add(next);
                }
            }
        }
        return found;
    }

    /** Walks only level+1 edges, which both filters to shortest paths and forbids cycles. */
    private static void collectPaths(String curWord, String endWord, Map<String, Integer> level,
                                     Map<String, List<String>> adjacency,
                                     List<String> path, List<List<String>> result) {
        if (curWord.equals(endWord)) {
            result.add(new ArrayList<>(path));
            return;
        }
        List<String> nexts = adjacency.get(curWord);
        if (nexts == null) return;                              // last layer, never expanded

        for (String next : nexts) {
            // The "+ 1" must stay inline so the Integer on the left unboxes.
            if (level.get(next) == level.get(curWord) + 1) {
                path.add(next);
                collectPaths(next, endWord, level, adjacency, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    /* ========================= neighbour strategies ========================= */

    /** Generate candidates by replacing up to maxChange positions. O(L^maxChange * 25^maxChange). */
    static List<String> neighborsByMutation(String word, Set<String> dict, int maxChange) {
        Set<String> hits = new LinkedHashSet<>();
        char[] arr = word.toCharArray();

        for (int i = 0; i < arr.length; i++) {
            char oldI = arr[i];
            for (char ci = 'a'; ci <= 'z'; ci++) {
                if (ci == oldI) continue;
                arr[i] = ci;
                String one = new String(arr);
                if (dict.contains(one) && !one.equals(word)) hits.add(one);

                if (maxChange >= 2) {
                    for (int j = i + 1; j < arr.length; j++) {
                        char oldJ = arr[j];
                        for (char cj = 'a'; cj <= 'z'; cj++) {
                            if (cj == oldJ) continue;
                            arr[j] = cj;
                            String two = new String(arr);
                            if (dict.contains(two)) hits.add(two);
                        }
                        arr[j] = oldJ;
                    }
                }
            }
            arr[i] = oldI;
        }
        return new ArrayList<>(hits);
    }

    /** Compare against every dictionary entry. O(W * L), independent of alphabet and maxChange. */
    static List<String> neighborsByScan(String word, Collection<String> dict, int maxChange) {
        List<String> hits = new ArrayList<>();
        for (String other : dict) {
            int d = hamming(word, other);
            if (d >= 1 && d <= maxChange) hits.add(other);
        }
        return hits;
    }

    /** Number of differing positions, or Integer.MAX_VALUE for mismatched lengths. */
    static int hamming(String a, String b) {
        if (a.length() != b.length()) return Integer.MAX_VALUE;  // never neighbours
        int d = 0;
        for (int i = 0; i < a.length(); i++) if (a.charAt(i) != b.charAt(i)) d++;
        return d;
    }

    /* ================================ Tests ================================ */

    public static void main(String[] args) {

        List<String> classic = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");

        /* ------------------------- Part 1, the classic ------------------------- */
        // Paths are compared as a SORTED set: the spec fixes no order between
        // ladders, and the real order just reflects neighbour-generation order.
        expect("classic, 1 char", findLadders("hit", "cog", classic),
                "[[hit, hot, dot, dog, cog], [hit, hot, lot, log, cog]]");
        expect("endWord absent", findLadders("hit", "cog",
                Arrays.asList("hot", "dot", "dog", "lot", "log")), "[]");
        expect("single step", findLadders("a", "c", Arrays.asList("a", "b", "c")), "[[a, c]]");
        expect("no path at all", findLadders("aaa", "bbb",
                Arrays.asList("bbb")), "[]");
        expect("begin not in list is fine", findLadders("hit", "hot",
                Arrays.asList("hot")), "[[hit, hot]]");

        /* ------------- Part 2, where allowing 2 changes rewrites it ------------- */
        // cog is TWO steps away now: hit->{hot,dot,lot} are all within 2, and each
        // of those is within 2 of cog. Shorter than Part 1 AND more numerous.
        expect("classic, 1-or-2 chars", findLaddersOneOrTwo("hit", "cog", classic),
                "[[hit, dot, cog], [hit, hot, cog], [hit, lot, cog]]");
        expect("1-char words: parts agree", findLaddersOneOrTwo("a", "c",
                Arrays.asList("a", "b", "c")), "[[a, c]]");
        expect("2 changes make it direct", findLaddersOneOrTwo("aa", "bb",
                Arrays.asList("ab", "bb")), "[[aa, bb]]");
        expect("3 apart still needs a hop", findLaddersOneOrTwo("aaa", "bbb",
                Arrays.asList("abb", "bbb")), "[[aaa, abb, bbb]]");
        expect("endWord absent, part 2", findLaddersOneOrTwo("hit", "cog",
                Arrays.asList("hot", "dot")), "[]");

        // ⚠ Check the distance between begin and end FIRST. Here they are 2 apart,
        // so Part 2 goes straight there and the whole chain is irrelevant.
        List<String> chain = Arrays.asList("ab", "bb", "bc", "cc");
        expect("chain, 1 char", findLadders("aa", "cc", chain),
                "[[aa, ab, bb, bc, cc]]");
        expect("chain, 1-or-2 chars: one hop", findLaddersOneOrTwo("aa", "cc", chain),
                "[[aa, cc]]");

        // A chain long enough that Part 2 still needs intermediates: 4 apart, so
        // Part 1 walks all four rungs and Part 2 takes two double steps.
        List<String> longChain = Arrays.asList("aaab", "aabb", "abbb", "bbbb");
        expect("4-rung chain, 1 char", findLadders("aaaa", "bbbb", longChain),
                "[[aaaa, aaab, aabb, abbb, bbbb]]");
        expect("4-rung chain, 1-or-2 chars", findLaddersOneOrTwo("aaaa", "bbbb", longChain),
                "[[aaaa, aabb, bbbb]]");

        /* --------------------- the two strategies must agree -------------------- */
        Set<String> dict = new HashSet<>(classic);
        for (String w : Arrays.asList("hit", "hot", "cog", "dot", "zzz")) {
            for (int mc = 1; mc <= 2; mc++) {
                Set<String> byMut = new TreeSet<>(neighborsByMutation(w, dict, mc));
                Set<String> byScan = new TreeSet<>(neighborsByScan(w, dict, mc));
                expect("neighbours agree, \"" + w + "\" maxChange=" + mc, byMut, byScan);
            }
        }

        randomChecks(1);
        randomChecks(2);
        levelFilterIsTheVisitedCheck();
        boxingTrap();
        strategyCrossover();
        pathCountBlowup();
    }

    /* ---------------- randomised check against a brute-force oracle ---------------- */

    private static void randomChecks(int maxChange) {
        Random rnd = new Random(212 + maxChange);
        int trials = 20000, mismatch = 0, nonEmpty = 0, multi = 0;
        long totalPaths = 0;
        int longest = 0;

        for (int t = 0; t < trials; t++) {
            int len = 2 + rnd.nextInt(3);                       // words of length 2..4
            int alpha = 2 + rnd.nextInt(2);                      // alphabet of 2..3 letters
            int count = 1 + rnd.nextInt(8);

            Set<String> words = new LinkedHashSet<>();
            for (int i = 0; i < count; i++) words.add(randomWord(rnd, len, alpha));
            String begin = randomWord(rnd, len, alpha), end = randomWord(rnd, len, alpha);
            if (begin.equals(end)) continue;
            List<String> list = new ArrayList<>(words);

            // Both neighbour strategies are live code now, so check both every trial.
            List<List<String>> got = solve(begin, end, list, maxChange, false);
            List<List<String>> viaScan = solve(begin, end, list, maxChange, true);
            List<List<String>> want = bruteForceShortest(begin, end, list, maxChange);

            if (!sameSet(got, want) || !sameSet(viaScan, want)) {
                if (mismatch++ < 4)
                    System.out.println("MISMATCH maxChange=" + maxChange + " begin=" + begin
                            + " end=" + end + " words=" + list + "\n  mutation " + got
                            + "\n  scan     " + viaScan + "\n  brute    " + want);
            }
            if (!got.isEmpty()) {
                nonEmpty++;
                totalPaths += got.size();
                if (got.size() > 1) multi++;
                longest = Math.max(longest, got.get(0).size());
            }
        }
        System.out.printf("%nmaxChange=%d: %d random dictionaries, %d mismatches vs brute force%n",
                maxChange, trials, mismatch);
        System.out.printf("  coverage: %d had a path (%d with several), %d paths total, "
                + "longest ladder %d words%n", nonEmpty, multi, totalPaths, longest);
    }

    /** Distinct random words, giving up rather than spinning when the space is too small. */
    private static Set<String> distinctWords(Random rnd, int len, int want) {
        Set<String> out = new LinkedHashSet<>();
        for (int tries = 0; out.size() < want && tries < want * 200; tries++)
            out.add(randomWord(rnd, len, 26));
        return out;
    }

    private static String randomWord(Random rnd, int len, int alpha) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(alpha)));
        return sb.toString();
    }

    /**
     * Independent oracle, by a different method: iterative-deepening DFS. Ask for
     * paths of exactly 1 word, then 2, then 3 … and return the first depth that
     * yields any. No BFS, no levels, no adjacency map — it shares no logic with
     * the engine above, which is the point of having it.
     */
    static List<List<String>> bruteForceShortest(String begin, String end,
                                                 List<String> words, int maxChange) {
        for (int limit = 1; limit <= words.size() + 1; limit++) {
            List<List<String>> found = new ArrayList<>();
            Set<String> visited = new LinkedHashSet<>();
            visited.add(begin);
            List<String> path = new ArrayList<>();
            path.add(begin);
            enumerate(begin, end, words, maxChange, visited, path, found, limit);
            if (!found.isEmpty()) return found;
        }
        return new ArrayList<>();
    }

    private static void enumerate(String cur, String end, List<String> words, int maxChange,
                                  Set<String> visited, List<String> path,
                                  List<List<String>> all, int limit) {
        if (cur.equals(end)) {
            if (path.size() == limit) all.add(new ArrayList<>(path));
            return;                                   // no shortest path passes through end
        }
        if (path.size() >= limit) return;
        for (String next : words) {
            if (visited.contains(next)) continue;
            int d = hamming(cur, next);
            if (d < 1 || d > maxChange) continue;
            visited.add(next);
            path.add(next);
            enumerate(next, end, words, maxChange, visited, path, all, limit);
            path.remove(path.size() - 1);
            visited.remove(next);
        }
    }

    private static boolean sameSet(List<List<String>> a, List<List<String>> b) {
        return new HashSet<>(a).equals(new HashSet<>(b)) && a.size() == b.size();
    }

    /* ------------------- the claims in the header, made runnable ------------------- */

    /** Shows that dropping the level filter loses termination, not just optimality. */
    private static void levelFilterIsTheVisitedCheck() {
        List<String> classic = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        Set<String> dict = new HashSet<>(classic);
        Map<String, List<String>> adjacency = new HashMap<>();
        Map<String, Integer> level = new HashMap<>();
        bfsLevels("hit", "cog", dict, adjacency, level, 1, false);

        List<List<String>> result = new ArrayList<>();
        List<String> path = new ArrayList<>();
        path.add("hit");
        String outcome;
        try {
            collectPathsNoFilter("hit", "cog", adjacency, path, result, 0);
            outcome = result.size() + " paths";
        } catch (StackOverflowError e) {
            outcome = "StackOverflowError";
        } catch (IllegalStateException e) {
            outcome = e.getMessage();
        }
        System.out.printf("%nlevel filter removed: %s   (with the filter: %d paths)%n",
                outcome, findLadders("hit", "cog", classic).size());
    }

    private static void collectPathsNoFilter(String cur, String end,
                                             Map<String, List<String>> adjacency,
                                             List<String> path, List<List<String>> result, int depth) {
        if (depth > 100000) throw new IllegalStateException("runaway DFS, no visited check");
        if (cur.equals(end)) { result.add(new ArrayList<>(path)); return; }
        List<String> nexts = adjacency.get(cur);
        if (nexts == null) return;
        for (String next : nexts) {                          // no level check at all
            path.add(next);
            collectPathsNoFilter(next, end, adjacency, path, result, depth + 1);
            path.remove(path.size() - 1);
        }
    }

    /** Shows why the "+ 1" has to stay inline. */
    private static void boxingTrap() {
        Map<String, Integer> level = new HashMap<>();
        level.put("lo", 200);  level.put("hi", 201);
        level.put("lo2", 100); level.put("hi2", 101);

        Integer hoisted = level.get("lo") + 1;
        Integer hoistedSmall = level.get("lo2") + 1;
        System.out.printf("boxing: inline +1 at level 201 -> %b (correct); "
                        + "hoisted into an Integer -> %b (WRONG); same hoist at 101 -> %b%n",
                level.get("hi") == level.get("lo") + 1,
                level.get("hi") == hoisted,
                level.get("hi2") == hoistedSmall);

        // The same thing inside the real algorithm, at levels past the cache. This
        // has to ASSERT, not print: hoisting the + 1 leaves every other test green.
        int len = 150;
        List<String> chain = new ArrayList<>();
        for (int i = 1; i <= len; i++) chain.add("b".repeat(i) + "a".repeat(len - i));
        List<List<String>> r = findLadders("a".repeat(len), "b".repeat(len), chain);
        expect("levels far past the Integer cache (" + len + "-long chain)",
                r.size() + " path of " + (r.isEmpty() ? 0 : r.get(0).size()) + " words",
                "1 path of " + (len + 1) + " words");
    }

    /** Where scan overtakes mutation, and what rebuilding the dict set costs. */
    private static void strategyCrossover() {
        System.out.printf("%n%-10s %-6s %-8s %-16s %-16s %s%n",
                "maxChange", "L", "W", "mutation (ms)", "scan (ms)", "faster");
        for (int mc = 1; mc <= 2; mc++) {
            for (int L : new int[]{3, 5, 10}) {
                for (int W : new int[]{200, 5000}) {
                    Set<String> dict = distinctWords(new Random(7), L, W);
                    List<String> probes = new ArrayList<>(dict).subList(0, Math.min(200, dict.size()));

                    for (String p : probes) { neighborsByMutation(p, dict, mc); neighborsByScan(p, dict, mc); }

                    long t0 = System.nanoTime();
                    for (String p : probes) neighborsByMutation(p, dict, mc);
                    double mut = (System.nanoTime() - t0) / 1e6;

                    long t1 = System.nanoTime();
                    for (String p : probes) neighborsByScan(p, dict, mc);
                    double scan = (System.nanoTime() - t1) / 1e6;

                    System.out.printf("%-10d %-6d %-8d %-16.2f %-16.2f %s%n", mc, L, dict.size(),
                            mut, scan, mut < scan ? "mutation" : "scan");
                }
            }
        }

        // the "rebuild the set every call" mistake
        List<String> big = new ArrayList<>(distinctWords(new Random(11), 5, 3000));
        Set<String> once = new HashSet<>(big);
        List<String> probes = big.subList(0, 200);

        long t0 = System.nanoTime();
        for (String p : probes) neighborsByMutation(p, once, 1);
        double shared = (System.nanoTime() - t0) / 1e6;

        long t1 = System.nanoTime();
        for (String p : probes) neighborsByMutation(p, new HashSet<>(big), 1);
        double perCall = (System.nanoTime() - t1) / 1e6;

        System.out.printf("dictionary set: built once %.2f ms vs rebuilt per word %.2f ms  (%.0fx)%n",
                shared, perCall, perCall / shared);
    }

    /** The answer itself can be exponential, so no algorithm avoids this. */
    private static void pathCountBlowup() {
        System.out.printf("%n%-8s %-12s %s%n", "L", "shortest", "number of shortest ladders");
        for (int L = 2; L <= 7; L++) {
            // every word of length L over {a,b}: begin = all a, end = all b,
            // so every ordering of the L flips is a distinct shortest ladder -> L!
            List<String> words = new ArrayList<>();
            for (int mask = 0; mask < (1 << L); mask++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < L; i++) sb.append((mask >> i & 1) == 1 ? 'b' : 'a');
                words.add(sb.toString());
            }
            List<List<String>> r = findLadders("a".repeat(L), "b".repeat(L), words);
            System.out.printf("%-8d %-12d %d%n", L, r.isEmpty() ? 0 : r.get(0).size(), r.size());
        }
    }

    /* ------------------------------- helpers ------------------------------- */

    private static void expect(String label, Object got, Object expected) {
        String g = canonical(got), e = canonical(expected);
        boolean ok = g.equals(e);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + g + "\n  expected=" + e));
    }

    /** Sorts a list of ladders so tests never pin neighbour-generation order. */
    private static String canonical(Object o) {
        if (!(o instanceof List<?> outer) || outer.isEmpty() || !(outer.get(0) instanceof List))
            return String.valueOf(o);
        List<String> rows = new ArrayList<>();
        for (Object row : outer) rows.add(String.valueOf(row));
        Collections.sort(rows);
        return rows.toString();
    }
}
