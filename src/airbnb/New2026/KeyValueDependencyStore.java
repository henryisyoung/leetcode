package airbnb.New2026;

import java.util.*;

/*
================================================================================
  Reactive key-value store with sum dependencies (Airbnb).
================================================================================

  API
    setValue(key, v)             — store an integer leaf value.
    setSum(key, [d1, d2, ...])   — define key as the SUM of the values of the
                                   given dependency keys.  Duplicates count:
                                   ["C","C","A"] means 2*C + A.
    getValue(key) -> int         — return the current value of key.

  Cascading behavior
    Any change to a leaf (setValue) or a formula (setSum) propagates to every
    dependent. Example from the spec:

       setValue(A, 5);  setValue(B, 10);
       setSum(C, [A, B])             →  C = 15
       setSum(D, [C, C, A])          →  D = 35
       setValue(A, 100)              →  C = 110, D = 320

  Two strategies, opposite trade-offs
  ──────────────────────────────────────────────────────────────────────────────
    EAGER   (default):    setValue: O(|transitive dependents| · avg formula size)
                          getValue: O(1)
                          → best when reads >> writes

    LAZY    (followup):   setValue: O(1)
                          getValue: O(formula subtree size)  with per-call memo
                          → best when WRITES >> reads
                          → answers the interview followup:
                            "if getValue is rare and setValue is frequent,
                             how to improve setValue?"

  Both are implemented below as inner classes so you can compare them
  side by side in the demo.

  Assumptions
    - Dependency graph is acyclic. (A cycle check could be added in setSum
      via BFS over current dependents, rejecting if `key` is reachable from
      any new dep — omitted here for brevity.)
    - Referencing an undefined key yields 0 (so setSum can forward-reference).

  Complexity recap
    Eager  setValue / setSum:  O(D)        D = transitive dependents touched
    Eager  getValue:           O(1)
    Lazy   setValue / setSum:  O(1)
    Lazy   getValue:           O(S)        S = unique nodes in formula subtree
================================================================================
*/
public class KeyValueDependencyStore {

    /* ============================================================
       EAGER — recompute on every write, reads are O(1).
       ============================================================ */
    public static class Eager {
        private final Map<String, Integer> value = new HashMap<>();
        // formula[k] is present iff k was defined via setSum (kept null otherwise).
        private final Map<String, List<String>> formula = new HashMap<>();
        // dependents[k] = set of keys whose formula MENTIONS k (reverse edges).
        private final Map<String, Set<String>> dependents = new HashMap<>();

        public void setValue(String key, int v) {
            detachFormula(key);
            value.put(key, v);
            propagate(key);
        }

        public void setSum(String key, List<String> deps) {
            detachFormula(key);
            formula.put(key, new ArrayList<>(deps));
            for (String d : deps) dependents.computeIfAbsent(d, k -> new HashSet<>()).add(key);
            value.put(key, recomputeFromFormula(key));
            propagate(key);
        }

        public int getValue(String key) {
            return value.getOrDefault(key, 0);
        }

        /** Remove `key` from the dependents set of each of its old deps. */
        private void detachFormula(String key) {
            List<String> old = formula.remove(key);
            if (old == null) return;
            for (String d : old) {
                Set<String> s = dependents.get(d);
                if (s != null) {
                    s.remove(key);
                    if (s.isEmpty()) dependents.remove(d);
                }
            }
        }

        private int recomputeFromFormula(String key) {
            int sum = 0;
            for (String d : formula.get(key)) sum += getValue(d);
            return sum;
        }

        /** BFS over dependents, recomputing each. Re-pushes on value change,
         *  so we converge even when BFS order doesn't match topological order. */
        private void propagate(String key) {
            Set<String> seed = dependents.get(key);
            if (seed == null) return;
            Queue<String> q = new LinkedList<>(seed);
            while (!q.isEmpty()) {
                String k = q.poll();
                int newV = recomputeFromFormula(k);
                if (newV != getValue(k)) {
                    value.put(k, newV);
                    Set<String> next = dependents.get(k);
                    if (next != null) q.addAll(next);
                }
                // unchanged → no downstream propagation needed
            }
        }
    }

    /* ============================================================
       LAZY — writes are O(1), reads traverse the formula subtree.
       This is the answer to the "setValue is frequent" followup.
       ============================================================ */
    public static class Lazy {
        private final Map<String, Integer> value = new HashMap<>();
        private final Map<String, List<String>> formula = new HashMap<>();

        public void setValue(String key, int v) {
            formula.remove(key);
            value.put(key, v);
        }

        public void setSum(String key, List<String> deps) {
            value.remove(key);
            formula.put(key, new ArrayList<>(deps));
        }

        public int getValue(String key) {
            return eval(key, new HashMap<>());
        }

        // Per-call memo collapses repeated deps (["C","C","A"] only walks C once).
        private int eval(String key, Map<String, Integer> memo) {
            Integer cached = memo.get(key);
            if (cached != null) return cached;

            int v;
            if (formula.containsKey(key)) {
                int sum = 0;
                for (String d : formula.get(key)) sum += eval(d, memo);
                v = sum;
            } else {
                v = value.getOrDefault(key, 0);
            }
            memo.put(key, v);
            return v;
        }
    }

    /* --- demo: reproduces the spec example on both implementations --- */
    public static void main(String[] args) {
        System.out.println("=== Eager (reads cheap, writes propagate) ===");
        Eager e = new Eager();
        e.setValue("A", 5);
        e.setValue("B", 10);
        e.setSum("C", List.of("A", "B"));
        e.setSum("D", List.of("C", "C", "A"));
        System.out.println("A=" + e.getValue("A"));     // 5
        System.out.println("C=" + e.getValue("C"));     // 15
        System.out.println("D=" + e.getValue("D"));     // 35
        e.setValue("A", 100);
        System.out.println("after A=100: C=" + e.getValue("C") + ", D=" + e.getValue("D"));  // 110, 320

        System.out.println("\n=== Lazy (writes cheap, reads compute) ===");
        Lazy l = new Lazy();
        l.setValue("A", 5);
        l.setValue("B", 10);
        l.setSum("C", List.of("A", "B"));
        l.setSum("D", List.of("C", "C", "A"));
        System.out.println("D=" + l.getValue("D"));     // 35
        l.setValue("A", 100);
        System.out.println("after A=100: C=" + l.getValue("C") + ", D=" + l.getValue("D"));  // 110, 320

        // Forward reference: define D before its deps exist (lazy handles naturally).
        System.out.println("\n=== Lazy forward reference ===");
        Lazy fwd = new Lazy();
        fwd.setSum("X", List.of("Y", "Z"));
        System.out.println("X (Y,Z undefined)=" + fwd.getValue("X"));   // 0 + 0 = 0
        fwd.setValue("Y", 7);
        fwd.setValue("Z", 3);
        System.out.println("X after Y=7, Z=3 → " + fwd.getValue("X"));  // 10
    }
}
