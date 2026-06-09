package airbnb.New2026;

import java.util.*;

/*
================================================================================
  Text Justification  (LC 68 — Airbnb / classic onsite)
================================================================================

  Given an array of words and a width `maxWidth`, format the text so that
  each line is exactly `maxWidth` characters and is FULLY justified.

  Rules
    1. Pack as many words as possible into each line (greedy). Adjacent
       words on the same line have AT LEAST one space between them.
    2. Extra space (maxWidth - sum_of_word_chars) is distributed evenly
       between the gaps. If it doesn't divide evenly, the LEFTMOST gaps
       get one extra space than the rightmost.
    3. The LAST line is LEFT-justified: a single space between words and
       the remaining padding on the right.
    4. A line that contains a SINGLE word (no gaps) is also left-justified
       with padding on the right — division-by-zero would otherwise happen.

  Example
      words    = ["This","is","an","example","of","text","justification."]
      maxWidth = 16
      output   = [
          "This    is    an",      // 4+4+2+4+2 = 16
          "example  of text",      // 7+2+2+1+4 = 16  (extra=1 → leftmost gap +1)
          "justification.  "       // last line, left-justified + pad
      ]

  Algorithm — greedy line packing + per-line spacing

    Outer loop: for each i, find the largest j s.t.
        sum_chars(i..j-1) + (j-i-1) <= maxWidth
    where (j-i-1) is the minimum required spaces (1 between each pair).
    Equivalently, walk j forward while
        sumChars + words[j].length() + (j - i) <= maxWidth
    (the `(j - i)` term counts the gap that would be added BY taking words[j]).

    For the chosen window words[i..j-1] (k = j-i words):
      * If k == 1 OR j == n  (single word OR last line):
            left-justify: one space between words, pad right to maxWidth.
      * Else:
            gaps        = k - 1
            totalSpaces = maxWidth - sumChars
            base        = totalSpaces / gaps
            extra       = totalSpaces % gaps         (first `extra` gaps get +1)

  Complexity
    Time:   O(total chars in output)  — every output char is emitted once.
    Memory: O(maxWidth) per line for the StringBuilder.

  Why the "leftmost gaps get the extra space" rule matters
    It's the spec. Naive implementations that put the extras on the RIGHT
    will fail the LeetCode judge on inputs like
        words=["What","must","be","acknowledgment","shall","be"], maxWidth=16
    where line 1 = "What   must   be" needs the extras on the left.

  Follow-ups worth mentioning in an interview
    F1. Streaming input — you can't randomly access future words. Solve with
        a rolling buffer: keep accumulating words until the next would
        overflow, then flush. Same algorithm, single forward pass.
    F2. Variable-width fonts (newspaper layout) — replace `.length()` with
        a width function and the rest is unchanged.
    F3. Hyphenation / soft-break — when a single word is wider than the
        line, split it (or hyphenate). Affects line-packing only.
    F4. Minimum-raggedness justification (Knuth-Plass) — greedy is locally
        good but globally suboptimal. Knuth-Plass uses DP minimizing the
        sum of squared slack per line, producing the LaTeX/InDesign-style
        output. O(n²) DP, well-studied.
    F5. Right-to-left scripts (Arabic, Hebrew) — flip the "leftmost gap"
        rule to "rightmost gap". Otherwise identical.
================================================================================
*/
public class TextJustification {

    public static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        if (words == null || words.length == 0) return result;

        int n = words.length;
        int i = 0;
        while (i < n) {
            int j = i;
            int sumChars = 0;
            while (j < n && sumChars + words[j].length() + (j - i) <= maxWidth) {
                sumChars += words[j].length();
                j++;
            }

            int wordCount = j - i;
            boolean isLastLine = (j == n);
            StringBuilder sb = new StringBuilder(maxWidth);

            if (wordCount == 1 || isLastLine) {
                for (int k = i; k < j; k++) {
                    if (k > i) sb.append(' ');
                    sb.append(words[k]);
                }
                while (sb.length() < maxWidth) sb.append(' ');
            } else {
                int gaps        = wordCount - 1;
                int totalSpaces = maxWidth - sumChars;
                int base        = totalSpaces / gaps;
                int extra       = totalSpaces % gaps;
                for (int k = i; k < j; k++) {
                    sb.append(words[k]);
                    if (k < j - 1) {
                        int spaces = base + ((k - i) < extra ? 1 : 0);
                        for (int s = 0; s < spaces; s++) sb.append(' ');
                    }
                }
            }
            result.add(sb.toString());
            i = j;
        }
        return result;
    }

    /* --------------------------- Demos / tests --------------------------- */

    public static void main(String[] args) {
        check("LC example 1",
                new String[]{"This","is","an","example","of","text","justification."}, 16,
                "This    is    an",
                "example  of text",
                "justification.  ");

        check("LC example 2 (last line + single-word lines)",
                new String[]{"What","must","be","acknowledgment","shall","be"}, 16,
                "What   must   be",
                "acknowledgment  ",
                "shall be        ");

        check("LC example 3 (long word forces single-word line + extras-on-left)",
                new String[]{"Science","is","what","we","understand","well","enough","to",
                             "explain","to","a","computer.","Art","is","everything","else","we","do"}, 20,
                "Science  is  what we",
                "understand      well",
                "enough to explain to",
                "a  computer.  Art is",
                "everything  else  we",
                "do                  ");

        check("single word exactly maxWidth",
                new String[]{"hello"}, 5,
                "hello");

        check("single word shorter than maxWidth",
                new String[]{"hi"}, 5,
                "hi   ");

        check("two words on last line",
                new String[]{"a","b"}, 5,
                "a b  ");

        check("perfectly packed (no slack)",
                new String[]{"ab","cd","ef"}, 8,
                "ab cd ef");

        check("one word per line (each maxes width)",
                new String[]{"aaaa","bbbb","cccc"}, 4,
                "aaaa",
                "bbbb",
                "cccc");

        check("extras land on leftmost gap (extra=1, multi-line)",
                new String[]{"ab","cd","ef","gh"}, 9,
                "ab  cd ef",
                "gh       ");

        check("extras spread across leftmost two gaps (extra=2)",
                new String[]{"a","b","c","def","gh"}, 11,
                "a  b  c def",
                "gh         ");

        check("empty input",
                new String[]{}, 10);
    }

    /* --------------------------- helpers --------------------------- */

    private static void check(String label, String[] words, int maxWidth, String... expectedLines) {
        List<String> got = fullJustify(words, maxWidth);
        List<String> expected = Arrays.asList(expectedLines);

        boolean ok = got.size() == expected.size();
        for (int i = 0; ok && i < got.size(); i++) {
            ok = got.get(i).equals(expected.get(i)) && got.get(i).length() == maxWidth;
        }
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  (maxWidth=" + maxWidth + ")");
        if (!ok) {
            System.out.println("  expected:");
            for (String s : expected) System.out.println("    |" + s + "|  (len=" + s.length() + ")");
            System.out.println("  got:");
            for (String s : got)      System.out.println("    |" + s + "|  (len=" + s.length() + ")");
        }
    }
}
