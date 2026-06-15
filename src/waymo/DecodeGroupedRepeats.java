package waymo;
/*
Decode Grouped Repeats  (LC 394 variant: repeat count is a {k} SUFFIX).

Input
  A string of literal characters, parenthesized groups, and {k} suffixes that
  repeat the immediately preceding group k times. Nesting allowed.

Output
  The fully expanded string.

Examples
  "abs(cs){3}g"        -> "abscscscsg"
  "a(b(c){2}){2}"      -> "abccbcc"
  "(a){2}b(c){3}"      -> "aabccc"
  "x"                  -> "x"
  "(ab){0}c"           -> "c"            (k = 0 drops the group)
  "(a)b"               -> "ab"           (missing {k} after ) => multiplier 1)

Approach -- the LC 394 two-stack idiom, adapted
  LC 394 ("3[cs]") puts the count BEFORE the bracket, so it reads digits and
  pushes them on a `count` stack, then pops at ']'. Here the count comes AFTER
  the group as "(cs){3}", so we don't need a count stack at all: when we hit
  ')' we look right past it for an optional "{k}" and read it inline.

  Stack of partial strings, seeded with "" so the top always exists:
    '('     -> push a new "" level for the group we're entering
    literal -> append the char to the current (top) level
    ')'     -> pop the finished group; read the optional {k} (default 1);
               append the group k times onto the parent level.
  The final answer is the single remaining string on the stack.

Edge cases
  k = 0 drops the group; a ')' with no following {k} means multiplier 1;
  multi-digit k; literals between/around groups.

Complexity note
  This faithful LC-394 style uses string concatenation (`stack.push(stack.pop()
  + ...)`), which is O(M^2) worst case (each concat copies the whole partial
  string), exactly like the canonical LC 394 snippet. To make it O(N + M),
  swap `Stack<String>` for a stack of StringBuilder and append in place instead
  of concatenating -- same control flow.
*/

import java.util.Stack;

public class DecodeGroupedRepeats {

    public static String decode(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        Stack<String> stack = new Stack<>();
        stack.push("");
        int i = 0, n = s.length();

        while (i < n) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push("");
                i++;
            } else if (c == ')') {
                String group = stack.pop();
                i++;                              // move past ')'

                int val = 1;                      // multiplier defaults to 1 if no {k}
                if (i < n && s.charAt(i) == '{') {
                    i++;                          // past '{'
                    val = 0;
                    while (i < n && s.charAt(i) != '}') {
                        val = val * 10 + (s.charAt(i) - '0');
                        i++;
                    }
                    i++;                          // past '}'
                }

                StringBuilder sb = new StringBuilder();
                while (val > 0) {
                    sb.append(group);
                    val--;
                }
                stack.push(stack.pop() + sb.toString());
            } else {
                stack.push(stack.pop() + c);
                i++;
            }
        }
        return stack.pop();
    }

    /* --------------- recursive-descent reference (oracle) --------------- */
    /*
     * Independent implementation used only to cross-check decode() in tests.
     * Returns the decoded suffix at each ')' boundary; mutates a shared cursor.
     */
    private static int rdPos;

    static String decodeRecursive(String s) {
        rdPos = 0;
        return rdParse(s, false);
    }

    private static String rdParse(String s, boolean insideGroup) {
        StringBuilder sb = new StringBuilder();
        while (rdPos < s.length()) {
            char c = s.charAt(rdPos);
            if (c == '(') {
                rdPos++;                         // consume '('
                String inner = rdParse(s, true); // parses up to and consumes ')'
                int k = 1;
                if (rdPos < s.length() && s.charAt(rdPos) == '{') {
                    rdPos++;                     // consume '{'
                    int v = 0;
                    while (s.charAt(rdPos) != '}') { v = v * 10 + (s.charAt(rdPos) - '0'); rdPos++; }
                    rdPos++;                     // consume '}'
                    k = v;
                }
                for (int r = 0; r < k; r++) sb.append(inner);
            } else if (c == ')') {
                rdPos++;                         // consume ')'
                if (!insideGroup) throw new IllegalArgumentException("unmatched )");
                return sb.toString();
            } else {
                sb.append(c);
                rdPos++;
            }
        }
        if (insideGroup) throw new IllegalArgumentException("unmatched (");
        return sb.toString();
    }

    /* ----------------------------- tests ----------------------------- */

    public static void main(String[] args) {
        check("prompt example",   "abs(cs){3}g",   "abscscscsg");
        check("nested",           "a(b(c){2}){2}", "abccbcc");
        check("chars between",    "(a){2}b(c){3}", "aabccc");
        check("single char",      "x",             "x");
        check("k=0 drops",        "(ab){0}c",      "c");
        check("missing suffix=1", "(a)b",          "ab");
        check("multi-digit k",    "(a){12}",       "aaaaaaaaaaaa");
        check("empty string",     "",              "");
        check("no groups",        "hello",         "hello");
        check("adjacent groups",  "(x){2}(y){2}",  "xxyy");
        check("deep nest",        "((a){2}){3}",   "aaaaaa");
        check("group then nest",  "z(a(bc){2}d){2}e", "zabcbcdabcbcde");

        // Cross-check decode() vs the recursive reference on random valid inputs.
        java.util.Random rng = new java.util.Random(99);
        int trials = 5000, fails = 0;
        for (int t = 0; t < trials; t++) {
            String input = randomPattern(rng, 0);
            String a, b;
            try { a = decode(input); b = decodeRecursive(input); }
            catch (Exception ex) {
                System.out.println("EXCEPTION on " + input + ": " + ex.getMessage());
                fails++;
                continue;
            }
            if (!a.equals(b)) {
                fails++;
                if (fails <= 3) System.out.println("MISMATCH in=" + input + " stack=" + a + " rec=" + b);
            }
        }
        System.out.println((fails == 0 ? "OK    " : "FAIL  ")
                + "random cross-check (" + trials + " patterns, fails=" + fails + ")");
    }

    /** Generate a random valid pattern; depth-bounded to keep output small. */
    private static String randomPattern(java.util.Random rng, int depth) {
        StringBuilder sb = new StringBuilder();
        int parts = rng.nextInt(3);                  // 0..2 elements at this level
        for (int p = 0; p <= parts; p++) {
            if (depth < 3 && rng.nextInt(100) < 40) { // a group
                sb.append('(');
                sb.append(randomPattern(rng, depth + 1));
                sb.append(')');
                if (rng.nextInt(100) < 80) {          // usually attach {k}
                    sb.append('{').append(rng.nextInt(4)).append('}');  // k in 0..3
                }
            } else {                                  // some literal chars
                int len = 1 + rng.nextInt(2);
                for (int c = 0; c < len; c++) sb.append((char) ('a' + rng.nextInt(3)));
            }
        }
        return sb.toString();
    }

    private static void check(String label, String input, String expected) {
        String got = decode(input);
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK    " : "FAIL  ") + label
                + "  in=\"" + input + "\" got=\"" + got + "\""
                + (ok ? "" : " expected=\"" + expected + "\""));
    }
}
