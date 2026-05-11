package waymo;
/*
Problem: Prefix Query (Trie Autocomplete)

Given a list of words (lowercase a-z only), build a data structure that supports:
  - build(words): insert all words into the structure.
  - query(prefix): return every inserted word that starts with prefix.

Constraints
  1 <= n         <= 2 * 10^5
  1 <= len(word) <= 30
  1 <= Q         <= 2 * 10^5
  0 <= len(prefix) <= 30                  (empty prefix returns every word)
  All characters in a..z.
  Must be efficient for many queries; cannot scan all words per query.

Sample I/O
  Input
    5
    apple
    app
    ape
    bat
    bath
    3
    ap
    app
    ba
  Output (order is not required; one of many valid renderings)
    ape app apple
    app apple
    bat bath
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
Algorithm: classic 26-ary trie (prefix tree).

  Insert (per word):
    Walk from root, lazily allocate a child for each character.  At the
    final node store the FULL word so queries can hand back references
    without rebuilding strings on the path.  O(L) per word.

  Query (per prefix):
    Walk down the prefix.  If we ever fall off the trie, return empty.
    Otherwise DFS the subtree rooted at the prefix node and emit every
    stored word.  O(|prefix|) to descend, then O(R) to enumerate the R
    matching words — no extra string concatenation needed because we
    already stashed each word at its terminal node.

  Why store the word at the end node:
    Without it, each emitted match costs O(L) to rebuild from the DFS
    path.  With it, each match is a single reference copy, so query
    cost is proportional to the answer size, not to |prefix| * matches.

Memory:
  At most one trie node per distinct (word, prefix) cell, so the node
  count is bounded by Σ |word_i|.  Each node carries a 26-slot child
  array (~104 B on a 64-bit JVM with compressed oops) plus a String
  reference at terminal nodes.  At the spec limit (200K words × 30 chars
  → 6M nodes worst case) this is ~700 MB of node objects, but shared
  prefixes in real workloads typically bring this down by 10–100×.  If
  memory is the concern, swap `Node.children` for a `HashMap` (lazy,
  smaller per node) — same algorithm, slower constant.

Complexity
  build:  O(Σ |word_i|)
  query:  O(|prefix| + total chars in answer)        // see "store the word" note
  space:  O(# distinct trie nodes) ≤ O(Σ |word_i|)
*/
public class PrefixTrieAutocomplete {

    private static final int ALPHA = 26;

    private static final class Node {
        Node[] children = new Node[ALPHA];
        /** non-null only at the terminal node of an inserted word — holds the full word. */
        String word;
    }

    private final Node root = new Node();
    private int size;

    /* --------------------------- Public API --------------------------- */

    public void build(List<String> words) {
        for (String w : words) insert(w);
    }

    public void insert(String w) {
        if (w == null) throw new IllegalArgumentException("null word");
        Node cur = root;
        for (int i = 0; i < w.length(); i++) {
            int c = w.charAt(i) - 'a';
            if (c < 0 || c >= ALPHA) {
                throw new IllegalArgumentException("non-lowercase char in '" + w + "'");
            }
            if (cur.children[c] == null) cur.children[c] = new Node();
            cur = cur.children[c];
        }
        if (cur.word == null) size++;
        cur.word = w;          // duplicate inserts overwrite harmlessly
    }

    /** All words that begin with {@code prefix}.  Empty prefix returns every inserted word. */
    public List<String> query(String prefix) {
        if (prefix == null) return Collections.emptyList();
        Node cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            int c = prefix.charAt(i) - 'a';
            if (c < 0 || c >= ALPHA) return Collections.emptyList();
            cur = cur.children[c];
            if (cur == null) return Collections.emptyList();
        }
        List<String> out = new ArrayList<>();
        collect(cur, out);
        return out;
    }

    /** Total number of distinct inserted words. */
    public int size() {
        return size;
    }

    /* --------------------------- Internals --------------------------- */

    private static void collect(Node node, List<String> out) {
        if (node.word != null) out.add(node.word);
        for (Node child : node.children) {
            if (child != null) collect(child, out);
        }
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());
        PrefixTrieAutocomplete trie = new PrefixTrieAutocomplete();
        for (int i = 0; i < n; i++) trie.insert(br.readLine());
        int q = Integer.parseInt(br.readLine().trim());

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < q; i++) {
            String prefix = br.readLine();
            if (prefix == null) prefix = "";
            List<String> matches = trie.query(prefix);
            for (int j = 0; j < matches.size(); j++) {
                if (j > 0) out.append(' ');
                out.append(matches.get(j));
            }
            out.append('\n');
        }
        System.out.print(out);
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        // Spec example.
        PrefixTrieAutocomplete trie = new PrefixTrieAutocomplete();
        trie.build(Arrays.asList("apple", "app", "ape", "bat", "bath"));
        runQuery(trie, "ap",  new String[]{"apple", "app", "ape"});
        runQuery(trie, "app", new String[]{"apple", "app"});
        runQuery(trie, "ba",  new String[]{"bat", "bath"});
        runQuery(trie, "",    new String[]{"apple", "app", "ape", "bat", "bath"});  // empty prefix = all
        runQuery(trie, "z",   new String[]{});                                       // no match
        runQuery(trie, "appz",new String[]{});                                       // prefix runs off the trie

        // Duplicates don't inflate the size or duplicate results.
        PrefixTrieAutocomplete dup = new PrefixTrieAutocomplete();
        dup.insert("apple");
        dup.insert("apple");
        dup.insert("apple");
        runQuery(dup, "ap", new String[]{"apple"});
        System.out.println("size after 3x apple = " + dup.size() + " (expected 1)");

        // Single-character words and 1-char prefix.
        PrefixTrieAutocomplete tiny = new PrefixTrieAutocomplete();
        tiny.build(Arrays.asList("a", "ab", "abc"));
        runQuery(tiny, "a",   new String[]{"a", "ab", "abc"});
        runQuery(tiny, "ab",  new String[]{"ab", "abc"});
        runQuery(tiny, "abc", new String[]{"abc"});
        runQuery(tiny, "abcd", new String[]{});

        // Cross-check against brute on random small inputs.
        Random rnd = new Random(11);
        int mismatches = 0;
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(20);
            List<String> words = new ArrayList<>();
            for (int i = 0; i < n; i++) words.add(randomWord(rnd, 1 + rnd.nextInt(6), 3));
            PrefixTrieAutocomplete tt = new PrefixTrieAutocomplete();
            tt.build(words);
            // Try 10 random prefixes per build.
            for (int q = 0; q < 10; q++) {
                String prefix = randomWord(rnd, rnd.nextInt(5), 3);
                Set<String> got = new HashSet<>(tt.query(prefix));
                Set<String> ref = bruteQuery(words, prefix);
                if (!got.equals(ref)) {
                    mismatches++;
                    System.out.println("MISMATCH words=" + words + " prefix='" + prefix + "'");
                    System.out.println("  got=" + got + " ref=" + ref);
                }
            }
        }
        System.out.println("Random cross-check: " + (1000 - mismatches) + "/1000 ok");

        // Performance: 200K random words, then a mix of query lengths.  Per-query cost is
        // O(|prefix| + #matches) so short prefixes naturally do more work.
        int N = 200_000, Q = 200_000;
        Random big = new Random(3);
        PrefixTrieAutocomplete perf = new PrefixTrieAutocomplete();
        long t0 = System.nanoTime();
        for (int i = 0; i < N; i++) perf.insert(randomWord(big, 4 + big.nextInt(8), 26));
        long buildMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("build " + N + " words: " + buildMs + " ms (size=" + perf.size() + ")");

        // Selective queries (prefix length 4..6).  Most return 0–few matches.
        long totalMatches = 0;
        t0 = System.nanoTime();
        for (int i = 0; i < Q; i++) {
            totalMatches += perf.query(randomWord(big, 4 + big.nextInt(3), 26)).size();
        }
        long qMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("query " + Q + " selective prefixes (len 4-6): "
                + qMs + " ms (total matches=" + totalMatches + ")");

        // Broad queries (prefix length 1..2).  Each match is a single ref copy, but the
        // answers are enormous — the cost is dominated by emitting matches, not by trie
        // traversal.  Run a smaller batch so the output stays readable.
        int Q2 = 5_000;
        totalMatches = 0;
        t0 = System.nanoTime();
        for (int i = 0; i < Q2; i++) {
            totalMatches += perf.query(randomWord(big, 1 + big.nextInt(2), 26)).size();
        }
        qMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("query " + Q2 + " broad prefixes (len 1-2):  "
                + qMs + " ms (total matches=" + totalMatches + ")");
    }

    private static void runQuery(PrefixTrieAutocomplete trie, String prefix, String[] expectedArr) {
        List<String> got = trie.query(prefix);
        Set<String> gotSet = new HashSet<>(got);
        Set<String> expectedSet = new HashSet<>(Arrays.asList(expectedArr));
        boolean ok = gotSet.equals(expectedSet);
        System.out.println((ok ? "OK   " : "FAIL ")
                + "query('" + prefix + "') → " + got + "  (expected " + expectedSet + ")");
    }

    private static String randomWord(Random rnd, int len, int alpha) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(alpha)));
        return sb.toString();
    }

    private static Set<String> bruteQuery(List<String> words, String prefix) {
        Set<String> out = new HashSet<>();
        for (String w : words) if (w.startsWith(prefix)) out.add(w);
        return out;
    }
}
