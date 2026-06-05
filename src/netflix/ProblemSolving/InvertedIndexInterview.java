package netflix.ProblemSolving;
/*
Inverted Index over nested JSON configs — 1-hour interview version.

What you build in the hour
  - flatten each config to (path, value) postings keyed by name
  - query JSON: sibling keys = AND, nested objects descend the path,
    inline arrays are OR (implicit $in), leaves are equality

What you DEFER, but mention out loud as follow-ups
  - default values for missing fields  (one post-pass after build)
  - $or / $not / $eq / range operators (each is one switch arm)
  - number canonicalisation (Integer/Long/Double)
  - array-of-objects pair preservation (synthetic element id in path)

Complexity
  build:  O(total leaves)
  query:  O(query nodes * (touched-set size))
  space:  O(total leaves)
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InvertedIndexInterview {

    /** index[path][value] -> names having that value at that path */
    private final Map<List<String>, Map<Object, Set<String>>> index = new HashMap<>();

    public InvertedIndexInterview(Map<String, Object> metadata) {
        for (Map.Entry<String, Object> e : metadata.entrySet()) {
            flatten(e.getKey(), new ArrayList<>(), e.getValue());
        }
    }

    /* ---------- build ---------- */

    @SuppressWarnings("unchecked")
    private void flatten(String name, List<String> path, Object node) {
        if (node == null) return;
        if (node instanceof Map) {
            // Object: extend path by each key, recurse.
            for (Map.Entry<String, Object> e : ((Map<String, Object>) node).entrySet()) {
                List<String> sub = new ArrayList<>(path.size() + 1);
                sub.addAll(path);
                sub.add(e.getKey());
                flatten(name, sub, e.getValue());
            }
        } else if (node instanceof List) {
            // Array: every element gets indexed AT THE PARENT path.
            // This is what makes {tags: "beta"} mean "tags contains beta" for free.
            for (Object elem : (List<?>) node) flatten(name, path, elem);
        } else {
            // Scalar leaf.
            index.computeIfAbsent(new ArrayList<>(path), k -> new HashMap<>())
                 .computeIfAbsent(node, k -> new HashSet<>())
                 .add(name);
        }
    }

    /* ---------- query ---------- */

    public Set<String> execute(Object query) {
        return new TreeSet<>(evaluate(query, new ArrayList<>()));
    }

    @SuppressWarnings("unchecked")
    private Set<String> evaluate(Object node, List<String> path) {
        if (node instanceof Map) {                                   // sibling AND
            Map<String, Object> m = (Map<String, Object>) node;
            Set<String> acc = null;
            for (Map.Entry<String, Object> e : m.entrySet()) {
                List<String> sub = new ArrayList<>(path.size() + 1);
                sub.addAll(path);
                sub.add(e.getKey());
                Set<String> r = evaluate(e.getValue(), sub);
                acc = (acc == null) ? r : intersect(acc, r);
                if (acc.isEmpty()) return acc;                       // short-circuit
            }
            return acc == null ? new HashSet<>() : acc;
        }
        if (node instanceof List) {                                  // array = OR
            Set<String> acc = new HashSet<>();
            for (Object v : (List<?>) node) acc.addAll(evaluate(v, path));
            return acc;
        }
        // Scalar leaf: equality lookup at the current path.
        Map<Object, Set<String>> bucket = index.get(path);
        if (bucket == null) return new HashSet<>();
        Set<String> r = bucket.get(node);
        return r == null ? new HashSet<>() : new HashSet<>(r);
    }

    private static Set<String> intersect(Set<String> a, Set<String> b) {
        if (a.size() > b.size()) { Set<String> t = a; a = b; b = t; } // iterate smaller
        Set<String> out = new HashSet<>();
        for (String x : a) if (b.contains(x)) out.add(x);
        return out;
    }

    /* ---------- demo ---------- */

    public static void main(String[] args) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("app1", obj("region", "us", "tier", "premium",
                             "tags",  list("beta", "stable"),
                             "owner", obj("team", "search")));
        meta.put("app2", obj("region", "us", "tier", "basic",
                             "tags",  list("beta"),
                             "owner", obj("team", "infra")));
        meta.put("app3", obj("region", "ca", "tier", "premium",
                             "owner", obj("team", "search")));

        InvertedIndexInterview idx = new InvertedIndexInterview(meta);

        check("region=us",                idx.execute(obj("region", "us")),                       set("app1", "app2"));
        check("tags contains beta",       idx.execute(obj("tags", "beta")),                        set("app1", "app2"));
        check("AND region=us tier=basic", idx.execute(obj("region", "us", "tier", "basic")),       set("app2"));
        check("nested owner.team=search", idx.execute(obj("owner", obj("team", "search"))),        set("app1", "app3"));
        check("region in [us, ca]",       idx.execute(obj("region", list("us", "ca"))),            set("app1", "app2", "app3"));
        check("no match",                 idx.execute(obj("region", "mars")),                      set());
    }

    /* ---------- helpers ---------- */

    private static Map<String, Object> obj(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    private static List<Object> list(Object... xs) {
        List<Object> out = new ArrayList<>(xs.length);
        for (Object x : xs) out.add(x);
        return out;
    }

    private static Set<String> set(String... xs) {
        Set<String> s = new TreeSet<>();
        for (String x : xs) s.add(x);
        return s;
    }

    private static void check(String label, Set<String> got, Set<String> expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
