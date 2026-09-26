package reddit.New2026;

import java.util.*;

/*
================================================================================
  ReportChain — Reddit onsite (2026), 4-part progression
================================================================================

  INPUT
    A list of comma-separated strings, one per manager. The FIRST token in each
    row is the manager; every remaining token is one of their direct reports.

        ["A,B,C", "C,D", "B,E"]
             │       │       │
             │       │       └─ B manages E
             │       └───────── C manages D
             └───────────────── A manages B, C

  MODEL
    Directed forest (one or more roots) where an edge points manager → report.
    Roots = people who never appear as a report on any row.

    Data:
      Map<String, List<String>>  children      — manager -> direct reports
      Map<String, String>        parent        — child   -> manager (unique)
      LinkedHashSet<String>      everyone      — preserves first-seen order

  --------------------------------------------------------------------------
  Part 1 — Print the whole hierarchy as an indented tree
  --------------------------------------------------------------------------
    Indent = 4 dots per level:

        A
        ....B
        ........E
        ....C
        ........D

    Algorithm:
      - find roots (people not in `parent`)
      - DFS in insertion order, prepend "...." * depth to each name.

  --------------------------------------------------------------------------
  Part 2 — Skip-level meeting pairs (manager, grandchild)
  --------------------------------------------------------------------------
    Pair (u, v) is "skip-level" iff v sits EXACTLY two levels below u — i.e.
    u is v's grandparent. Direct-report pairs are excluded, and so are
    great-grandchildren and deeper.

    For the sample tree the answer is  [(A,E), (A,D)].

    ⚠ The sample tree has height 2, so it CANNOT distinguish "exactly 2" from
      "2 or more" — both specs return [(A,E), (A,D)]. Any test that pins this
      choice needs depth ≥ 3: on A→B→C→D, exactly-2 gives [(A,C), (B,D)],
      while 2-or-more would also emit (A,D).

    Algorithm:
      for each node u, descend two levels and emit every grandchild, then
      prune — a deeper pair is found later from a deeper ancestor.
      Equivalently: for each v with a grandparent, emit (grandparent(v), v) —
      which is why each pair appears exactly once in a tree.

      The prune is invisible to output tests: forgetting it still returns the
      right pairs, just at O(n·h) instead of O(n). See collectSkip.

    ⚠ This is LINEAR, unlike Parts 1 and 3. Each u touches only its children
      and grandchildren, so the work is Θ(n + #pairs) with #pairs ≤ n. Under
      the old "depth >= 2" spec a chain produced Θ(n²) pairs; now it produces
      n − 2. Measured: n = 800 chain gives 798 pairs, where "2 or more" gave
      318,801. A star gives 0.

  --------------------------------------------------------------------------
  Part 2b — MINORITY VARIANT: test two given employees   → enum Relation
  --------------------------------------------------------------------------
    ⚠ ASK THIS BEFORE WRITING ANY CODE. Some loops don't want Part 2 at all.
      They hand you two employees and ask whether one reports up to the other
      through ANY number of levels, returning the relationship or false. The
      two readings share almost no code and have different shapes:

                      "enumerate all skip-level pairs"   "test two employees"
        signature     List<String[]> ()                   Relation (a, b)
        levels        EXACTLY 2                           1 or more, unbounded
        returns       every pair                          which one is above
        cost          Θ(n + #pairs), one shot             Θ(h) per query
        upgrade       none needed                         O(1) after Euler tour

      Guessing wrong is expensive: you'd build a whole enumerator when they
      wanted a predicate, and the level rule differs too, so you can't even
      salvage the traversal.

    The distance rule is the sharpest difference. On A→B→C→D:
        skipLevelPairs()      → [(A,C), (B,D)]     — (A,D) EXCLUDED, 3 levels
        relate("A","D")       → ANCESTOR           — 3 levels is fine here
      So a pair can be unrelated under Part 2 and related under Part 2b.

    Clarify two more things while you're asking:
      - is a person their own ancestor?  Part 4's LCA says yes (LCA(A,A)=A);
        this part says NO — "one reports up to the other" needs two people.
        Same tree, two different conventions, so state each one explicitly.
      - direct reports: included here (1 level counts), excluded in Part 2.

    Algorithm (naive, no preprocessing):
      walk parent pointers up from b looking for a, then up from a looking
      for b. O(h) per query, O(1) memory — no ancestor Set needed, unlike
      Part 4, because we're testing one target rather than intersecting.

    Upgrade for many queries — Euler tour (class AncestorIndex):
      one O(n) DFS recording tin[v] / tout[v]; then
          a is a strict ancestor of b  ⟺  tin[a] < tin[b] && tout[b] < tout[a]
      because a subtree occupies a contiguous, properly nested interval.
      O(1) per query, O(n) memory. Separate trees of the forest get disjoint
      intervals, so cross-tree pairs fall out as UNRELATED for free.

  --------------------------------------------------------------------------
  Part 3 — Query one person's up-and-down chain
  --------------------------------------------------------------------------
    For query "B" on the sample, output the single-line manager chain from
    root down to B, then B's whole subtree, keeping global indentation:

        A
        ....B
        ........E

    Algorithm:
      - walk parent pointers from B up to the root → ancestors (top-down)
      - print each ancestor at its own depth (0..d-1)
      - DFS B's subtree starting at depth d = ancestors.size()

  --------------------------------------------------------------------------
  Part 4 — Lowest Common Manager (LCA)
  --------------------------------------------------------------------------
    Standard LCA in a rooted tree. For (C, E) on the sample, answer = A.

    Algorithm (simple, no preprocessing):
      - collect C's ancestors (including C) into a HashSet
      - walk up from E; first node that is in the set is the LCA
      - O(h1 + h2) time, O(h1) extra memory
      - A node is considered its own ancestor: LCA(A, C) = A.

    Alt: pre-compute depth[]; equalize depths then walk both up together
    (O(h) with O(n) preprocessing). Binary lifting → O(log n) per query.

  --------------------------------------------------------------------------
  Complexity summary   (n = # people, h = tree height, r = # roots,
                        d = depth of the queried user)
  --------------------------------------------------------------------------
  ⚠ Two different bounds matter here and they DON'T match. Node visits are
    linear-ish; the produced OUTPUT is quadratic, because the format itself is:
    level i carries 4·i indent characters, so a tree of height h costs
    Σ 4·depth(v) characters. StringBuilder.append is O(length), so the output
    size — not the visit count — is the real time bound. Measured on a pure
    chain: printTree() emits exactly ~2n² chars (1.28 MB for n = 800).
    Quote the visit count, then say "…but the string is Θ(n·h) because the
    indent is". Claiming O(n) alone is the trap.

                        nodes visited          characters emitted (= real time)
    constructor         O(sum of row tokens)   —
    Part 1 printTree    O(n)                   Θ(Σ depth(v)) = O(n·h)
                                               → Θ(n²) on a chain, 0 extra on a star
    Part 2 skipLevel    O(n + #pairs) = O(n)   Θ(#pairs) = Θ(#nodes at depth ≥ 2)
                        each u touches only    ≤ n, so LINEAR — the one part
                        children+grandchildren whose output is NOT quadratic.
    Part 2b relate      O(h1 + h2), O(1) space — (returns one enum)
    Part 2b AncestorIdx O(n) build, O(1)/query O(n) memory for tin/tout
    Part 3 queryChain   O(h + |subtree(user)|) Θ(d² + Σ_{v∈subtree} depth(v))
                                               → the d² is the ancestor indent block
    Part 4 LCA (naive)  O(h1 + h2)             — (returns one name)
    Space               O(n) for children + parent + everyone maps

    Escaping the quadratic output (only Parts 1 and 3 — Part 2 is already linear):
      - emit (name, depth) pairs and let the renderer indent → Θ(n) output
      - fixed-width prefix instead of 4·depth → Θ(n) output
    Part 2 has no quadratic to escape, but if only the COUNT is wanted:
      - count nodes at depth ≥ 2 in O(n), no enumeration
      - isSkipLevel(u,v) is already O(1): parent[parent[v]] == u — no Euler
        tour needed. The tour is what Part 2b needs, where the distance is
        unbounded and there's no fixed number of parent hops to check.
    Part 4 upgrade:
      - binary lifting → O(log n) per query after O(n log n) preprocessing
================================================================================
*/
public class ReportChain {

    private final Map<String, List<String>> children = new HashMap<>();
    private final Map<String, String>       parent   = new HashMap<>();
    private final Set<String>               everyone = new LinkedHashSet<>();

    /** Time O(sum of tokens across all rows) — one hashmap op per name. */
    public ReportChain(List<String> rows) {
        for (String row : rows) {
            String[] p = row.split(",");
            String mgr = p[0].trim();
            addPerson(mgr);
            children.computeIfAbsent(mgr, k -> new ArrayList<>());
            for (int i = 1; i < p.length; i++) {
                String rep = p[i].trim();
                addPerson(rep);
                children.get(mgr).add(rep);
                String prior = parent.get(rep);             // enforce the ≤1 manager assumption
                if (prior != null && !prior.equals(mgr)) {
                    throw new IllegalArgumentException(
                            rep + " has two managers: " + prior + " and " + mgr);
                }
                parent.put(rep, mgr);
            }
        }

        validateForest();
    }

    private void addPerson(String u) { everyone.add(u); }

    /** Every person has at most one manager, so this is a functional graph — climbing parent pointers
     *  is enough, no recursive DFS over children. The two sets mean different things and cannot be
     *  merged: `seen` is the current climb (a repeat is a cycle), `safe` is already proven clean (a hit
     *  just stops the climb). Merging them turns every cycle into a silent stop. `safe` is what keeps
     *  this O(n) amortized rather than O(n·h) — measured n²/2 climbs without it on a chain.
     *  Fail fast here: a cycle later means a silent "" from printTree, a StackOverflowError,
     *  an OutOfMemoryError, or an unkillable hang in lowestCommonManager. */
    private void validateForest() {
        Set<String> safe = new HashSet<>();
        for (String start : everyone) {
            Set<String> seen = new HashSet<>();
            String cur = start;
            while (cur != null && !safe.contains(cur)) {
                if (seen.contains(cur)) {
                    throw new IllegalArgumentException("reporting cycle involving " + cur);
                }
                seen.add(cur);
                cur = parent.get(cur);
            }
            safe.addAll(seen);
        }
    }

    /* ============================================================
       Part 1 — full hierarchy as an indented tree
       ============================================================ */
    /** Visits O(n) nodes, but emits Θ(Σ depth(v)) = O(n·h) characters — the 4-dots-per-level
     *  format is what's quadratic, not the traversal. Θ(n²) on a pure reporting chain. */
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        for (String root : findRoots()) dfsIndented(root, 0, sb);
        return sb.toString();
    }

    private List<String> findRoots() {
        List<String> roots = new ArrayList<>();
        for (String u : everyone) if (!parent.containsKey(u)) roots.add(u);
        return roots;
    }

    private void dfsIndented(String node, int depth, StringBuilder sb) {
        for (int i = 0; i < depth * 4; i++) sb.append('.');
        sb.append(node).append('\n');
        for (String c : children.getOrDefault(node, Collections.emptyList())) {
            dfsIndented(c, depth + 1, sb);
        }
    }

    /* ============================================================
       Part 2 — skip-level pairs
       ============================================================ */
    /** Every (manager, grandchild) pair — exactly two levels, not "two or more".
     *  Θ(n + #pairs) = O(n): each u descends only two levels, then prunes, because a
     *  deeper pair is emitted later from a deeper ancestor. Output-optimal. */
    public List<String[]> skipLevelPairs() {
        List<String[]> out = new ArrayList<>();
        for (String u : everyone) collectSkip(u, u, 0, out);
        return out;
    }

    /** ⚠ The `return` is a COST guard, not a correctness one. Without it the output is
     *  byte-identical (deeper nodes are visited but never match depth == 2), so no
     *  output-based test can catch its absence — it's the difference between
     *  Θ(n + #pairs) and O(Σ |subtree(u)|) = O(n·h), i.e. linear vs quadratic on a chain. */
    private void collectSkip(String ancestor, String node, int depth, List<String[]> out) {
        if (depth == 2) {
            out.add(new String[]{ancestor, node});
            return;
        }
        for (String c : children.getOrDefault(node, Collections.emptyList())) {
            collectSkip(ancestor, c, depth + 1, out);
        }
    }

    /* ============================================================
       Part 2b — minority variant: are these two related at all?
                 Note this shares NO code with Part 2 above: different
                 signature, different level rule, different cost model.
       ============================================================ */

    /** Result of relate(a, b), read as "a is the ___ of b". */
    public enum Relation {
        ANCESTOR,       // a is somewhere above b (1+ levels — direct reports count)
        DESCENDANT,     // a is somewhere below b
        UNRELATED       // siblings, cousins, different trees, unknown names, or a == b
    }

    /** Does one of these two report up to the other, at any distance?
     *  Time O(h1 + h2), space O(1) — one target to look for, so no Set (contrast Part 4).
     *  ⚠ STRICT: relate(x, x) is UNRELATED, unlike lowestCommonManager, where a node
     *  is its own ancestor. Two conventions in one file is fine as long as both are stated. */
    public Relation relate(String a, String b) {
        if (!everyone.contains(a) || !everyone.contains(b)) return Relation.UNRELATED;
        if (a.equals(b)) return Relation.UNRELATED;

        String x = parent.get(b);                       // walk up from b looking for a
        while (x != null) {
            if (x.equals(a)) return Relation.ANCESTOR;
            x = parent.get(x);
        }

        String y = parent.get(a);                       // then up from a looking for b
        while (y != null) {
            if (y.equals(b)) return Relation.DESCENDANT;
            y = parent.get(y);
        }
        return Relation.UNRELATED;
    }

    /** Time O(n) — one DFS over the whole forest. Amortizes to O(1) per relate() call. */
    public AncestorIndex buildAncestorIndex() { return new AncestorIndex(); }

    /** Euler-tour intervals: a subtree is a contiguous, properly nested [tin, tout] range,
     *  so ancestry becomes an interval-containment test. O(1) per query after O(n) setup. */
    public final class AncestorIndex {
        private final Map<String, Integer> tin = new HashMap<>(), tout = new HashMap<>();
        private int timer = 0;

        private AncestorIndex() {
            for (String root : findRoots()) tour(root);      // disjoint ranges per tree
        }

        private void tour(String node) {
            tin.put(node, timer++);
            for (String c : children.getOrDefault(node, Collections.emptyList())) tour(c);
            tout.put(node, timer++);
        }

        /** Time O(1) — four map lookups and two int compares. Same contract as relate(). */
        public Relation relate(String a, String b) {
            Integer ai = tin.get(a), bi = tin.get(b);
            if (ai == null || bi == null || a.equals(b)) return Relation.UNRELATED;
            if (ai < bi && tout.get(b) < tout.get(a)) return Relation.ANCESTOR;
            if (bi < ai && tout.get(a) < tout.get(b)) return Relation.DESCENDANT;
            return Relation.UNRELATED;
        }
    }

    /* ============================================================
       Part 3 — single-user up-and-down chain
       ============================================================ */
    /** Visits O(h + |subtree(user)|) nodes, but emits Θ(d² + Σ_{v∈subtree} depth(v))
     *  characters — the d² is the ancestor indent block below. */
    public String queryChain(String user) {
        if (!everyone.contains(user)) return "";
        List<String> ancestors = new ArrayList<>();     // root ... user's manager
        String cur = user;
        while (parent.containsKey(cur)) {
            cur = parent.get(cur);
            ancestors.add(cur);
        }
        Collections.reverse(ancestors);                 // top-down

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ancestors.size(); i++) {
            for (int j = 0; j < i * 4; j++) sb.append('.');
            sb.append(ancestors.get(i)).append('\n');
        }
        dfsIndented(user, ancestors.size(), sb);
        return sb.toString();
    }

    /* ============================================================
       Part 4 — lowest common manager (LCA)
       ============================================================ */
    /** A node counts as its own ancestor, so LCA(A, C) = A when A manages C.
     *  Returns null for unknown names or for two different trees of the forest.
     *  Time O(h1 + h2), space O(h1). */
    public String lowestCommonManager(String u, String v) {
        if (!everyone.contains(u) || !everyone.contains(v)) return null;

        // `x != null`, NOT parent.containsKey(x) — the latter exits before adding the root.
        Set<String> path = new HashSet<>();
        String x = u;
        while (x != null) {
            path.add(x);
            x = parent.get(x);
        }

        String y = v;
        while (y != null) {
            if (path.contains(y)) return y;
            y = parent.get(y);
        }

        return null;                                    // disconnected forest
    }

    /* ============================================================
       Tests / demo
       ============================================================ */
    public static void main(String[] args) {
        List<String> rows = Arrays.asList("A,B,C", "C,D", "B,E");
        ReportChain rc = new ReportChain(rows);

        // ---- Part 1
        expect("Part 1: full tree",
                rc.printTree(),
                "A\n....B\n........E\n....C\n........D\n");

        // ---- Part 2
        // The sample tree is height 2, so it agrees under both specs — this depth-3
        // chain is the only thing here that pins "exactly two" over "two or more":
        // 2-or-more would additionally emit (A,D).
        expect("Part 2: exactly two levels, not two-or-more",
                pairsToString(new ReportChain(Arrays.asList("A,B", "B,C", "C,D")).skipLevelPairs()),
                "[(A,C), (B,D)]");
        // A great-grandchild reached through a branch must also be excluded.
        expect("Part 2: depth 3 through a branch is excluded",
                pairsToString(new ReportChain(Arrays.asList("A,B,X", "B,C", "C,D")).skipLevelPairs()),
                "[(A,C), (B,D)]");
        expect("Part 2: a star has no grandchildren",
                pairsToString(new ReportChain(Arrays.asList("A,B,C,D")).skipLevelPairs()),
                "[]");

        expect("Part 2: skip-level pairs",
                pairsToString(rc.skipLevelPairs()),
                "[(A,E), (A,D)]");

        // ---- Part 2b: test two given employees
        expect("Part 2b: grandparent is an ANCESTOR", rc.relate("A", "E"), Relation.ANCESTOR);
        expect("Part 2b: and the reverse is DESCENDANT", rc.relate("E", "A"), Relation.DESCENDANT);
        expect("Part 2b: a DIRECT report still counts", rc.relate("A", "B"), Relation.ANCESTOR);
        expect("Part 2b: siblings are unrelated",  rc.relate("B", "C"), Relation.UNRELATED);
        expect("Part 2b: cousins are unrelated",   rc.relate("D", "E"), Relation.UNRELATED);
        expect("Part 2b: self is unrelated (strict, unlike LCA)",
                rc.relate("A", "A"), Relation.UNRELATED);
        expect("Part 2b: unknown name",            rc.relate("A", "Z"), Relation.UNRELATED);
        expect("Part 2b: both unknown",            rc.relate("Y", "Z"), Relation.UNRELATED);
        expect("Part 2b: across trees of a forest",
                new ReportChain(Arrays.asList("A,B", "B,C", "X,Y", "Y,Z")).relate("C", "Z"),
                Relation.UNRELATED);

        // The sharpest difference between the two readings of Part 2: on A→B→C→D the
        // pair (A,D) is 3 levels apart, so it is NOT a skip-level pair but IS ancestry.
        ReportChain chain = new ReportChain(Arrays.asList("A,B", "B,C", "C,D"));
        expect("Part 2b: 3 levels is ancestry...", chain.relate("A", "D"), Relation.ANCESTOR);
        expect("Part 2b: ...but not a skip-level pair",
                pairsToString(chain.skipLevelPairs()), "[(A,C), (B,D)]");

        // The O(1) upgrade must agree with the naive walk everywhere.
        ReportChain.AncestorIndex idx = rc.buildAncestorIndex();
        expect("Part 2b: index agrees on ANCESTOR",   idx.relate("A", "E"), Relation.ANCESTOR);
        expect("Part 2b: index agrees on DESCENDANT", idx.relate("E", "A"), Relation.DESCENDANT);
        expect("Part 2b: index agrees on siblings",   idx.relate("B", "C"), Relation.UNRELATED);
        expect("Part 2b: index agrees on self",       idx.relate("A", "A"), Relation.UNRELATED);
        expect("Part 2b: index agrees on unknowns",   idx.relate("A", "Z"), Relation.UNRELATED);

        // Exhaustive cross-check on the sample tree: naive vs Euler tour, all ordered pairs.
        int disagreements = 0, related = 0;
        String[] all = {"A", "B", "C", "D", "E", "Z"};
        for (String x : all) for (String y : all) {
            if (rc.relate(x, y) != idx.relate(x, y)) disagreements++;
            if (rc.relate(x, y) != Relation.UNRELATED) related++;
        }
        expect("Part 2b: naive and indexed agree on all 36 ordered pairs", disagreements, 0);
        expect("Part 2b: ...and 12 of those pairs are actually related", related, 12);

        // ---- Part 3
        expect("Part 3: query B",
                rc.queryChain("B"),
                "A\n....B\n........E\n");
        expect("Part 3: query A (root)",
                rc.queryChain("A"),
                "A\n....B\n........E\n....C\n........D\n");
        expect("Part 3: query E (leaf)",
                rc.queryChain("E"),
                "A\n....B\n........E\n");
        expect("Part 3: unknown user",
                rc.queryChain("Z"),
                "");

        // ---- Part 4
        expect("Part 4: LCA(C, E) = A",  rc.lowestCommonManager("C", "E"), "A");
        expect("Part 4: LCA(D, E) = A",  rc.lowestCommonManager("D", "E"), "A");
        expect("Part 4: LCA(A, D) = A",  rc.lowestCommonManager("A", "D"), "A");
        expect("Part 4: LCA(D, D) = D",  rc.lowestCommonManager("D", "D"), "D");
        expect("Part 4: LCA(B, E) = B",  rc.lowestCommonManager("B", "E"), "B");

        // ---- Multi-root forest
        ReportChain forest = new ReportChain(Arrays.asList(
                "A,B", "B,C",     // A -> B -> C
                "X,Y", "Y,Z"      // X -> Y -> Z
        ));
        expect("Forest: full tree",
                forest.printTree(),
                "A\n....B\n........C\nX\n....Y\n........Z\n");
        expect("Forest: skip-level across trees",
                pairsToString(forest.skipLevelPairs()),
                "[(A,C), (X,Z)]");
        expect("Forest: LCA across trees is null",
                forest.lowestCommonManager("C", "Z"), null);
    }

    /* --------------------------- helpers --------------------------- */
    private static String pairsToString(List<String[]> pairs) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (String[] p : pairs) sj.add("(" + p[0] + "," + p[1] + ")");
        return sj.toString();
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + toDisplay(got)
                           + "\n  expected=" + toDisplay(expected)));
    }
    private static String toDisplay(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\\n");
    }
}
