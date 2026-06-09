package airbnb.New2026;
/*
LeetCode 269: Alien Dictionary.

Given a list of words written in an unknown alphabet that is sorted
lexicographically by the rules of that alphabet, return ANY string that
represents a valid order of the alphabet.  Return "" if no valid order
exists.

Examples
  ["wrt","wrf","er","ett","rftt"]   -> "wertf"
  ["z","x"]                          -> "zx"
  ["z","x","z"]                      -> ""        (cycle)
  ["abc","ab"]                       -> ""        (longer prefix before shorter)

Approach -- topological sort over single characters

  1. Collect every character that appears anywhere; each starts at in-degree 0.
  2. For each adjacent pair (w1, w2):
       - find the first differing position j
       - add edge w1[j] -> w2[j]
       - if no differing position is found AND w1 is longer than w2 -> "".
  3. Kahn's BFS: enqueue all in-degree-0 chars; pop, append to order, drop
     edges, enqueue any newly-zero successors.
  4. If the resulting order contains every character we saw, return it.
     Otherwise the graph has a cycle and we return "".

Edge cases the code must handle
  E1. Single word: no constraints, return any permutation of its letters.
       (Tests below assert "z" -> "z".  We rely on insertion order being
       stable since we initialize the in-degree map in word order, but the
       LC grader accepts any permutation.)
  E2. Duplicate adjacent words ("ab","ab"): no info, no edge.
  E3. Equal prefix ("ab","abc"): no edge, valid.
  E4. Prefix violation ("abc","ab"): impossible -> "".
  E5. Same-letter words only ("aaa","aa"): also a prefix violation.
  E6. Disconnected letters ("z","x"): both start at degree 0, both leave
       the queue in the order we pushed them.

Why we add ONLY the first differing edge per pair
  The lex sort tells us nothing about characters past the first mismatch.
  ("wrt" < "wrf" tells us t < f; it tells us nothing about r vs r or w vs w
  that we didn't already know from elsewhere.)  Adding spurious edges past
  the mismatch can both invent fake cycles AND inflate in-degrees so a
  valid order is rejected.

Complexity
  Let C = total characters across all words, k = distinct characters (<= 26).
  Time:   O(C)        for building the graph (each pair scanned once).
  Memory: O(k + E)    E <= k(k-1)/2 -> bounded by 26^2 in practice.
*/

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AlienDictionary {

    public static String alienOrder(String[] words) {
        // LinkedHashMap so the iteration over "in-degree-0 starters" is
        // deterministic for the tests below; correctness does not depend on it.
        Map<Character, Set<Character>> graph = new LinkedHashMap<>();
        Map<Character, Integer> inDeg = new LinkedHashMap<>();

        // 1. Seed every character with empty adjacency and zero in-degree.
        for (String w : words) {
            for (char ch : w.toCharArray()) {
                graph.putIfAbsent(ch, new HashSet<>());
                inDeg.putIfAbsent(ch, 0);
            }
        }

        // 2. One edge per adjacent pair, at the first differing position.
        for (int i = 0; i + 1 < words.length; i++) {
            String a = words[i], b = words[i + 1];
            int j = 0;
            while (j < a.length() && j < b.length() && a.charAt(j) == b.charAt(j)) j++;

            if (j == a.length()) {
                // a is a prefix of b (or equal); no constraint, valid.
                continue;
            }
            if (j == b.length()) {
                // b is a strict prefix of a -- impossible under lex order.
                return "";
            }
            char from = a.charAt(j), to = b.charAt(j);
            // Skip duplicate edges so we don't double-count in-degree.
            if (graph.get(from).add(to)) {
                inDeg.merge(to, 1, Integer::sum);
            }
        }

        // 3. Kahn's algorithm.
        Deque<Character> queue = new ArrayDeque<>();
        for (Map.Entry<Character, Integer> e : inDeg.entrySet()) {
            if (e.getValue() == 0) queue.add(e.getKey());
        }
        StringBuilder order = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            order.append(c);
            for (char nxt : graph.get(c)) {
                int v = inDeg.get(nxt) - 1;
                inDeg.put(nxt, v);
                if (v == 0) queue.add(nxt);
            }
        }

        // 4. If we couldn't drain every character, there's a cycle.
        return order.length() == inDeg.size() ? order.toString() : "";
    }

    /* ------------------------------ tests ------------------------------ */

    public static void main(String[] args) {
        // Canonical LC example.  "wertf" is the deterministic output of this
        // implementation; the LC grader accepts any valid order.
        check("LC standard", new String[]{"wrt","wrf","er","ett","rftt"}, "wertf");

        check("two-word",         new String[]{"z","x"},                 "zx");
        check("cycle",            new String[]{"z","x","z"},             "");
        check("prefix violation", new String[]{"abc","ab"},              "");
        check("equal prefix ok",  new String[]{"ab","abc"},              "abc");
        check("duplicate words",  new String[]{"ab","ab"},               "ab");
        check("single word",      new String[]{"abc"},                   "abc");
        check("disjoint chars",   new String[]{"a","b","c"},             "abc");
        check("same-letter prefix violation",
                                  new String[]{"aaa","aa"},              "");

        // Validate every passing test result instead of pinning specific strings.
        // Returns true iff `out` is a permutation of all distinct chars in `words`
        // AND respects every adjacent-pair constraint.
        String[][] cases = {
                {"wrt","wrf","er","ett","rftt"},
                {"z","x"},
                {"ab","abc"},
                {"ab","ab"},
                {"abc"},
                {"a","b","c"},
        };
        for (String[] words : cases) {
            String out = alienOrder(words);
            String label = "validate(" + String.join(",", words) + ")";
            System.out.println((isValidOrder(words, out) ? "OK    " : "FAIL  ")
                    + label + " -> " + out);
        }
    }

    /* ------------------------------ helpers ------------------------------ */

    private static void check(String label, String[] words, String expected) {
        String got = alienOrder(words);
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK    " : "FAIL  ") + label
                + " words=" + java.util.Arrays.toString(words)
                + " got=" + got + (ok ? "" : " expected=" + expected));
    }

    /** Permutation of all distinct chars AND respects every adjacent-pair edge. */
    private static boolean isValidOrder(String[] words, String order) {
        Set<Character> chars = new HashSet<>();
        for (String w : words) for (char c : w.toCharArray()) chars.add(c);
        if (order.length() != chars.size()) return false;
        Map<Character, Integer> rank = new HashMap<>();
        for (int i = 0; i < order.length(); i++) rank.put(order.charAt(i), i);
        if (!rank.keySet().equals(chars)) return false;

        for (int i = 0; i + 1 < words.length; i++) {
            String a = words[i], b = words[i + 1];
            int j = 0;
            while (j < a.length() && j < b.length() && a.charAt(j) == b.charAt(j)) j++;
            if (j == a.length()) continue;
            if (j == b.length()) return false;          // prefix violation
            if (rank.get(a.charAt(j)) >= rank.get(b.charAt(j))) return false;
        }
        return true;
    }
}
