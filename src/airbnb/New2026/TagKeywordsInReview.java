package airbnb.New2026;
/*
Tag keywords in a user review.

Given a map of keyword -> tag, replace each occurrence of a keyword
in the review with "[<tag>]{<originalKeywordText>}".

Subtle requirements (all visible in the worked example):

  1. CASE-INSENSITIVE matching, CASE-PRESERVING output.
        keyword "san"  matches "San", "san", "SAN" — the [person]{San}
        tag wraps the ORIGINAL substring with its original case.

  2. LONGEST MATCH WINS.
        "San Francisco" matches keyword "san francisco" -> [city] rather
        than two separate "san" + "francisco" replacements.

  3. WORD-BOUNDARY matching only.
        "san's" matches "san" (the apostrophe is a word boundary).
        "Francisco" inside another word would NOT match.

  4. The keyword text itself can contain SPACES ("san francisco");
     the space is part of the literal sequence to match.

I/O
  Input : Map<String,String> keywordsToTags, String review
  Output: String with each keyword wrapped as [<tag>]{<original>}

Constraints (typical)
  total keywords up to ~1e3, total chars ~1e5.
  Letters are matched case-insensitively in ASCII; other characters
  (digits, punctuation, whitespace) are matched literally.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
Algorithm — trie + longest-match scan.

  Build a trie keyed on LOWERCASED characters from every keyword.
  Each terminal node stores the tag for that keyword (the original
  keyword string isn't needed — we slice the original text by the
  matched length to preserve case).

  Scan the review with a single index `i`:
    - Word-boundary precondition: a match may BEGIN at i only if
        i == 0  OR  text[i-1] is not a "word character" (letter/digit).
      If not, just copy text[i] and advance.
    - Otherwise descend the trie from the root, lowercasing each
      character of the text as we go. Keep track of the deepest
      terminal node reached whose END position ALSO sits at a word
      boundary (`i+len == text.length() || !isWordChar(text[i+len])`).
    - If such a match exists, emit "[tag]{original}" and advance
      `i` past the match. Otherwise emit one character and advance
      by 1.

  Why "deepest terminal seen along the walk" (not just the first):
    We always need the LONGEST valid match. Different keywords may
    share a prefix ("san" and "san francisco"); the trie walk
    naturally exposes both as we descend, so we just remember the
    last terminal that also satisfies the right-side word boundary.

Edge cases handled
  - Keywords containing spaces (matched literally, case-insensitive
    on the letter portions).
  - Keys that are prefixes of other keys (longest wins).
  - Keyword right at the start or right at the end of the review.
  - Keyword followed by "'s" (apostrophe is a boundary; matches).
  - Empty review / empty map: returned unchanged.

Complexity
  Let n = |review|, k = total chars across all keywords, L = longest
  keyword length.
    build: O(k)
    scan:  O(n * L) worst case (typically << that — only word-start
                                positions descend the trie)
    space: O(k)
*/
public class TagKeywordsInReview {

    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        String tag;                                          // non-null at terminal nodes
    }

    private Node root;

    public TagKeywordsInReview(Map<String, String> keywordsToTags) {
        root = new Node();
        if (keywordsToTags == null) return;
        for (Map.Entry<String, String> e : keywordsToTags.entrySet()) {
            String key = e.getKey();
            String tag = e.getValue();
            if (key == null || key.isEmpty() || tag == null) continue;
            Node cur = root;
            for (int i = 0; i < key.length(); i++) {
                char c = Character.toLowerCase(key.charAt(i));
                cur = cur.children.computeIfAbsent(c, k_ -> new Node());
            }
            // Duplicate keyword: keep the LAST tag declared (LinkedHashMap-like).
            cur.tag = tag;
        }
    }

    public String tagReview(String review) {
        if (review == null || review.isEmpty()) return review == null ? "" : "";

        StringBuilder out = new StringBuilder(review.length() + 32);
        int n = review.length();
        int i = 0;
        while (i < n) {
            // Word-boundary precondition: only START matches at word boundaries.
            if (i > 0 && isWordChar(review.charAt(i - 1))) {
                out.append(review.charAt(i));
                i++;
                continue;
            }
            // Walk trie from `i`, remembering the longest valid (boundary-ending) match.
            Node cur = root;
            int bestLen = 0;
            String bestTag = null;
            for (int j = i; j < n; j++) {
                char c = Character.toLowerCase(review.charAt(j));
                Node next = cur.children.get(c);
                if (next == null) break;
                cur = next;
                if (cur.tag != null) {
                    int matchEnd = j + 1;
                    if (matchEnd == n || !isWordChar(review.charAt(matchEnd))) {
                        bestLen = matchEnd - i;
                        bestTag = cur.tag;
                    }
                }
            }
            if (bestLen > 0) {
                out.append('[').append(bestTag).append("]{")
                   .append(review, i, i + bestLen)
                   .append('}');
                i += bestLen;
            } else {
                out.append(review.charAt(i));
                i++;
            }
        }
        return out.toString();
    }

    /** Letters and digits constitute "inside-a-word" characters. */
    private static boolean isWordChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c);
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
     *   line 1: N (number of keyword entries)
     *   next N lines: "keyword<TAB>tag"
     *   remaining lines: the review (joined with newlines)
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            String[] parts = br.readLine().split("\t", 2);
            map.put(parts[0], parts[1]);
        }
        StringBuilder review = new StringBuilder();
        String ln;
        while ((ln = br.readLine()) != null) {
            if (review.length() > 0) review.append('\n');
            review.append(ln);
        }
        System.out.println(new TagKeywordsInReview(map).tagReview(review.toString()));
    }

    private static void runDemos() {
        // ---- Spec example ----
        Map<String, String> map = new LinkedHashMap<>();
        map.put("san",            "person");
        map.put("francisco",      "person");
        map.put("san francisco",  "city");
        map.put("Airbnb",         "business");
        map.put("city",           "location");

        TagKeywordsInReview solver = new TagKeywordsInReview(map);

        String review =
                "I travelled to San Francisco for work and stayed at Airbnb.\n"
              + "I really loved the city and the home where I stayed.\n"
              + "I stayed with San and Francisco.\n"
              + "They both were really good and san's hospitality was outstanding.";

        String expected =
                "I travelled to [city]{San Francisco} for work and stayed at [business]{Airbnb}.\n"
              + "I really loved the [location]{city} and the home where I stayed.\n"
              + "I stayed with [person]{San} and [person]{Francisco}.\n"
              + "They both were really good and [person]{san}'s hospitality was outstanding.";

        check("spec example", solver.tagReview(review), expected);

        // ---- Tiny direct checks ----
        check("longest wins (san vs san francisco)",
                solver.tagReview("San Francisco"), "[city]{San Francisco}");
        check("case preserved",
                solver.tagReview("SAN"), "[person]{SAN}");
        check("apostrophe boundary",
                solver.tagReview("san's"), "[person]{san}'s");
        check("middle-of-word doesn't match",
                solver.tagReview("Sanchez"), "Sanchez");      // 'san' is not at end-of-word here
        check("digit boundary",
                solver.tagReview("san3"), "san3");            // digit follows -> not a boundary
        check("punctuation boundary",
                solver.tagReview("Hello, san!"), "Hello, [person]{san}!");
        check("at start of text",
                solver.tagReview("Airbnb rocks"), "[business]{Airbnb} rocks");
        check("at end of text",
                solver.tagReview("I love Airbnb"), "I love [business]{Airbnb}");
        check("no match",
                solver.tagReview("hello world"), "hello world");
        check("empty review", solver.tagReview(""), "");
        check("null review",  solver.tagReview(null), "");

        // ---- "city" + "san francisco" cohabiting ----
        check("city inside another phrase doesn't trigger 'san francisco'",
                solver.tagReview("city san"), "[location]{city} [person]{san}");

        // ---- Duplicate keyword, last tag wins ----
        Map<String, String> dup = new LinkedHashMap<>();
        dup.put("foo", "first");
        dup.put("FOO", "second");                            // same trie path lowercased
        check("duplicate keyword (last wins)",
                new TagKeywordsInReview(dup).tagReview("foo"), "[second]{foo}");

        // ---- Empty / null mapping ----
        check("empty mapping passes text through",
                new TagKeywordsInReview(new HashMap<>()).tagReview("anything"), "anything");
        check("null mapping passes text through",
                new TagKeywordsInReview(null).tagReview("anything"), "anything");

        // ---- Stress: 1k keywords, 100k chars ----
        Map<String, String> bigMap = new HashMap<>();
        for (int k = 0; k < 1000; k++) bigMap.put("kw" + k, "T" + (k % 5));
        StringBuilder text = new StringBuilder(100_000);
        for (int i = 0; text.length() < 100_000; i++) {
            text.append("kw").append(i % 1000).append(' ');
        }
        long t0 = System.nanoTime();
        String out = new TagKeywordsInReview(bigMap).tagReview(text.toString());
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress contains a replacement",
                out.contains("[T0]{kw0}") && out.length() > text.length(), true);
        System.out.println("Stress |text|=" + text.length() + " keywords=1000 in " + ms + " ms");
    }

    private static void check(String label, Object got, Object expected) {
        boolean ok = (got == null ? expected == null : got.equals(expected));
        System.out.println((ok ? "OK   " : "FAIL ") + label);
        if (!ok) {
            System.out.println("  expected:\n" + expected);
            System.out.println("  got     :\n" + got);
        }
    }
    private static void check(String label, boolean got, boolean expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
