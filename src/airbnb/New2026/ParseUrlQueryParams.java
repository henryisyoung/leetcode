package airbnb.New2026;
/*
Parse URL Query Parameters into a Map.

Given a URL string `url`, parse its query string and return all query
parameters as a key -> value map.

Rules
  - The query string begins after the FIRST '?'.
  - Each parameter has the form key=value.
  - Parameters are separated by '&'.
  - A bare key (e.g. "a" or "a=") maps to "".
  - No '?' or empty query -> empty map.
  - A fragment '#...' (if present after the query) is dropped.
  - Duplicate keys: LAST one wins (spec).
  - URL-decoding is optional; off by default.

I/O
  Input : url (String)
  Output: Map<String,String>

Constraints
  1 <= url.length() <= 1e5

Examples
  "https://example.com/path?foo=1&bar=2"      -> {foo=1, bar=2}
  "https://example.com/path"                  -> {}
  "https://example.com/path?"                 -> {}
  "https://example.com/path?empty&x="         -> {empty="", x=""}
  "https://example.com/path?a=1&b=2&b=3"      -> {a=1, b=3}   (last wins)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/*
Implementation notes

  * Single pass over the query substring. We track the start of the
    current parameter and the position of the '=' (if any) inside it.
    On '&' (or end-of-string) we materialise one (key, value) pair.

  * "Last one wins" falls out naturally: LinkedHashMap.put overwrites
    the previous value while preserving the insertion order of the
    first time each key was seen. If callers prefer "first wins",
    flip to putIfAbsent.

  * Fragment '#...' is stripped before parsing — '#' is not allowed
    inside a query value by RFC 3986, so anything after it is the
    fragment identifier and not part of the parameters.

  * URL decoding is off by default. The `decode` flag uses
    java.net.URLDecoder with UTF-8 — note that decoder turns '+' into
    a space, which is the standard application/x-www-form-urlencoded
    behavior. If you don't want that, pre-replace '+' or write a
    %xx-only decoder.

  * Edge cases handled:
       "...?"              -> {}
       "...?&"             -> {}      (empty segment skipped)
       "...?&&a=1"         -> {a=1}
       "...?=v"            -> {"": "v"} treated as empty-key
       "...?k=a=b"         -> {k="a=b"} only the FIRST '=' splits
       "...?k=v#frag"      -> {k=v}    fragment dropped
       null url            -> {}

Complexity
  Time:   O(n)   single pass + O(n) total for substring allocations
  Memory: O(n)   for the returned map's keys/values
*/
public class ParseUrlQueryParams {

    /** Default: no URL-decoding, last-occurrence wins. */
    public Map<String, String> parse(String url) {
        return parse(url, false);
    }

    public Map<String, String> parse(String url, boolean decode) {
        Map<String, String> out = new LinkedHashMap<>();
        if (url == null) return out;

        int q = url.indexOf('?');
        if (q < 0 || q == url.length() - 1) return out;

        // Query starts after '?', ends at '#' (fragment) or end-of-string.
        int end = url.indexOf('#', q + 1);
        if (end < 0) end = url.length();
        if (end <= q + 1) return out;

        String query = url.substring(q + 1, end);

        int start = 0;
        int eq = -1;
        int n = query.length();
        for (int i = 0; i <= n; i++) {
            if (i == n || query.charAt(i) == '&') {
                if (i > start) {                           // skip empty segments like "&&"
                    String key, val;
                    if (eq < 0) {
                        key = query.substring(start, i);
                        val = "";
                    } else {
                        key = query.substring(start, eq);
                        val = query.substring(eq + 1, i);  // ok when eq+1 == i -> ""
                    }
                    if (decode) {
                        key = urlDecode(key);
                        val = urlDecode(val);
                    }
                    out.put(key, val);                     // last one wins
                }
                start = i + 1;
                eq = -1;
            } else if (eq < 0 && query.charAt(i) == '=') {
                eq = i;                                    // only the FIRST '=' splits
            }
        }
        return out;
    }

    private static String urlDecode(String s) {
        try {
            return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return s;                                      // tolerate malformed %xx
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
        check("ex1", solver.parse("https://example.com/path?foo=1&bar=2"),
                kv("foo", "1", "bar", "2"));
        check("ex2", solver.parse("https://example.com/path"),
                kv());
        check("ex3", solver.parse("https://example.com/path?"),
                kv());
        check("ex4", solver.parse("https://example.com/path?empty&x="),
                kv("empty", "", "x", ""));
        check("ex5 last-wins", solver.parse("https://example.com/path?a=1&b=2&b=3"),
                kv("a", "1", "b", "3"));

        // ---- Edge cases ----
        check("null url", solver.parse(null), kv());
        check("only ?", solver.parse("?"), kv());
        check("empty segments", solver.parse("h://x?&&a=1&&"), kv("a", "1"));
        check("empty key", solver.parse("h://x?=v"), kv("", "v"));
        check("multi '=' in value", solver.parse("h://x?k=a=b=c"), kv("k", "a=b=c"));
        check("fragment dropped", solver.parse("h://x?k=v#frag"), kv("k", "v"));
        check("fragment only", solver.parse("h://x?#frag"), kv());
        check("query before fragment, no value", solver.parse("h://x?k#frag"), kv("k", ""));

        // ---- Optional URL decoding ----
        check("decode %20", solver.parse("h://x?q=hello%20world", true),
                kv("q", "hello world"));
        check("decode '+' as space (form-encoded)", solver.parse("h://x?q=a+b", true),
                kv("q", "a b"));
        check("no decode by default", solver.parse("h://x?q=hello%20world"),
                kv("q", "hello%20world"));

        // ---- Stress: ~1e5 chars ----
        StringBuilder big = new StringBuilder("https://x/path?");
        int pairs = 20_000;                                // ~ "kNNNNN=vNNNNN&" each ~14 chars
        for (int i = 0; i < pairs; i++) {
            if (i > 0) big.append('&');
            big.append("k").append(i).append('=').append("v").append(i);
        }
        long t0 = System.nanoTime();
        Map<String, String> m = solver.parse(big.toString());
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress len=" + big.length() + " pairs=" + m.size() + " in " + ms + " ms");
    }

    /** Build an expected map preserving insertion order. */
    private static Map<String, String> kv(String... kvs) {
        if ((kvs.length & 1) != 0) throw new IllegalArgumentException("kv needs even args");
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kvs.length; i += 2) m.put(kvs[i], kvs[i + 1]);
        return m;
    }

    private static void check(String label, Map<String, String> got, Map<String, String> expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label);
        if (!ok) {
            System.out.println("  expected: " + expected);
            System.out.println("  got     : " + got);
        }
    }
}
