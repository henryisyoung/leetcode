package netflix.ProblemSolving;
/*
Inverted Index over Nested JSON Config Metadata.

Input
  metadataMap : Map<name, configJson>  (name is unique)
                configJson values can be:
                  - scalars (String / Number / Boolean / null)
                  - arrays of values / objects
                  - nested objects
  defaults    : Map<dotted-path, default value>
                applied for any name that is missing the field at that path
                (anywhere in the nested object).

Output API
  build_index(metadataMap, defaults)
  execute(query) -> Set<name>   matching names
  executeWithConfigs(query) -> Map<name, configJson>

Query DSL (Mongo-ish, kept small)
  Plain field constraint
     {region: "us"}                 region == "us"
     {tags: "beta"}                 ARRAY CONTAINMENT: "beta" appears in tags[]
     {owner: {team: "search"}}      nested object descent
     {region: ["us", "ca"]}         shorthand for $in
  Operators (all start with '$')
     {$and: [q1, q2, ...]}          intersection
     {$or:  [q1, q2, ...]}          union
     {$not: q}                      complement vs. all names
     {$in:  [v1, v2, ...]}          membership against the current path
     {$eq:  v}                      explicit equality
  Sibling top-level keys are implicitly AND-ed.

Scale assumptions (interview-stated)
  N = #names                 1e5
  F = avg fields per config  ~20
  D = max nesting depth      <= 5
  V = avg distinct values per path  varies; bounded by N
  Q = query operator nodes    <= 1e3

Complexity
  build:   O(N * F)            — one walk per config
  execute: O(Q * (R + log V))  — each leaf is a HashMap lookup that
                                 yields a posting set; AND/OR cost is
                                 proportional to set sizes touched (R)
  space:   O(N * F)
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/*
Inverted-index key design

  After flattening, each (name, path, value) tuple becomes a posting:
        index[path][value]  -> set of names

  Where `path` is a List<String> of object keys from the root. Arrays
  do NOT contribute to the path — every element shares the array's
  path. This makes "array contains X" a free side-effect of equality
  lookup: querying {tags: "beta"} hits all names whose tags[] list
  contains "beta".

  Examples (path -> value):
    {region: "us"}                            -> ([region],         "us")
    {tags: ["beta", "stable"]}                -> ([tags], "beta"),
                                                 ([tags], "stable")
    {owner: {team: "search"}}                 -> ([owner, team],    "search")
    {regions: [{code:"us"}, {code:"ca"}]}     -> ([regions, code],  "us"),
                                                 ([regions, code],  "ca")

  Lossy detail (worth calling out in the interview):
    Flattening arrays-of-objects DISSOLVES the pairing between sibling
    keys.  Above, ([regions, code]="us") and ([regions, tier]=1) are
    indexed independently, so the index can't answer "regions where
    code=us AND tier=1 in the SAME element".  If pairing matters, the
    standard fix is to add a synthetic element id to the path
    ([regions, $i, code]) and pre-group, or do a final per-name
    object-level filter as a post-pass.  Both are bolt-ons; the core
    index stays the same.

Default values

  For each path P with a default D, any name that did NOT contribute a
  posting at P during build gets (P, D) added at the end of build.
  This lets the user write {tier: "basic"} and match BOTH explicit
  basic-tier names AND names that simply omitted the field.

  Defaults are configured via a flat Map<dottedPath, defaultValue> for
  ergonomics; "." is treated as a path separator. (Standard JSON
  keys-with-dots caveat applies.)

How postings are combined

  evaluate() is one recursive function over the query Object:
    - Map with operator keys ($and/$or/$not/$in/$eq) handles the
      explicit boolean operators.
    - Map with regular keys descends path-by-key (and ANDs siblings).
    - List anywhere in a value position becomes $in.
    - Scalar becomes equality at the current path.

  Short-circuit: AND returns the empty set the moment the accumulator
  goes empty.  OR could deduplicate eagerly via the result Set's
  contains semantics.  NOT is implemented as `allNames \ matching`.

Edge cases handled
  - Missing field with no default registered  -> name simply absent
                                                   from that path's bucket
  - null in JSON                              -> indexed as the value
                                                   `JSON_NULL` (a sentinel)
  - duplicate values inside one array         -> deduped by Set semantics
  - Empty query {}                            -> matches all names
  - Operator on top-level path                -> evaluated at root
*/
public class InvertedIndex {

    /** Sentinel for JSON null (HashMap doesn't take null keys; this gives us a value-key). */
    public static final Object JSON_NULL = new Object() {
        @Override public String toString() { return "<null>"; }
    };

    /** index[path][value] -> names */
    private final Map<List<String>, Map<Object, Set<String>>> index = new HashMap<>();
    private final Set<String> allNames = new LinkedHashSet<>();
    private final Map<String, Object> configs = new LinkedHashMap<>();
    private final Map<List<String>, Object> defaults;

    /** Convenience constructor: defaults via dotted paths. */
    public InvertedIndex(Map<String, Object> metadataMap, Map<String, Object> defaultsByDottedPath) {
        this.defaults = toPathDefaults(defaultsByDottedPath);
        buildIndex(metadataMap);
    }

    public InvertedIndex(Map<String, Object> metadataMap) {
        this(metadataMap, Collections.emptyMap());
    }

    private static Map<List<String>, Object> toPathDefaults(Map<String, Object> dotted) {
        if (dotted == null || dotted.isEmpty()) return Collections.emptyMap();
        Map<List<String>, Object> out = new HashMap<>();
        for (Map.Entry<String, Object> e : dotted.entrySet()) {
            out.put(Arrays.asList(e.getKey().split("\\.")), normalize(e.getValue()));
        }
        return out;
    }

    /* --------------------------- build --------------------------- */

    public void buildIndex(Map<String, Object> metadataMap) {
        if (metadataMap == null) return;
        for (Map.Entry<String, Object> e : metadataMap.entrySet()) {
            addConfig(e.getKey(), e.getValue());
        }
        applyDefaults();
    }

    private void addConfig(String name, Object config) {
        if (name == null) throw new IllegalArgumentException("name must be non-null");
        if (allNames.contains(name)) {
            throw new IllegalArgumentException("duplicate name: " + name);
        }
        allNames.add(name);
        configs.put(name, config);
        flatten(name, new ArrayList<>(), config);
    }

    @SuppressWarnings("unchecked")
    private void flatten(String name, List<String> path, Object node) {
        if (node == null) {
            addPosting(name, path, JSON_NULL);
        } else if (node instanceof Map) {
            for (Map.Entry<String, Object> e : ((Map<String, Object>) node).entrySet()) {
                List<String> sub = new ArrayList<>(path.size() + 1);
                sub.addAll(path);
                sub.add(e.getKey());
                flatten(name, sub, e.getValue());
            }
        } else if (node instanceof List) {
            // Arrays share the parent path; each element gets indexed under it.
            // Empty array contributes no postings (use a default if you want
            // "missing field" semantics).
            for (Object elem : (List<?>) node) flatten(name, path, elem);
        } else {
            addPosting(name, path, normalize(node));
        }
    }

    private void addPosting(String name, List<String> path, Object value) {
        index
                .computeIfAbsent(new ArrayList<>(path), k -> new HashMap<>())
                .computeIfAbsent(value, k -> new HashSet<>())
                .add(name);
    }

    /** For every default-configured path, fill in names that contributed nothing there. */
    private void applyDefaults() {
        for (Map.Entry<List<String>, Object> def : defaults.entrySet()) {
            List<String> path = def.getKey();
            Object value = def.getValue();
            Map<Object, Set<String>> bucket = index.computeIfAbsent(path, k -> new HashMap<>());
            Set<String> covered = new HashSet<>();
            for (Set<String> ns : bucket.values()) covered.addAll(ns);
            for (String name : allNames) {
                if (!covered.contains(name)) {
                    bucket.computeIfAbsent(value, k -> new HashSet<>()).add(name);
                }
            }
        }
    }

    /** Number canonicalisation: equality across Integer/Long/Double would otherwise miss. */
    private static Object normalize(Object v) {
        if (v == null) return JSON_NULL;
        if (v instanceof Number) {
            Number n = (Number) v;
            double d = n.doubleValue();
            if (d == Math.floor(d) && !Double.isInfinite(d)) return n.longValue();
            return d;
        }
        return v;
    }

    /* --------------------------- query --------------------------- */

    public Set<String> execute(Object query) {
        Set<String> r = evaluate(query, new ArrayList<>());
        return new TreeSet<>(r);
    }

    public Map<String, Object> executeWithConfigs(Object query) {
        Set<String> names = execute(query);
        Map<String, Object> out = new LinkedHashMap<>();
        for (String n : names) out.put(n, configs.get(n));
        return out;
    }

    @SuppressWarnings("unchecked")
    private Set<String> evaluate(Object node, List<String> prefix) {
        if (node == null) return lookup(prefix, JSON_NULL);

        if (node instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) node;
            if (m.isEmpty()) return new HashSet<>(allNames);     // {} matches all
            Set<String> acc = null;
            for (Map.Entry<String, Object> e : m.entrySet()) {
                String key = e.getKey();
                Set<String> partial;
                switch (key) {
                    case "$and": partial = evalAnd(e.getValue(), prefix);                  break;
                    case "$or":  partial = evalOr(e.getValue(), prefix);                   break;
                    case "$not": partial = complement(evaluate(e.getValue(), prefix));     break;
                    case "$in":  partial = evalIn(e.getValue(), prefix);                   break;
                    case "$eq":  partial = lookup(prefix, normalize(e.getValue()));        break;
                    default:
                        List<String> ext = new ArrayList<>(prefix.size() + 1);
                        ext.addAll(prefix);
                        ext.add(key);
                        partial = evaluate(e.getValue(), ext);
                }
                acc = (acc == null) ? partial : intersect(acc, partial);
                if (acc.isEmpty()) return acc;                                              // AND short-circuit
            }
            return acc == null ? new HashSet<>(allNames) : acc;
        }

        if (node instanceof List) {
            return evalIn(node, prefix);                                                   // implicit $in
        }

        return lookup(prefix, normalize(node));
    }

    private Set<String> evalAnd(Object node, List<String> prefix) {
        List<?> list = asList(node, "$and");
        if (list.isEmpty()) return new HashSet<>(allNames);
        Set<String> acc = null;
        for (Object q : list) {
            Set<String> r = evaluate(q, prefix);
            acc = (acc == null) ? r : intersect(acc, r);
            if (acc.isEmpty()) return acc;
        }
        return acc;
    }

    private Set<String> evalOr(Object node, List<String> prefix) {
        List<?> list = asList(node, "$or");
        Set<String> acc = new HashSet<>();
        for (Object q : list) acc.addAll(evaluate(q, prefix));
        return acc;
    }

    private Set<String> evalIn(Object node, List<String> prefix) {
        List<?> list = asList(node, "$in");
        Set<String> acc = new HashSet<>();
        for (Object v : list) acc.addAll(evaluate(v, prefix));
        return acc;
    }

    private static List<?> asList(Object o, String op) {
        if (!(o instanceof List)) throw new IllegalArgumentException(op + " expects an array");
        return (List<?>) o;
    }

    private Set<String> lookup(List<String> path, Object value) {
        Map<Object, Set<String>> bucket = index.get(path);
        if (bucket == null) return new HashSet<>();
        Set<String> ns = bucket.get(value);
        return ns == null ? new HashSet<>() : new HashSet<>(ns);
    }

    private Set<String> complement(Set<String> s) {
        Set<String> out = new HashSet<>(allNames);
        out.removeAll(s);
        return out;
    }

    private static Set<String> intersect(Set<String> a, Set<String> b) {
        if (a.size() > b.size()) { Set<String> t = a; a = b; b = t; }    // iterate the smaller
        Set<String> out = new HashSet<>();
        for (String x : a) if (b.contains(x)) out.add(x);
        return out;
    }

    public int size() { return allNames.size(); }
    public Set<String> names() { return Collections.unmodifiableSet(allNames); }

    /* --------------------------- ergonomic builders --------------------------- */

    /** Build a JSON object literal: obj("k1", v1, "k2", v2, ...). */
    public static Map<String, Object> obj(Object... kv) {
        if ((kv.length & 1) != 0) throw new IllegalArgumentException("obj needs even args");
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    /** Build a JSON array literal: arr(v1, v2, v3). */
    public static List<Object> arr(Object... xs) {
        List<Object> out = new ArrayList<>(xs.length);
        Collections.addAll(out, xs);
        return out;
    }

    /* --------------------------- demos / tests --------------------------- */

    public static void main(String[] args) {
        // ---- Example metadata ----
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("app1", obj(
                "region", "us",
                "tier",   "premium",
                "tags",   arr("beta", "stable"),
                "owner",  obj("team", "search", "email", "search@x.com")));
        meta.put("app2", obj(
                "region", "us",
                "tier",   "basic",
                "tags",   arr("beta"),
                "owner",  obj("team", "infra")));                       // no owner.email
        meta.put("app3", obj(
                "region", "ca",
                "tier",   "premium",
                "owner",  obj("team", "search")));                      // no tags, no owner.email
        meta.put("app4", obj(
                "region", "jp"));                                       // tier missing, etc.

        // ---- Defaults ----
        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("tier",        "basic");
        defaults.put("owner.email", "(none)");
        defaults.put("region",      "unknown");

        InvertedIndex idx = new InvertedIndex(meta, defaults);

        // ---- Simple scalar equality ----
        check("region = us",
                idx.execute(obj("region", "us")),
                setOf("app1", "app2"));

        // ---- Array containment (free with our flattening) ----
        check("tags contains beta",
                idx.execute(obj("tags", "beta")),
                setOf("app1", "app2"));
        check("tags contains stable",
                idx.execute(obj("tags", "stable")),
                setOf("app1"));

        // ---- Nested object descent ----
        check("owner.team = search (nested obj)",
                idx.execute(obj("owner", obj("team", "search"))),
                setOf("app1", "app3"));

        // ---- Defaulting: owner.email defaulted to "(none)" for app2/3/4 ----
        check("owner.email = (none) via default",
                idx.execute(obj("owner", obj("email", "(none)"))),
                setOf("app2", "app3", "app4"));
        check("tier defaulted to basic for app4",
                idx.execute(obj("tier", "basic")),
                setOf("app2", "app4"));

        // ---- Implicit AND across sibling keys ----
        check("AND: region=us AND tier=premium",
                idx.execute(obj("region", "us", "tier", "premium")),
                setOf("app1"));

        // ---- Inline $in via array shorthand ----
        check("region in [us, ca]",
                idx.execute(obj("region", arr("us", "ca"))),
                setOf("app1", "app2", "app3"));

        // ---- Explicit operators ----
        check("$or",
                idx.execute(obj("$or", arr(
                        obj("region", "ca"),
                        obj("tier", "premium")))),
                setOf("app1", "app3"));
        check("$and",
                idx.execute(obj("$and", arr(
                        obj("region", "us"),
                        obj("tags", "stable")))),
                setOf("app1"));
        check("$not(tier=premium) -- with default basic for app4",
                idx.execute(obj("$not", obj("tier", "premium"))),
                setOf("app2", "app4"));
        check("$in",
                idx.execute(obj("tier", obj("$in", arr("premium")))),
                setOf("app1", "app3"));
        check("$eq explicit",
                idx.execute(obj("region", obj("$eq", "jp"))),
                setOf("app4"));

        // ---- Empty query -> all ----
        check("empty {} matches all",
                idx.execute(obj()),
                setOf("app1", "app2", "app3", "app4"));

        // ---- Unknown value -> empty ----
        check("region = mars (no match)",
                idx.execute(obj("region", "mars")),
                setOf());

        // ---- executeWithConfigs returns the original configs ----
        Map<String, Object> hits = idx.executeWithConfigs(obj("region", "ca"));
        check("withConfigs region=ca keys",
                new TreeSet<>(hits.keySet()),
                setOf("app3"));

        // ---- Stress: 1e4 names, ~10 fields each, varied values ----
        Map<String, Object> big = new LinkedHashMap<>();
        String[] regions = {"us", "ca", "jp", "eu", "au"};
        for (int i = 0; i < 10_000; i++) {
            big.put("svc" + i, obj(
                    "region", regions[i % regions.length],
                    "tier",   (i % 3 == 0) ? "premium" : "basic",
                    "tags",   arr("v" + (i % 7), "common"),
                    "owner",  obj("team", "t" + (i % 13))));
        }
        long t0 = System.nanoTime();
        InvertedIndex bigIdx = new InvertedIndex(big, defaults);
        long buildMs = (System.nanoTime() - t0) / 1_000_000;
        long t1 = System.nanoTime();
        Set<String> r = bigIdx.execute(obj("$and", arr(
                obj("region", "us"),
                obj("tier", "premium"),
                obj("tags", "common"))));
        long qMs = (System.nanoTime() - t1) / 1_000_000;
        System.out.println("Stress n=10000: build=" + buildMs + " ms, query=" + qMs
                + " ms, hits=" + r.size());

        // ---- Vocabulary diagnostics ----
        System.out.println("paths indexed = " + idx.indexedPathCount()
                + ", names = " + idx.size());
    }

    public int indexedPathCount() { return index.size(); }

    /* --------------------------- assertion helpers --------------------------- */

    private static Set<String> setOf(String... xs) {
        return new TreeSet<>(Arrays.asList(xs));
    }

    private static void check(String label, Set<String> got, Set<String> expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + "  got=" + got + " expected=" + expected);
    }
}
