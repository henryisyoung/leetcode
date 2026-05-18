package airbnb.New2026;
/*
Deduplicate user account registrations  (Airbnb phone-screen).

Input: a list of account entries in registration order. Each entry
is a map from attribute name to value (e.g. {name, email, phone,
region, ssn, ...}). A subset of those attribute names is designated
"unique-identifying keys" (email, phone, ssn, ...). A new entry is
considered a DUPLICATE if ANY of its unique-key values has already
been registered by an earlier entry. First-come-first-served.

Return the 0-based indices of all duplicate entries, in input order.

I/O
  Input : List<Map<String,String>> entries, Set<String> uniqueKeys
  Output: List<Integer> (indices of duplicates)

Constraints (typical)
  - Entries up to ~1e5.
  - Per-entry attribute count "small" (a handful).
  - Data is uniformly formatted — every entry contains every
    declared unique-key attribute. (Per the prompt.)

Example
  uniqueKeys = {email, phone}
  entries = [
    {name=john,   email=john@…,  phone=800 800 8800},          // 0  registers
    {name=amy,    email=amy@…,   phone=800 800 8800},          // 1  dup: phone
    {name=johnny, email=john@…,  phone=800 800 8888},          // 2  dup: email
  ]
  -> [1, 2]

  Followup (more attributes — region, ssn, ...): same algorithm,
  just iterate over the larger uniqueKeys set. The implementation
  below is already generic over an arbitrary number of attributes.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
Algorithm — one pass with per-attribute Sets.

  For each unique key K, keep a HashSet<String> of values already
  claimed by some earlier successful registration. Walk entries in
  order:

      for each entry:
          isDup = false
          for K in uniqueKeys:
              v = entry.get(K)
              if v != null and v is already in seen[K]:
                  isDup = true; break
          if isDup:
              record i as duplicate
          else:
              for K in uniqueKeys:
                  add entry.get(K) to seen[K]

  Two important details:

    1. "First-wins" means we register the FIRST entry to claim a value
       and reject every subsequent entry that touches any of those
       values. So we only insert into seen[K] when the entry survives
       all checks — never insert from a duplicate, otherwise we'd
       block later entries that try the same value.

    2. The algorithm is intentionally NOT transitive. If two entries
       collide on phone, the second is rejected; that does NOT make a
       third entry colliding on email-with-the-rejected-second a
       duplicate. (Per the spec: "amy registers, johnny registers" —
       they're independently rejected by john's already-claimed
       values.) If a future requirement asks for transitive merging
       across attributes, swap the Set<String> for a Union-Find keyed
       on (attribute, value) — the rest of the loop stays the same.

  Followup readiness
    The "extra attribute" extension is free: passing a larger
    uniqueKeys set works as-is. We also expose a convenience method
    that auto-derives uniqueKeys as "every key seen except for a
    blacklist of informational fields" (name/region by default),
    which matches a common interview follow-up.

Complexity
  Time:   O(n * k) where k = |uniqueKeys|
  Memory: O(total values inserted) — bounded by O(n * k)
*/
public class DedupeUserAccounts {

    /** Default informational attributes that should NOT be treated as uniqueness keys. */
    public static final Set<String> DEFAULT_INFO_ATTRS =
            new HashSet<>(Arrays.asList("name", "region"));

    /** Core API: explicit set of unique-identifying attributes. */
    public List<Integer> findDuplicates(List<Map<String, String>> entries,
                                        Set<String> uniqueKeys) {
        if (entries == null || entries.isEmpty()) return new ArrayList<>();
        if (uniqueKeys == null || uniqueKeys.isEmpty()) {
            return new ArrayList<>();                          // nothing identifies an entry -> no dupes
        }

        Map<String, Set<String>> seen = new HashMap<>();
        for (String k : uniqueKeys) seen.put(k, new HashSet<>());

        List<Integer> dups = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            Map<String, String> e = entries.get(i);
            if (e == null) continue;
            boolean isDup = false;
            for (String k : uniqueKeys) {
                String v = e.get(k);
                if (v != null && seen.get(k).contains(v)) {
                    isDup = true;
                    break;
                }
            }
            if (isDup) {
                dups.add(i);
            } else {
                // Register this entry's values for every unique key.
                for (String k : uniqueKeys) {
                    String v = e.get(k);
                    if (v != null) seen.get(k).add(v);
                }
            }
        }
        return dups;
    }

    /**
     * Convenience: auto-derive unique-keys as every attribute name observed
     * in `entries`, minus an "informational" blacklist (name, region by default).
     */
    public List<Integer> findDuplicates(List<Map<String, String>> entries) {
        return findDuplicates(entries, deriveUniqueKeys(entries, DEFAULT_INFO_ATTRS));
    }

    public List<Integer> findDuplicates(List<Map<String, String>> entries,
                                        Set<String> infoAttributes,
                                        boolean infoAreBlacklist) {
        if (!infoAreBlacklist) {
            return findDuplicates(entries, infoAttributes);    // whitelist
        }
        return findDuplicates(entries, deriveUniqueKeys(entries, infoAttributes));
    }

    private static Set<String> deriveUniqueKeys(List<Map<String, String>> entries,
                                                Set<String> infoBlacklist) {
        Set<String> all = new LinkedHashSet<>();
        if (entries != null) {
            for (Map<String, String> e : entries) {
                if (e != null) all.addAll(e.keySet());
            }
        }
        if (infoBlacklist != null) all.removeAll(infoBlacklist);
        return all;
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    /**
     * Stdin format:
     *   line 1: uniqueKeys, comma-separated (e.g. "email,phone")
     *   line 2: N (number of entries)
     *   next N lines: "k1=v1; k2=v2; k3=v3"  (semicolon-separated pairs)
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Set<String> uniqueKeys = new LinkedHashSet<>(Arrays.asList(br.readLine().split(",")));
        int n = Integer.parseInt(br.readLine().trim());
        List<Map<String, String>> entries = new ArrayList<>(n);
        for (int i = 0; i < n; i++) entries.add(parseEntry(br.readLine()));
        System.out.println(new DedupeUserAccounts().findDuplicates(entries, uniqueKeys));
    }

    private static Map<String, String> parseEntry(String line) {
        Map<String, String> m = new LinkedHashMap<>();
        if (line == null) return m;
        for (String pair : line.split(";")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) m.put(kv[0].trim(), kv[1].trim());
        }
        return m;
    }

    private static void runDemos() {
        DedupeUserAccounts solver = new DedupeUserAccounts();

        // ---- Spec example 1: name/email/phone, uniqueKeys = {email, phone} ----
        List<Map<String, String>> ex1 = Arrays.asList(
                entry("name", "john",   "email", "john@example.com",  "phone", "800 800 8800"),
                entry("name", "amy",    "email", "amy@example.com",   "phone", "800 800 8800"),
                entry("name", "johnny", "email", "john@example.com",  "phone", "800 800 8888"));
        check("ex1 explicit keys",
                solver.findDuplicates(ex1, setOf("email", "phone")),
                Arrays.asList(1, 2));

        // The convenience overload should agree: name is on the info blacklist,
        // so derived keys = {email, phone}.
        check("ex1 auto-derived keys (name blacklisted)",
                solver.findDuplicates(ex1),
                Arrays.asList(1, 2));

        // ---- Followup: with region (still expect [1, 2]) ----
        List<Map<String, String>> ex2 = Arrays.asList(
                entry("name", "john",   "email", "john@example.com",  "phone", "800 800 8800", "region", "us"),
                entry("name", "amy",    "email", "amy@example.com",   "phone", "800 800 8800", "region", "jp"),
                entry("name", "johnny", "email", "john@example.com",  "phone", "800 800 8888", "region", "au"));
        check("ex2 explicit keys {email, phone}",
                solver.findDuplicates(ex2, setOf("email", "phone")),
                Arrays.asList(1, 2));
        check("ex2 auto-derived (name+region blacklisted)",
                solver.findDuplicates(ex2),
                Arrays.asList(1, 2));

        // ---- If region were ALSO a uniqueness key, all three would be distinct ----
        check("ex2 with region as unique key -> no dupes",
                solver.findDuplicates(ex2, setOf("email", "phone", "region")),
                Arrays.asList(1, 2));
        //   Wait: region values {us, jp, au} are distinct, so adding region as
        //   a unique key doesn't UNDO the email/phone duplicates. Still [1, 2].

        // ---- ssn-style additional attribute ----
        List<Map<String, String>> ex3 = Arrays.asList(
                entry("name", "a", "email", "a@x", "phone", "p1", "ssn", "111"),
                entry("name", "b", "email", "b@x", "phone", "p2", "ssn", "111"),  // dup on ssn
                entry("name", "c", "email", "c@x", "phone", "p3", "ssn", "222"));
        check("ssn collision",
                solver.findDuplicates(ex3, setOf("email", "phone", "ssn")),
                Arrays.asList(1));

        // ---- First-wins, not transitive: ----
        //   E0 registers (p1, e1). E1 has (p1, e2) -> dup on phone.
        //   E2 has (p2, e2)        -> dup on email IF e2 was claimed; but e2
        //                              was claimed by E1 which was REJECTED, so
        //                              e2 is still free -> E2 is NOT a dup.
        List<Map<String, String>> ex4 = Arrays.asList(
                entry("email", "e1", "phone", "p1"),
                entry("email", "e2", "phone", "p1"),                            // dup phone
                entry("email", "e2", "phone", "p2"));                           // NOT dup
        check("rejected entry doesn't claim values",
                solver.findDuplicates(ex4, setOf("email", "phone")),
                Arrays.asList(1));

        // ---- Edge cases ----
        check("empty entries",
                solver.findDuplicates(new ArrayList<>(), setOf("email")),
                new ArrayList<Integer>());
        check("no unique keys -> no dupes",
                solver.findDuplicates(ex1, new HashSet<>()),
                new ArrayList<Integer>());
        check("single entry",
                solver.findDuplicates(Arrays.asList(entry("email", "x")), setOf("email")),
                new ArrayList<Integer>());
        check("all identical",
                solver.findDuplicates(Arrays.asList(
                        entry("email", "x"), entry("email", "x"), entry("email", "x")),
                        setOf("email")),
                Arrays.asList(1, 2));

        // ---- Stress: 1e5 entries, 4 unique keys, ~half duplicates ----
        int N = 100_000;
        List<Map<String, String>> big = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            int g = i / 2;                                    // pairs share the same email+phone
            big.add(entry("email", "u" + g + "@x", "phone", "p" + g,
                          "ssn", "s" + i, "region", "r" + (i % 5)));
        }
        long t0 = System.nanoTime();
        List<Integer> dups = solver.findDuplicates(big, setOf("email", "phone"));
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress count", dups.size(), N / 2);
        System.out.println("Stress n=" + N + " dups=" + dups.size() + " in " + ms + " ms");
    }

    /* --------------------------- helpers --------------------------- */

    private static Map<String, String> entry(String... kvs) {
        if ((kvs.length & 1) != 0) throw new IllegalArgumentException("entry needs even args");
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kvs.length; i += 2) m.put(kvs[i], kvs[i + 1]);
        return m;
    }

    private static Set<String> setOf(String... xs) {
        return new LinkedHashSet<>(Arrays.asList(xs));
    }

    private static void check(String label, Object got, Object expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
