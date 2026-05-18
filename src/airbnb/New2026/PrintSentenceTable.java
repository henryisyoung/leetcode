package airbnb.New2026;
/*
Fixed-Width Text Formatting + Multi-Column Layout.

Given a string `text` (words separated by whitespace) and a positive
integer `width`, format the text into lines each of EXACTLY `width`
characters, left-aligned.

Rules
  - Keep original word order.
  - Greedy left-to-right packing: put as many words as fit, separating
    adjacent words by exactly one space.
  - After packing, right-pad with spaces to reach `width`.
  - Each word's length is guaranteed to be <= width.

Follow-up: k-column layout
  Generate all single-column lines first, then arrange them into k
  vertical columns:
    - Each column cell has width `width`.
    - Columns are separated by exactly one space.
    - If a column runs out of lines, pad with an all-space line.
  Default column-splitting strategy: rowsPerCol = ceil(total / k);
  column c receives lines [c*rowsPerCol .. (c+1)*rowsPerCol).

Examples (single-column)
  "this is a article", width=10
    "this is a "
    "article   "
  "a b c d e",         width=3
    "a b"
    "c d"
    "e  "
  "hello",             width=5  -> "hello"
  "aa bb ccc",         width=4  -> "aa  " / "bb  " / "ccc "
  "one two three four",width=8  -> "one two " / "three   " / "four    "

Constraints
  1 <= n      <= 1e5    (words)
  1 <= width  <= 100
  1 <= k      <= 5      (follow-up)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Implementation notes

  * Tokenization uses split("\\s+") and filters leading-empty tokens
    that show up when the input starts with whitespace.  This keeps
    the contract "words separated by spaces" tolerant of irregular
    spacing in the raw input.

  * Greedy packing is one pass:
        lineLen = 0
        for each word w:
            need = (lineLen == 0 ? 0 : 1) + w.length()
            if lineLen + need > width: flush line; lineLen = 0; need = w.length()
            append word (with separating space if not first)
            lineLen += need
        flush
    The "need" trick costs O(|word|) per word; total O(total chars).

  * Padding uses a precomputed `spaces` String (width characters) and
    substring(0, missing) — avoids per-line StringBuilder churn for
    long inputs.

  * Multi-column layout splits the line list vertically (not
    row-interleaved).  ceil(total / k) makes the first columns
    "full" and the last possibly short; the short columns are
    padded with blank lines so every final row has the same width.

Complexity
  Let S = total input chars (text), L = number of formatted lines.
  Time:   O(S + L * width)
  Memory: O(L * width)
*/
public class PrintSentenceTable {

    /** Single-column fixed-width formatting. Returns one string per line, each of length `width`. */
    public List<String> format(String text, int width) {
        if (width <= 0) throw new IllegalArgumentException("width must be positive: " + width);
        if (text == null) return new ArrayList<>();

        String[] words = text.trim().split("\\s+");
        // After trim() an empty input becomes [""], split returns [""].
        if (words.length == 1 && words[0].isEmpty()) return new ArrayList<>();

        String pad = padString(width);

        List<String> out = new ArrayList<>();
        StringBuilder line = new StringBuilder(width);
        int lineLen = 0;

        for (String w : words) {
            if (w.length() > width) {
                throw new IllegalArgumentException("word longer than width: \"" + w + "\"");
            }
            int sep = lineLen == 0 ? 0 : 1;
            if (lineLen + sep + w.length() > width) {
                out.add(flush(line, lineLen, width, pad));
                line.setLength(0);
                lineLen = 0;
                sep = 0;
            }
            if (sep == 1) line.append(' ');
            line.append(w);
            lineLen += sep + w.length();
        }
        if (lineLen > 0) out.add(flush(line, lineLen, width, pad));

        return out;
    }

    /** k-column layout over the single-column lines. */
    public List<String> formatMultiColumn(String text, int width, int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be positive: " + k);
        List<String> lines = format(text, width);
        if (lines.isEmpty()) return new ArrayList<>();
        if (k == 1) return lines;

        int total = lines.size();
        int rowsPerCol = (total + k - 1) / k;          // ceil(total / k)
        String blank = padString(width);

        List<String> out = new ArrayList<>(rowsPerCol);
        StringBuilder row = new StringBuilder(k * width + (k - 1));
        for (int r = 0; r < rowsPerCol; r++) {
            row.setLength(0);
            for (int c = 0; c < k; c++) {
                int idx = c * rowsPerCol + r;
                if (c > 0) row.append(' ');
                row.append(idx < total ? lines.get(idx) : blank);
            }
            out.add(row.toString());
        }
        return out;
    }

    /* --------------------------- helpers --------------------------- */

    private static String padString(int width) {
        char[] buf = new char[width];
        Arrays.fill(buf, ' ');
        return new String(buf);
    }

    private static String flush(StringBuilder line, int lineLen, int width, String pad) {
        if (lineLen == width) return line.toString();
        line.append(pad, 0, width - lineLen);
        return line.toString();
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
     *   Line 1: text (the full sentence on one line)
     *   Line 2: width
     *   Line 3 (optional): k for multi-column
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String text = br.readLine();
        int width = Integer.parseInt(br.readLine().trim());
        String kLine = br.readLine();
        PrintSentenceTable solver = new PrintSentenceTable();
        List<String> lines;
        if (kLine == null || kLine.trim().isEmpty()) {
            lines = solver.format(text == null ? "" : text, width);
        } else {
            int k = Integer.parseInt(kLine.trim());
            lines = solver.formatMultiColumn(text == null ? "" : text, width, k);
        }
        for (String ln : lines) System.out.println(ln);
    }

    private static void runDemos() {
        PrintSentenceTable solver = new PrintSentenceTable();

        // ---- Spec single-column examples ----
        checkLines("ex1: this is a article / 10",
                solver.format("this is a article", 10),
                "this is a ",
                "article   ");
        checkLines("ex2: a b c d e / 3",
                solver.format("a b c d e", 3),
                "a b",
                "c d",
                "e  ");
        checkLines("ex3: hello / 5",
                solver.format("hello", 5),
                "hello");
        checkLines("ex4: aa bb ccc / 4",
                solver.format("aa bb ccc", 4),
                "aa  ",
                "bb  ",
                "ccc ");
        checkLines("ex5: one two three four / 8",
                solver.format("one two three four", 8),
                "one two ",
                "three   ",
                "four    ");

        // ---- Edge cases ----
        checkLines("empty text", solver.format("", 5));
        checkLines("blank text", solver.format("   ", 5));
        checkLines("single word == width",
                solver.format("abcde", 5),
                "abcde");
        checkLines("multiple spaces collapse",
                solver.format("a   b   c", 3),
                "a b",
                "c  ");

        // ---- Multi-column layout ----
        // 5 lines, k=2 -> rowsPerCol = 3. Col0: lines 0,1,2. Col1: lines 3,4 (+ 1 blank).
        // Use "a b c d e" / 3 to get 3 lines (NOT 5). Need a longer text.
        // Let's pick "one two three four five six seven eight nine ten" / 5 to get distinct lines.
        // one(3), two(3), three(5), four(4), five(4), six(3), seven(5), eight(5), nine(4), ten(3)
        // Greedy at width 5:
        //   "one"(3) + " two"(4)=7>5 flush "one  "
        //   "two"(3) + " three"(... way too long) flush "two  "
        //   "three"(5) flush "three"
        //   "four"(4) flush "four "
        //   "five"(4) flush "five "
        //   "six"(3) flush "six  "
        //   "seven"(5) flush "seven"
        //   "eight"(5) flush "eight"
        //   "nine"(4) flush "nine "
        //   "ten"(3) flush "ten  "
        // -> 10 lines.  k=2 -> rowsPerCol = 5.  Col0 lines 0..4, Col1 lines 5..9.
        List<String> mc = solver.formatMultiColumn(
                "one two three four five six seven eight nine ten", 5, 2);
        checkLines("multi-column k=2",
                mc,
                "one   six  ",
                "two   seven",
                "three eight",
                "four  nine ",
                "five  ten  ");

        // k=3 on 10 lines -> rowsPerCol = ceil(10/3) = 4.  Col0:0..3, Col1:4..7, Col2:8..9 (+2 blank).
        List<String> mc3 = solver.formatMultiColumn(
                "one two three four five six seven eight nine ten", 5, 3);
        checkLines("multi-column k=3",
                mc3,
                "one   five  nine ",
                "two   six   ten  ",
                "three seven      ",
                "four  eight      ");

        // k=1 degenerates to single-column.
        List<String> mc1 = solver.formatMultiColumn("a b c d e", 3, 1);
        checkLines("multi-column k=1 equals single-col",
                mc1,
                "a b",
                "c d",
                "e  ");

        // ---- Stress: n = 1e5 words ----
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 100_000; i++) {
            if (i > 0) big.append(' ');
            big.append("w").append(i);
        }
        long t0 = System.nanoTime();
        List<String> lines = solver.format(big.toString(), 100);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress n=100000 width=100: " + lines.size() + " lines in " + ms + " ms");
    }

    private static void checkLines(String label, List<String> got, String... expected) {
        boolean ok = got.size() == expected.length;
        if (ok) {
            for (int i = 0; i < expected.length; i++) {
                if (!got.get(i).equals(expected[i])) { ok = false; break; }
            }
        }
        System.out.println((ok ? "OK   " : "FAIL ") + label);
        if (!ok) {
            System.out.println("  expected (" + expected.length + " lines):");
            for (String s : expected) System.out.println("    \"" + s + "\"");
            System.out.println("  got      (" + got.size() + " lines):");
            for (String s : got)      System.out.println("    \"" + s + "\"");
        }
    }
}
