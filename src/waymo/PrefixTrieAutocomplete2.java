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
import java.util.*;

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
public class PrefixTrieAutocomplete2 {
    class TrieNode {
        String word;
        TrieNode[] children;

        public TrieNode() {
            this.children = new TrieNode[26];
        }
    }

    TrieNode root;
    public PrefixTrieAutocomplete2() {
        this.root = new TrieNode();
    }

    public void build(List<String> words) {
        for (String w : words) {
            insert(w);
        }
    }

    public void insert(String w) {
        TrieNode node = root;
        for (char c : w.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.word = w;
    }

    /** All words that begin with {@code prefix}.  Empty prefix returns every inserted word. */
    public List<String> query(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                return result;
            }
            node = node.children[index];
        }
        dfsFindAll(node, result);
        return result;
    }

    private void dfsFindAll(TrieNode node, List<String> result) {
        if (node == null) {
            return;
        }
        if (node.word != null) {
            result.add(node.word);
        }
        for (TrieNode child : node.children) {
            if (child != null) {
                dfsFindAll(child, result);
            }
        }
    }
}
