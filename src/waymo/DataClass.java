package waymo;
/*
DataClass: tiny in-memory key/value store with get/set.

Java port of data_class_kv_store.py.

Spec
  obj = new DataClass();
  obj.set("a", "1");
  obj.get("a");   // -> "1"
  obj.get("b");   // -> null

Both keys and values are strings.

Stdin command format (matches the Python REPL):
  set c 3
  get c
  del c

Run options:
  java waymo.DataClass            -> runs the test suite
  java waymo.DataClass --stdin    -> runs tests, then reads commands from stdin

Notes
  * Java has no operator overloading, so the Python `db[k]` and `db[k] = v`
    sugar maps to explicit methods: `getStrict(k)` (throws on miss, like
    Python's `KeyError`) and `set(k, v)`.
  * Returning `null` for a missing key is the direct Java analog of Python's
    `None` and matches `Map.get` semantics.
  * `LinkedHashMap` preserves insertion order so iteration is predictable
    (mirrors Python's dict guarantees since 3.7+).

Complexity
  set / get / delete / contains / size : amortised O(1)
  iterator                              : O(n)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class DataClass implements Iterable<String> {

    private final Map<String, String> store = new LinkedHashMap<>();

    /* --------------------------- Required API --------------------------- */

    /** Store or overwrite {@code key -> value}. */
    public void set(String key, String value) {
        store.put(key, value);
    }

    /** Return the value for {@code key}, or {@code null} if absent. */
    public String get(String key) {
        return store.get(key);
    }

    /* --------------------------- Bonus dict-like helpers --------------------------- */

    /** Remove {@code key}.  Returns {@code true} if it existed, {@code false} otherwise. */
    public boolean delete(String key) {
        return store.remove(key) != null;
    }

    public boolean contains(String key) {
        return store.containsKey(key);
    }

    public int size() {
        return store.size();
    }

    public boolean isEmpty() {
        return store.isEmpty();
    }

    /**
     * Strict accessor: throws {@link NoSuchElementException} on miss.
     * Matches Python {@code db[key]} / {@code __getitem__} semantics.
     */
    public String getStrict(String key) {
        if (!store.containsKey(key)) {
            throw new NoSuchElementException("no value for key: " + key);
        }
        return store.get(key);
    }

    /** Iterates over keys in insertion order. */
    @Override
    public Iterator<String> iterator() {
        return store.keySet().iterator();
    }

    @Override
    public String toString() {
        return "DataClass" + store;
    }

    /* --------------------------- Tests --------------------------- */

    public static void main(String[] args) throws IOException {
        runTests();
        if (args.length > 0 && args[0].equals("--stdin")) {
            runStdin();
        }
    }

    private static void runTests() {
        // 1. Spec example.
        DataClass db = new DataClass();
        db.set("a", "1");
        expect("1".equals(db.get("a")), "spec set/get");
        expect(db.get("b") == null, "missing key returns null");

        // 2. Overwriting an existing key.
        db.set("a", "2");
        expect("2".equals(db.get("a")), "overwrite");

        // 3. Independent values.
        db.set("name", "Waymo");
        db.set("city", "Mountain View");
        expect("Waymo".equals(db.get("name")), "name");
        expect("Mountain View".equals(db.get("city")), "city");

        // 4. Empty string key and value (both are valid strings).
        db.set("", "empty-key");
        expect("empty-key".equals(db.get("")), "empty string key");
        db.set("blank-val", "");
        expect("".equals(db.get("blank-val")), "empty string value");

        // 5. Each DataClass instance is independent (regression for shared state).
        DataClass a = new DataClass();
        DataClass b = new DataClass();
        a.set("k", "from-a");
        expect(b.get("k") == null, "instances must not share state");

        // 6. delete() returns the right boolean and removes the entry.
        expect(db.delete("name"), "delete existing returns true");
        expect(db.get("name") == null, "deleted key now returns null");
        expect(!db.delete("nope"), "delete missing returns false");

        // 7. contains / getStrict.
        db.set("sugar", "ok");
        expect(db.contains("sugar"), "contains existing");
        expect("ok".equals(db.getStrict("sugar")), "getStrict existing");
        try {
            db.getStrict("does-not-exist");
            throw new AssertionError("getStrict should throw on miss");
        } catch (NoSuchElementException expected) {
            // ok
        }

        // 8. size + iteration in insertion order.
        DataClass fresh = new DataClass();
        for (int i = 0; i < 5; i++) fresh.set("k" + i, String.valueOf(i));
        expect(fresh.size() == 5, "size after 5 inserts");
        List<String> keys = new ArrayList<>();
        for (String k : fresh) keys.add(k);
        Collections.sort(keys);
        List<String> wantSorted = new ArrayList<>();
        for (int i = 0; i < 5; i++) wantSorted.add("k" + i);
        expect(keys.equals(wantSorted), "iteration covers all keys");

        // 9. Many writes followed by lookup — sanity.
        DataClass big = new DataClass();
        for (int i = 0; i < 10_000; i++) big.set(String.valueOf(i), String.valueOf(i * 2));
        expect("19998".equals(big.get("9999")), "stress 9999 -> 19998");
        expect(big.get("10000") == null, "stress 10000 missing");

        System.out.println("All tests passed  (" + fresh.size()
                + " entries in iter test, " + big.size() + " in stress test)");
    }

    private static void expect(boolean cond, String name) {
        if (!cond) {
            throw new AssertionError("FAIL: " + name);
        }
    }

    /* --------------------------- Stdin REPL --------------------------- */

    /**
     * Reads {@code set k v} / {@code get k} / {@code del k} commands from stdin
     * and prints results.  Values may contain spaces: the third field of
     * {@code set} captures the rest of the line, so {@code set greeting hello world}
     * stores {@code "hello world"}.
     */
    private static void runStdin() throws IOException {
        DataClass db = new DataClass();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            String[] parts = trimmed.split("\\s+", 3);
            String cmd = parts[0].toLowerCase();
            switch (cmd) {
                case "set":
                    if (parts.length >= 3) {
                        db.set(parts[1], parts[2]);
                    } else {
                        System.err.println("# ignored: " + line);
                    }
                    break;
                case "get":
                    if (parts.length == 2) {
                        String v = db.get(parts[1]);
                        System.out.println(v == null ? "None" : v);
                    } else {
                        System.err.println("# ignored: " + line);
                    }
                    break;
                case "del":
                    if (parts.length == 2) {
                        db.delete(parts[1]);
                    } else {
                        System.err.println("# ignored: " + line);
                    }
                    break;
                default:
                    System.err.println("# ignored: " + line);
            }
        }
    }
}
