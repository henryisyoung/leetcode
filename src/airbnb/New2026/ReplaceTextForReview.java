package airbnb.New2026;
/*
Replace Text for Review.

Given a piece of `text` and a `dictionary` of (word -> replacement),
return the text after replacing every occurrence of each dictionary
word with its mapped value.  Replacements happen at WORD BOUNDARIES,
not as raw substring replace — "quickly" must not match "quick".

I/O
  Input  : text (String), dictionary (Map<String,String>)
  Output : modified string

Examples
  text = "The quick brown fox"
  dict = {"quick": "slow", "brown": "black"}
  out  = "The slow black fox"

  text = "The quick, brown fox jumped quickly."
  dict = {"quick": "slow"}
  out  = "The slow, brown fox jumped quickly."     // "quickly" untouched

  text = "Hello world"
  dict = {"world": "everyone"}
  out  = "Hello everyone"

Decisions / clarifications (each is a one-liner to flip)
  - WORD BOUNDARY: a "word" is a maximal run of [A-Za-z0-9_] (the
    standard \w class).  Punctuation, spaces, etc. delimit words and
    are preserved verbatim in the output.
  - CASE SENSITIVE by default.  Constructor flag `caseInsensitive`
    lowercases tokens before lookup; replacement string is emitted
    as-is (case of the replacement wins).
  - REPLACEMENTS DO NOT CASCADE.  If "a"->"b" and "b"->"c", the input
    "a" becomes "b", not "c".  Single linear pass over the text.
  - DUPLICATE keys in the input map: the Map itself handles that
    (last put wins) — we don't re-validate.

Constraints (typical interview targets)
  1 <= |text| <= 1e6
  1 <= |dict| <= 1e5
  Sum of dictionary key/value lengths bounded by upstream usage.

Complexity
  Single linear scan: O(|text| + sum of emitted replacement lengths).
  Map lookup is O(1) amortized per word.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
Implementation notes

  * One pass over the text.  We accumulate characters of the current
    word in `[start, i)` and flush on the first non-word char (or
    end of text); on flush we look up `text.substring(start, i)` in
    the dictionary and emit either the replacement or the original.

  * For case-insensitive mode we lowercase only the LOOKUP KEY; the
    original casing of the source word is irrelevant because the
    output uses the replacement string verbatim when matched, and
    the original characters otherwise.  If you need "preserve casing
    of source" (e.g. "Quick" -> "Slow" not "slow"), apply a casing
    transform to the replacement at emit time.

  * StringBuilder sized to roughly text length to avoid early resizing.
    Worst case (every word replaced by a longer string) it still grows
    geometrically; nothing pathological.

  * For very large dictionaries with multi-word keys (e.g. "New York"
    -> "NYC"), this approach won't work and you'd reach for an
    Aho-Corasick automaton or a trie walk.  Mentioned as a follow-up
    in the doc above; not needed for the spec example.
*/
public class ReplaceTextForReview {

    private final boolean caseInsensitive;

    public ReplaceTextForReview() { this(false); }
    public ReplaceTextForReview(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /** Returns `text` with every dictionary word replaced. */
    public String replace(String text, Map<String, String> dict) {
        if (text == null || text.isEmpty()) return "";
        if (dict == null || dict.isEmpty()) return text;

        // For case-insensitive mode we use a lowercased key view.
        Map<String, String> lookup = dict;
        if (caseInsensitive) {
            lookup = new HashMap<>(dict.size() * 2);
            for (Map.Entry<String, String> e : dict.entrySet()) {
                if (e.getKey() != null) lookup.put(e.getKey().toLowerCase(), e.getValue());
            }
        }

        int n = text.length();
        StringBuilder out = new StringBuilder(n);
        int i = 0;
        while (i < n) {
            char c = text.charAt(i);
            if (isWordChar(c)) {
                int start = i++;
                while (i < n && isWordChar(text.charAt(i))) i++;
                String word = text.substring(start, i);
                String key = caseInsensitive ? word.toLowerCase() : word;
                String repl = lookup.get(key);
                out.append(repl != null ? repl : word);
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    /** A "word" character is a letter, digit, or underscore (matches regex \w). */
    private static boolean isWordChar(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '_';
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
     * Stdin format (matches the prompt):
     *   one line of the form:  "<text>;{'k1':'v1','k2':'v2'}"
     * Quotes can be single or double; whitespace inside the dict is tolerated.
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        if (line == null) return;
        int sep = line.lastIndexOf(';');
        String text = sep >= 0 ? line.substring(0, sep) : line;
        Map<String, String> dict = sep >= 0 ? parseDict(line.substring(sep + 1)) : new HashMap<>();
        System.out.println(new ReplaceTextForReview().replace(text, dict));
    }

    /** Tolerant parser for "{'k1':'v1','k2':'v2'}" / "{\"k\":\"v\"}" / "{}" forms. */
    static Map<String, String> parseDict(String s) {
        Map<String, String> out = new LinkedHashMap<>();
        if (s == null) return out;
        s = s.trim();
        if (s.startsWith("{")) s = s.substring(1);
        if (s.endsWith("}"))   s = s.substring(0, s.length() - 1);
        s = s.trim();
        if (s.isEmpty()) return out;

        int i = 0, n = s.length();
        while (i < n) {
            // skip whitespace and commas
            while (i < n && (s.charAt(i) == ' ' || s.charAt(i) == ',')) i++;
            if (i >= n) break;
            String key = readToken(s, i);
            i += keyAdvance(s, i, key);
            // expect ':'
            while (i < n && s.charAt(i) != ':') i++;
            if (i >= n) break;
            i++;                                       // skip ':'
            while (i < n && s.charAt(i) == ' ') i++;
            String val = readToken(s, i);
            i += keyAdvance(s, i, val);
            out.put(key, val);
        }
        return out;
    }

    private static String readToken(String s, int i) {
        if (i >= s.length()) return "";
        char q = s.charAt(i);
        if (q == '\'' || q == '"') {
            int end = s.indexOf(q, i + 1);
            return end < 0 ? s.substring(i + 1) : s.substring(i + 1, end);
        }
        // Bare token: read until ',', ':', or whitespace.
        int j = i;
        while (j < s.length() && ",:".indexOf(s.charAt(j)) < 0
                && !Character.isWhitespace(s.charAt(j))) j++;
        return s.substring(i, j);
    }

    /** Number of characters consumed for a token starting at `i`. */
    private static int keyAdvance(String s, int i, String token) {
        if (i >= s.length()) return 0;
        char q = s.charAt(i);
        if (q == '\'' || q == '"') return token.length() + 2;     // open + content + close quote
        return token.length();
    }

    private static void runDemos() {
        ReplaceTextForReview solver = new ReplaceTextForReview();

        // Spec example.
        Map<String, String> d1 = new LinkedHashMap<>();
        d1.put("quick", "slow");
        d1.put("brown", "black");
        check(solver, "The quick brown fox", d1, "The slow black fox");

        // Word boundary: "quickly" must NOT be replaced even though "quick" is a key.
        Map<String, String> d2 = new LinkedHashMap<>();
        d2.put("quick", "slow");
        check(solver, "The quick brown fox jumped quickly.", d2,
                "The slow brown fox jumped quickly.");

        // Punctuation preserved around words.
        check(solver, "The quick, brown fox.", d1, "The slow, black fox.");

        // Multiple occurrences all replaced.
        Map<String, String> d3 = new LinkedHashMap<>();
        d3.put("fox", "wolf");
        check(solver, "fox fox FOX foxes", d3, "wolf wolf FOX foxes");
        //                                ^---- case-sensitive  ^--- "foxes" not a whole word

        // Empty text / empty dict are no-ops.
        check(solver, "", d1, "");
        check(solver, "hello", new HashMap<>(), "hello");

        // Replacement longer than original.
        Map<String, String> d4 = new LinkedHashMap<>();
        d4.put("hi", "greetings");
        check(solver, "hi all", d4, "greetings all");

        // Replacement empty -> word is "removed" but its surrounding whitespace stays.
        Map<String, String> d5 = new LinkedHashMap<>();
        d5.put("the", "");
        check(solver, "the cat and the dog", d5, " cat and  dog");

        // Numbers / underscores are word chars; "v_2" matches "v_2".
        Map<String, String> d6 = new LinkedHashMap<>();
        d6.put("v_2", "v2");
        check(solver, "release v_2 now", d6, "release v2 now");

        // No cascade: a -> b, b -> c; "a b" becomes "b c", not "c c".
        Map<String, String> d7 = new LinkedHashMap<>();
        d7.put("a", "b");
        d7.put("b", "c");
        check(solver, "a b", d7, "b c");

        // ---- Case-insensitive mode ----
        ReplaceTextForReview ci = new ReplaceTextForReview(true);
        Map<String, String> dci = new LinkedHashMap<>();
        dci.put("foo", "BAR");
        // "Foo", "FOO", "FoO" all match; replacement emitted verbatim ("BAR").
        check(ci, "Foo FOO FoO foo", dci, "BAR BAR BAR BAR");

        // ---- Stress: long text, many keys ----
        StringBuilder big = new StringBuilder();
        java.util.Random rnd = new java.util.Random(1);
        Map<String, String> bigDict = new HashMap<>();
        for (int i = 0; i < 1_000; i++) bigDict.put("w" + i, "x" + i);
        for (int i = 0; i < 200_000; i++) {
            if (i > 0) big.append(' ');
            big.append('w').append(rnd.nextInt(2_000));
        }
        long t0 = System.nanoTime();
        String out = solver.replace(big.toString(), bigDict);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress 200k words, 1k dict: " + out.length() + " chars in " + ms + " ms");

        // ---- Parser sanity ----
        Map<String, String> parsed = parseDict("{'quick':'slow','brown':'black'}");
        check(solver, "The quick brown fox", parsed, "The slow black fox");
    }

    private static void check(ReplaceTextForReview solver,
                              String text, Map<String, String> dict, String expected) {
        String got = solver.replace(text, dict);
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ")
                + "text=\"" + text + "\" dict=" + dict
                + " expected=\"" + expected + "\" got=\"" + got + "\"");
    }
}
