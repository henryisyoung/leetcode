package airbnb.New2026;
/*
Parse URL Query Parameters into a Map.

Given a URL string `url`, parse its query string and return all query
parameters as a key -> value map.

Rules
  - The query string begins after the FIRST '?'.
  - Each parameter is key=value, separated by '&'.
  - A bare key without '=' maps to Boolean.TRUE.
    (Some interviewers prefer ""; clarify before coding.)
  - A key with '=' but empty value maps to "".
  - No '?' or empty query -> empty map.
  - Duplicate keys collect values into a List.

I/O
  Input : url (String)
  Output: Map<String,Object>

Constraints
  1 <= url.length() <= 1e5

Examples
  "https://example.com/path?foo=1&bar=2"      -> {foo=1, bar=2}
  "https://example.com/path"                  -> {}
  "https://example.com/path?"                 -> {}
  "?a=b&c=d"                                  -> {a=b, c=d}
  "?sd"                                       -> {sd=true}
  "?a=b&a=c&a=d"                              -> {a=[b, c, d]}
  "?key=hello%20world"                        -> {key=hello%20world}
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
Implementation notes

  * Single pass over the query substring. We track the start of the current
    parameter and the position of the '=' (if any) inside it. On '&' (or
    end-of-string) we materialise one (key, value) pair.

  * Repeated keys are folded into a List while preserving encounter order:
        a=b&a=c -> {a=[b, c]}

Complexity
  Time:   O(n)   single pass + O(n) total for substring allocations
  Memory: O(n)   for the returned map's keys/values
*/
public class ParseUrlQueryParams {

    /** Default parser: percent-decodes, bare keys map to Boolean.TRUE. */
    public Map<String, Object> parse(String url) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (url == null) return out;

        int q = url.indexOf('?');
        if (q < 0 || q == url.length() - 1) return out;

        String query = url.substring(q + 1);

        int start = 0;
        int eq = -1;
        int n = query.length();
        for (int i = 0; i <= n; i++) {
            if (i == n || query.charAt(i) == '&') {
                if (i > start) {
                    String key, val;
                    boolean hasEquals = eq >= 0;
                    if (eq < 0) {
                        key = query.substring(start, i);
                        val = null;
                    } else {
                        key = query.substring(start, eq);
                        val = query.substring(eq + 1, i);  // ok when eq+1 == i -> ""
                    }
                    Object parsedValue;
                    if (!hasEquals) {
                        parsedValue = Boolean.TRUE;
                    } else {
                        parsedValue = val;
                    }
                    addValue(out, key, parsedValue);
                }
                start = i + 1;
                eq = -1;
            } else if (eq < 0 && query.charAt(i) == '=') {
                eq = i;                                    // only the FIRST '=' splits
            }
        }
        return out;
    }

    private static void addValue(Map<String, Object> out, String key, Object val) {
        Object old = out.get(key);
        if (old == null && !out.containsKey(key)) {
            out.put(key, val);
        } else if (old instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) old;
            list.add(val);
        } else {
            List<Object> list = new java.util.ArrayList<>();
            list.add(old);
            list.add(val);
            out.put(key, list);
        }
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

    /** Stdin: one URL per line. Prints the parsed map per line. */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ParseUrlQueryParams solver = new ParseUrlQueryParams();
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(solver.parse(line));
        }
    }

    private static void runDemos() {
        ParseUrlQueryParams solver = new ParseUrlQueryParams();

        // ---- Spec tests ----
        check("ex1", solver.parse("?a=b&c=d"),
                kv("a", "b", "c", "d"));
        check("ex2", solver.parse("https://example.com/path"),
                kv());
        check("ex3", solver.parse("https://example.com/path?"),
                kv());
        check("ex4", solver.parse("https://example.com/path?empty&x="),
                kv("empty", true, "x", ""));
        check("ex5 repeated list", solver.parse("?a=b&a=c&a=d"),
                kv("a", list("b", "c", "d")));
        check("percent preserved", solver.parse("?key=hello%20world"),
                kv("key", "hello%20world"));

        // ---- Edge cases ----
        check("null url", solver.parse(null), kv());
        check("only ?", solver.parse("?"), kv());
        check("empty key", solver.parse("h://x?=v"), kv("", "v"));
        check("multi '=' in value", solver.parse("h://x?k=a=b=c"), kv("k", "a=b=c"));
        check("plus preserved", solver.parse("h://x?q=a+b"),
                kv("q", "a+b"));

        // ---- Stress: ~1e5 chars ----
        StringBuilder big = new StringBuilder("https://x/path?");
        int pairs = 20_000;                                // ~ "kNNNNN=vNNNNN&" each ~14 chars
        for (int i = 0; i < pairs; i++) {
            if (i > 0) big.append('&');
            big.append("k").append(i).append('=').append("v").append(i);
        }
        long t0 = System.nanoTime();
        Map<String, Object> m = solver.parse(big.toString());
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress len=" + big.length() + " pairs=" + m.size() + " in " + ms + " ms");
    }

    /** Build an expected map preserving insertion order. */
    private static Map<String, Object> kv(Object... kvs) {
        if ((kvs.length & 1) != 0) throw new IllegalArgumentException("kv needs even args");
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kvs.length; i += 2) m.put((String) kvs[i], kvs[i + 1]);
        return m;
    }

    private static List<Object> list(Object... xs) {
        return java.util.Arrays.asList(xs);
    }

    private static void check(String label, Map<String, Object> got, Map<String, Object> expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label);
        if (!ok) {
            System.out.println("  expected: " + expected);
            System.out.println("  got     : " + got);
        }
    }
}
