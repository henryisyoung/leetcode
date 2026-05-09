package snowflake.mianjing;

/*
Snowflake mianjing — Document Predicate Search

Implement two APIs:
  1. InsertDoc(filename, content)
       Tokenize content into words and index it under filename.
       (Original problem says InsertDoc(filename); we take content as a
       String parameter to keep the example self-contained — no real I/O.)

  2. CheckContains(filename, predicate)
       Return true iff the file's words satisfy the boolean predicate.
       Predicate syntax supports word literals, &&, ||, and parentheses.
       Examples:
         "a || b || c"         → does the file contain a OR b OR c?
         "a && b || c"         → (a AND b) OR c   (&& binds tighter than ||)
         "(a || b) && c"       → standard parenthesization

Follow-up 1:
  3. GetAllFiles(predicate)
       Return every indexed filename whose contents satisfy the predicate.
       Should not be O(num_files * predicate_eval) — use the inverted index.

Follow-up 2:
  Distributed sharding + replication design — discussed in the long
  comment near the bottom of this file.

Implementation summary
----------------------
- forward index : filename -> Set<word>      (powers CheckContains)
- inverted index: word     -> Set<filename>  (powers GetAllFiles)

- Predicate parser is a tiny recursive-descent parser over the token
  stream. Operator precedence: NOT > AND (&&) > OR (||).

  expr   := term ('||' term)*
  term   := factor ('&&' factor)*
  factor := WORD | '(' expr ')' | '!' factor
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocPredicateSearch {

    /* ---------------------------- Public API ---------------------------- */

    private final Map<String, Set<String>> forward = new HashMap<>();
    private final Map<String, Set<String>> inverted = new HashMap<>();

    /** Index a document. `content` is tokenized into words on whitespace/punct. */
    public void insertDoc(String filename, String content) {
        Set<String> words = tokenizeWords(content);

        // If we're re-indexing, drop the old postings first.
        Set<String> previous = forward.put(filename, words);
        if (previous != null) {
            for (String w : previous) {
                Set<String> postings = inverted.get(w);
                if (postings != null) {
                    postings.remove(filename);
                    if (postings.isEmpty()) inverted.remove(w);
                }
            }
        }
        for (String w : words) {
            inverted.computeIfAbsent(w, k -> new HashSet<>()).add(filename);
        }
    }

    /** Does this file's contents satisfy the predicate? */
    public boolean checkContains(String filename, String predicate) {
        Set<String> words = forward.get(filename);
        if (words == null) {
            return false;
        }
        return Parser.parse(predicate).eval(words);
    }

    /** Follow-up 1: every indexed file whose contents satisfy the predicate. */
    public Set<String> getAllFiles(String predicate) {
        return Parser.parse(predicate).matchAll(this);
    }

    /* --------------------------- Tokenization --------------------------- */

    /** Split content into a set of lowercase words ([A-Za-z0-9_]+). */
    private static Set<String> tokenizeWords(String content) {
        Set<String> out = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(Character.toLowerCase(c));
            } else if (sb.length() > 0) {
                out.add(sb.toString());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) out.add(sb.toString());
        return out;
    }

    /* ---------------------- Predicate AST + Evaluator ------------------- */

    /**
     * Each AST node knows two things:
     *   eval(words)       — true/false for one document's word set.
     *   matchAll(engine)  — set of all filenames matching, computed via
     *                       the inverted index without scanning every doc.
     */
    private interface Pred {
        boolean eval(Set<String> words);
        Set<String> matchAll(DocPredicateSearch engine);
    }

    private static final class Lit implements Pred {
        final String word;
        Lit(String word) { this.word = word; }

        @Override public boolean eval(Set<String> words) {
            return words.contains(word);
        }
        @Override public Set<String> matchAll(DocPredicateSearch engine) {
            Set<String> postings = engine.inverted.get(word);
            return postings == null ? Collections.emptySet() : new HashSet<>(postings);
        }
    }

    private static final class And implements Pred {
        final Pred l, r;
        And(Pred l, Pred r) { this.l = l; this.r = r; }

        @Override public boolean eval(Set<String> words) {
            return l.eval(words) && r.eval(words);
        }
        @Override public Set<String> matchAll(DocPredicateSearch engine) {
            Set<String> a = l.matchAll(engine);
            Set<String> b = r.matchAll(engine);
            // Iterate the smaller set for cheaper intersection.
            if (a.size() > b.size()) { Set<String> t = a; a = b; b = t; }
            Set<String> out = new HashSet<>(Math.min(a.size(), b.size()));
            for (String f : a) if (b.contains(f)) out.add(f);
            return out;
        }
    }

    private static final class Or implements Pred {
        final Pred l, r;
        Or(Pred l, Pred r) { this.l = l; this.r = r; }

        @Override public boolean eval(Set<String> words) {
            return l.eval(words) || r.eval(words);
        }
        @Override public Set<String> matchAll(DocPredicateSearch engine) {
            Set<String> out = new HashSet<>(l.matchAll(engine));
            out.addAll(r.matchAll(engine));
            return out;
        }
    }

    private static final class Not implements Pred {
        final Pred inner;
        Not(Pred inner) { this.inner = inner; }

        @Override public boolean eval(Set<String> words) {
            return !inner.eval(words);
        }
        @Override public Set<String> matchAll(DocPredicateSearch engine) {
            // NOT against the corpus: all files - inner.matchAll
            Set<String> out = new HashSet<>(engine.forward.keySet());
            out.removeAll(inner.matchAll(engine));
            return out;
        }
    }

    /* ----------------------- Predicate Parser --------------------------- */

    /** Recursive-descent parser for: word, !, &&, ||, parens. */
    private static final class Parser {
        private final List<String> tokens;
        private int pos = 0;

        private Parser(List<String> tokens) { this.tokens = tokens; }

        static Pred parse(String src) {
            Parser p = new Parser(tokenize(src));
            Pred result = p.parseOr();
            if (p.pos != p.tokens.size()) {
                throw new IllegalArgumentException("Unexpected token: " + p.peek());
            }
            return result;
        }

        private Pred parseOr() {
            Pred left = parseAnd();
            while (accept("||")) {
                left = new Or(left, parseAnd());
            }
            return left;
        }

        private Pred parseAnd() {
            Pred left = parseFactor();
            while (accept("&&")) {
                left = new And(left, parseFactor());
            }
            return left;
        }

        private Pred parseFactor() {
            if (accept("!")) {
                return new Not(parseFactor());
            }
            if (accept("(")) {
                Pred inner = parseOr();
                expect(")");
                return inner;
            }
            String w = consume();
            if (w == null || isOp(w)) {
                throw new IllegalArgumentException("Expected word, got: " + w);
            }
            return new Lit(w.toLowerCase());
        }

        /* token helpers */

        private String peek() { return pos < tokens.size() ? tokens.get(pos) : null; }
        private String consume() { return pos < tokens.size() ? tokens.get(pos++) : null; }
        private boolean accept(String t) {
            if (t.equals(peek())) { pos++; return true; }
            return false;
        }
        private void expect(String t) {
            if (!accept(t)) throw new IllegalArgumentException("Expected '" + t + "' got " + peek());
        }
        private static boolean isOp(String s) {
            return s.equals("&&") || s.equals("||") || s.equals("!") || s.equals("(") || s.equals(")");
        }

        /** Tokenize on whitespace + operators &&, ||, !, (, ). Words = letters/digits/_. */
        private static List<String> tokenize(String src) {
            List<String> out = new ArrayList<>();
            int i = 0, n = src.length();
            while (i < n) {
                char c = src.charAt(i);
                if (Character.isWhitespace(c)) { i++; continue; }
                if (c == '&' && i + 1 < n && src.charAt(i + 1) == '&') { out.add("&&"); i += 2; continue; }
                if (c == '|' && i + 1 < n && src.charAt(i + 1) == '|') { out.add("||"); i += 2; continue; }
                if (c == '(' || c == ')' || c == '!') { out.add(String.valueOf(c)); i++; continue; }
                if (Character.isLetterOrDigit(c) || c == '_') {
                    int j = i;
                    while (j < n && (Character.isLetterOrDigit(src.charAt(j)) || src.charAt(j) == '_')) j++;
                    out.add(src.substring(i, j));
                    i = j;
                    continue;
                }
                throw new IllegalArgumentException("Unexpected character: " + c);
            }
            return out;
        }
    }

    /* ------------------------------ Demo -------------------------------- */

    public static void main(String[] args) {
        DocPredicateSearch s = new DocPredicateSearch();
        s.insertDoc("doc1.txt", "apple banana cherry");
        s.insertDoc("doc2.txt", "banana date fig");
        s.insertDoc("doc3.txt", "apple cherry");
        s.insertDoc("doc4.txt", "fig grape");

        // ----- CheckContains -----
        // "a || b || c"  with literal words apple/banana/cherry
        check(true,  s.checkContains("doc1.txt", "apple || banana || cherry"));
        check(true,  s.checkContains("doc2.txt", "apple || banana || cherry"));
        check(false, s.checkContains("doc4.txt", "apple || banana || cherry"));

        // "a && b || c" === (a && b) || c
        check(true,  s.checkContains("doc1.txt", "apple && banana || date"));   // apple&&banana
        check(true,  s.checkContains("doc2.txt", "apple && banana || date"));   // date
        check(false, s.checkContains("doc4.txt", "apple && banana || date"));

        // parens override precedence
        check(false, s.checkContains("doc2.txt", "(apple || cherry) && date")); // doc2 has no apple/cherry
        check(true,  s.checkContains("doc1.txt", "(apple || cherry) && banana"));

        // negation
        check(true,  s.checkContains("doc4.txt", "!apple"));
        check(false, s.checkContains("doc1.txt", "!apple"));

        // missing file
        check(false, s.checkContains("nope.txt", "apple"));

        // ----- GetAllFiles -----
        System.out.println("apple || date         → " + sorted(s.getAllFiles("apple || date")));
        System.out.println("banana && cherry      → " + sorted(s.getAllFiles("banana && cherry")));
        System.out.println("(apple || fig) && !date → " + sorted(s.getAllFiles("(apple || fig) && !date")));
        // expected:
        //   apple || date         → [doc1.txt, doc2.txt, doc3.txt]
        //   banana && cherry      → [doc1.txt]
        //   (apple || fig) && !date → [doc1.txt, doc3.txt, doc4.txt]
    }

    private static void check(boolean expected, boolean actual) {
        if (expected != actual) throw new AssertionError("expected " + expected + " got " + actual);
        System.out.println(actual);
    }
    private static List<String> sorted(Set<String> s) {
        List<String> l = new ArrayList<>(s);
        Collections.sort(l);
        return l;
    }
}

/*
==============================================================================
Follow-up 2 — Distributed System: Sharding & Replication
==============================================================================

The single-machine design above has two indexes:
  forward  : filename -> Set<word>
  inverted : word     -> Set<filename>

When the corpus grows past one machine, both indexes must be partitioned.
There are two natural sharding strategies — they are NOT equivalent.

------------------------------------------------------------------------------
Strategy A — Document-partitioned (a.k.a. "local index", a.k.a. shard by file)
------------------------------------------------------------------------------
  Hash(filename) % N  picks the shard that owns the document AND its inverted
  posting lists for that document only.

  Each shard stores:
    forward[ files it owns ]
    inverted[ word ] = files-it-owns containing word

  InsertDoc(filename):
    coordinator → shard = hash(filename) % N
    shard updates its local forward + inverted index.

  CheckContains(filename, predicate):
    coordinator → shard = hash(filename) % N
    shard evaluates predicate against its forward index.       O(predicate)

  GetAllFiles(predicate):
    coordinator → fan-out to ALL shards (scatter-gather)
    each shard returns its locally matching filenames.
    coordinator unions the results.

  Pros:
    - Inserts hit ONE shard. Cheap, no cross-shard coordination.
    - CheckContains is one shard — O(1) routing.
    - Scales linearly with corpus size.
  Cons:
    - GetAllFiles fans out to every shard. Tail latency = slowest shard.
    - Doesn't reduce total work, just parallelizes it.

  This is what Elasticsearch and MongoDB do by default. Good for write-heavy
  systems and for queries where each shard returns small results.

------------------------------------------------------------------------------
Strategy B — Term-partitioned (a.k.a. "global index", a.k.a. shard by word)
------------------------------------------------------------------------------
  Hash(word) % N  picks the shard that owns the global posting list for
  that word.

  Each shard stores:
    inverted[ words it owns ] = ALL files (across whole corpus) with that word

  InsertDoc(filename):
    read words from file (on the coordinator or a worker).
    for each distinct word w:
       send "add filename to inverted[w]" to shard = hash(w) % N
    → an insert touches as many shards as the doc has distinct words.

  CheckContains(filename, predicate):
    Two options:
      (a) maintain a separate forward-index shard set keyed by filename
          (effectively layering Strategy A's forward index on top).
      (b) for each word in predicate, query its shard's posting list and
          ask "does it contain filename?" — wasteful for large predicates.
    In practice you keep both: term-partitioned inverted, doc-partitioned
    forward.

  GetAllFiles(predicate):
    parse predicate → set of leaf words.
    for each leaf word, fetch posting list from its single owning shard.
    intersect/union locally on the coordinator.
    → reads only the shards holding the words in the query.

  Pros:
    - GetAllFiles touches O(distinct words in query) shards, not all N.
    - Posting lists are globally complete in one place — small queries are fast.
  Cons:
    - Inserts now touch O(distinct words in doc) shards = distributed write.
    - A "hot word" (e.g. "the") creates a hot shard. Need sub-sharding by
      filename hash within that posting list.
    - Posting list for a popular word might be too big for one shard.

  This is what Google's classic search index does (term-partitioned with
  per-term sub-sharding) and what some specialized search engines use.

------------------------------------------------------------------------------
Recommended hybrid (what I'd actually build)
------------------------------------------------------------------------------
  - Document-partition by hash(filename). Each shard runs the single-node
    DocPredicateSearch above on its own slice of the corpus.
  - GetAllFiles fan-out is acceptable because:
      * each shard's predicate evaluation is identical and parallel,
      * results are typically smaller than the corpus,
      * head-of-line blocking is mitigated by fast-path "respond if you
        have nothing" — a shard with no matches returns instantly.
  - For huge corpora (>> 1 B docs), add a Bloom filter per shard listing
    the union of words it contains. The coordinator queries the Bloom
    filters first and SKIPS shards that can't contain any word in the
    predicate. Cheap pruning.

------------------------------------------------------------------------------
Replication
------------------------------------------------------------------------------
  - Replication factor = 3 per shard, spread across racks/AZs.
  - One leader per shard (Raft) for InsertDoc — the only mutating op.
    Followers replicate the WAL and serve reads.
  - Reads:
      * CheckContains   → leader (linearizable) or any replica with
                          a leader-issued lease (faster, still linearizable).
      * GetAllFiles     → any replica per shard (eventual consistency
                          is acceptable for full-corpus search).
    The trade-off: scatter-gather hits one replica per shard, so there's
    no benefit to leader-only reads when the result set spans the corpus.
  - Replica failures: Raft membership change adds a replacement; the new
    replica catches up via WAL streaming.

------------------------------------------------------------------------------
Rebalancing / repartitioning
------------------------------------------------------------------------------
  - Use consistent hashing with virtual nodes for filename-sharding so
    adding/removing nodes moves only ~1/N of the data.
  - Resize via:
      1. Spin up new shard.
      2. Stream the docs that NOW belong to it from old owner(s) (live
         reads still served by old owner).
      3. Atomic flip in the metadata service when caught up.
      4. Old owner GC's the migrated docs.

------------------------------------------------------------------------------
Service discovery / metadata
------------------------------------------------------------------------------
  - A small Raft-replicated metadata service (etcd / built-in) holds:
      shard_id -> { leader, replicas, key_range }
  - Clients (and the coordinator) cache this mapping; on routing errors
    refresh it. Same pattern as HBase META, Bigtable, CockroachDB ranges.

------------------------------------------------------------------------------
Failure handling summary
------------------------------------------------------------------------------
  Single replica down       : Raft quorum still works, no impact.
  Leader down               : new leader elected in ~hundreds of ms.
  Network partition         : minority side rejects writes (CP over AP).
  Coordinator down          : stateless — restart on another node.
  Metadata service down     : clients use cached mapping; new placements
                              pause until it recovers.
  Slow shard during fan-out : per-shard timeout + partial results +
                              per-tenant query budget so one bad shard
                              can't take down the whole search service.

------------------------------------------------------------------------------
One-line summary for the interviewer
------------------------------------------------------------------------------
  "Document-partition by hash(filename) for cheap inserts and CheckContains;
  scatter-gather for GetAllFiles. Each shard is a 3-replica Raft group so
  writes are durable and reads can be served by followers. For very large
  corpora, add a Bloom filter per shard so the coordinator skips shards
  that can't contain any word in the query — that's the cheap pruning
  that makes scatter-gather scale."
*/
