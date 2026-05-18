package airbnb.New2026;
/*
Multi-column variant of PrintSentenceTable.

Spec
  Input
    rows    : List<List<String>>  -- each row is a list of cell strings.
    widths  : int[]               -- content width of each column.
    mode    : TRUNCATE | WRAP     -- how to handle a cell longer than its column width.
  Output
    A bordered ASCII table whose row heights vary with the longest
    wrapped cell when mode = WRAP, or always 1 when mode = TRUNCATE.

Layout
  Border row : '+' + (for each col: '-' * (width + 2) + '+')
  Cell slot  : ' ' + content padded to width + ' '
  Content row: '|' + col0 + '|' + col1 + '|' + ... + '|'

Example  (widths = [10, 16], mode = WRAP)
  +------------+------------------+
  | Hello      | world            |
  +------------+------------------+
  | A long     | This is also     |
  | cell that  | a long cell      |
  | wraps      | spanning lines   |
  +------------+------------------+

Ragged rows
  If a row has fewer cells than widths.length we pad with "".
  If a row has more cells than widths.length we ignore the extras.
  These choices keep the renderer total-on-malformed-input; flip to a
  hard-error in two lines if your usage prefers strictness.

Mode semantics
  TRUNCATE
    Each cell's content is shortened to its column width via substring.
    Every row is exactly one visual line tall.
  WRAP
    Each cell is split into chunks of size width (greedy word-wrap if
    that fits, hard-wrap as fallback).  Row height = max chunk count
    across cells; shorter cells emit blank padding on their extra
    visual lines so the column borders stay aligned.

Constraints
  widths.length >= 1, every widths[j] >= 1
  cells contain printable ASCII; row count and column count are bounded
  by upstream usage.

Complexity
  Let R = number of rows, C = widths.length, S = total content chars.
  Time:   O(S + R * C * maxRowHeight)
  Memory: O(S + width of one rendered row)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Implementation notes

  * The horizontal border is built ONCE per call and reused.  If you
    render many tables with the same column widths repeatedly, hoist
    `buildBorder` out and cache its result.

  * Word-wrap rule (WRAP mode):
        - Try to break at the last space at-or-before column j+width.
        - If no space exists in the slice (single huge token), hard-wrap
          at exactly width.
        - Trim leading spaces on continuation lines so wrapped text
          doesn't drift right.

  * For TRUNCATE mode every "row height" is 1 so the inner loop simply
    builds a single line per row.

  * Stdin format (loose, easy to type):
        rows X cols Y mode TRUNCATE|WRAP
        widths
        w0 w1 ... wY-1
        cells
        cell0_0 | cell0_1 | ... | cell0_(Y-1)
        cell1_0 | ...
        ...
*/
public class MultiColumnTable {

    public enum Mode { TRUNCATE, WRAP }

    public String render(List<List<String>> rows, int[] widths, Mode mode) {
        if (widths == null || widths.length == 0) {
            throw new IllegalArgumentException("widths must be non-empty");
        }
        for (int w : widths) {
            if (w < 1) throw new IllegalArgumentException("each width must be >= 1");
        }
        if (mode == null) mode = Mode.TRUNCATE;
        if (rows == null || rows.isEmpty()) return "";

        String border = buildBorder(widths);
        StringBuilder out = new StringBuilder();

        for (List<String> row : rows) {
            String[] cells = normalizeRow(row, widths.length);
            out.append(border).append('\n');
            if (mode == Mode.TRUNCATE) {
                out.append(renderTruncatedRow(cells, widths)).append('\n');
            } else {
                List<String[]> wrapped = wrapAll(cells, widths);
                int height = wrapped.get(0).length;       // all cells share the same height (padded)
                for (int line = 0; line < height; line++) {
                    out.append(renderRowLine(wrapped, widths, line)).append('\n');
                }
            }
        }
        out.append(border).append('\n');
        return out.toString();
    }

    /* --------------------------- helpers --------------------------- */

    private static String buildBorder(int[] widths) {
        int total = 1;                                    // leading '+'
        for (int w : widths) total += (w + 2) + 1;        // segment of '-' + trailing '+'
        char[] buf = new char[total];
        int p = 0;
        buf[p++] = '+';
        for (int w : widths) {
            for (int i = 0; i < w + 2; i++) buf[p++] = '-';
            buf[p++] = '+';
        }
        return new String(buf);
    }

    private static String[] normalizeRow(List<String> row, int cols) {
        String[] cells = new String[cols];
        for (int j = 0; j < cols; j++) {
            String v = (row != null && j < row.size()) ? row.get(j) : "";
            cells[j] = v == null ? "" : v;
        }
        return cells;
    }

    private static String renderTruncatedRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int j = 0; j < widths.length; j++) {
            String c = cells[j];
            if (c.length() > widths[j]) c = c.substring(0, widths[j]);
            appendCell(sb, c, widths[j]);
        }
        return sb.toString();
    }

    /** Wrap every cell to widths[j]; pad to a uniform height across the row. */
    private static List<String[]> wrapAll(String[] cells, int[] widths) {
        String[][] wrapped = new String[widths.length][];
        int maxHeight = 1;
        for (int j = 0; j < widths.length; j++) {
            wrapped[j] = wrap(cells[j], widths[j]);
            if (wrapped[j].length > maxHeight) maxHeight = wrapped[j].length;
        }
        for (int j = 0; j < widths.length; j++) {
            if (wrapped[j].length < maxHeight) {
                String[] padded = new String[maxHeight];
                System.arraycopy(wrapped[j], 0, padded, 0, wrapped[j].length);
                Arrays.fill(padded, wrapped[j].length, maxHeight, "");
                wrapped[j] = padded;
            }
        }
        return Arrays.asList(wrapped);
    }

    /** Render visual line `line` of a wrapped row. */
    private static String renderRowLine(List<String[]> wrapped, int[] widths, int line) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int j = 0; j < widths.length; j++) {
            appendCell(sb, wrapped.get(j)[line], widths[j]);
        }
        return sb.toString();
    }

    /** Append " content" right-padded to width + " |". */
    private static void appendCell(StringBuilder sb, String content, int width) {
        sb.append(' ').append(content);
        for (int i = content.length(); i < width; i++) sb.append(' ');
        sb.append(' ').append('|');
    }

    /**
     * Word-wrap `s` into chunks of size <= width.  Greedy: at each step
     * take the longest prefix that fits and ends at a space (so words
     * stay intact); if no space fits inside the slice, hard-wrap at
     * exactly `width` so a single huge token still fits the column.
     * Continuation lines have their leading spaces trimmed to avoid drift.
     */
    static String[] wrap(String s, int width) {
        if (s == null) s = "";
        if (s.isEmpty()) return new String[]{""};

        List<String> out = new ArrayList<>();
        int i = 0, n = s.length();
        while (i < n) {
            // Skip leading spaces on continuation lines (but not the very first line).
            if (!out.isEmpty()) {
                while (i < n && s.charAt(i) == ' ') i++;
                if (i == n) break;
            }
            int end = Math.min(i + width, n);
            if (end < n) {
                // Largest space index <= end (inclusive) — that fits "word boundary at the column edge".
                int br = s.lastIndexOf(' ', end);
                if (br > i) end = br;       // exclusive end at the space; loop skips it next iter
            }
            // If no space found, end stays at i + width (hard-wrap).
            out.add(s.substring(i, end));
            i = end;
        }
        return out.toArray(new String[0]);
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

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // First line: "R C MODE"
        String[] hdr = br.readLine().trim().split("\\s+");
        int R = Integer.parseInt(hdr[0]);
        int C = Integer.parseInt(hdr[1]);
        Mode mode = hdr.length >= 3 ? Mode.valueOf(hdr[2].toUpperCase()) : Mode.TRUNCATE;

        // Next line: widths.
        String[] wTok = br.readLine().trim().split("\\s+");
        int[] widths = new int[C];
        for (int j = 0; j < C; j++) widths[j] = Integer.parseInt(wTok[j]);

        List<List<String>> rows = new ArrayList<>(R);
        for (int i = 0; i < R; i++) {
            String line = br.readLine();
            if (line == null) line = "";
            String[] cells = line.split("\\s*\\|\\s*", -1);
            List<String> row = new ArrayList<>(C);
            for (int j = 0; j < C; j++) row.add(j < cells.length ? cells[j] : "");
            rows.add(row);
        }
        System.out.print(new MultiColumnTable().render(rows, widths, mode));
    }

    private static void runDemos() {
        MultiColumnTable t = new MultiColumnTable();

        // ---- TRUNCATE demo ----
        System.out.println("--- TRUNCATE, widths=[10,16] ---");
        String tr = t.render(Arrays.asList(
                Arrays.asList("Hello", "world"),
                Arrays.asList("How are you", "doing today?"),
                Arrays.asList("A very long cell here", "Short")
        ), new int[]{10, 16}, Mode.TRUNCATE);
        System.out.print(tr);

        // ---- WRAP demo ----
        System.out.println("--- WRAP, widths=[10,16] ---");
        String wr = t.render(Arrays.asList(
                Arrays.asList("Hello", "world"),
                Arrays.asList("A long cell that wraps", "This is also a long cell spanning lines"),
                Arrays.asList("Bye", "")
        ), new int[]{10, 16}, Mode.WRAP);
        System.out.print(wr);

        // ---- Sanity checks ----
        // Border length matches the formula: 1 + sum(w + 2) + cols (for trailing '+').
        // widths=[10,16] -> 1 + 12 + 1 + 18 + 1 = 33.
        check("truncate border length 33", tr.split("\n")[0].length() == 33);
        check("truncate has 4 borders + 3 rows = 7 lines",
                tr.split("\n").length == 7);

        // Wrap mode: row 2 should be 4 lines tall (the longer cell wraps to 4 chunks at width 16).
        // Visualize: "This is also a long cell spanning lines" wraps at width 16:
        //   "This is also a"   "long cell"   "spanning lines"  -> 3 lines
        // First column: "A long cell that wraps" wraps at width 10:
        //   "A long"   "cell that"   "wraps"                   -> 3 lines
        // So row 2 visual height = 3.  Check the table line count: 3 rows of heights 1, 3, 1
        // plus 4 borders = 1 + 3 + 1 + 4 = 9.
        check("wrap line count == 9", wr.split("\n").length == 9);

        // Each content line has the same length as the border.
        String[] wlines = wr.split("\n");
        int bw = wlines[0].length();
        boolean uniform = true;
        for (String ln : wlines) if (ln.length() != bw) { uniform = false; break; }
        check("wrap rows align (all lines same length)", uniform);

        // ---- wrap() unit checks ----
        check("wrap empty -> [\"\"]", Arrays.equals(wrap("", 5), new String[]{""}));
        check("wrap fits in one chunk",
                Arrays.equals(wrap("hi", 5), new String[]{"hi"}));
        check("wrap word boundary",
                Arrays.equals(wrap("a long cell", 6), new String[]{"a long", "cell"}));
        check("wrap hard-wrap on huge token",
                Arrays.equals(wrap("abcdefghij", 4), new String[]{"abcd", "efgh", "ij"}));
        check("wrap trims continuation leading space",
                Arrays.equals(wrap("hello world", 5), new String[]{"hello", "world"}));

        // ---- Ragged rows ----
        // Missing cells are padded with "".
        String rag = t.render(Arrays.asList(
                Arrays.asList("a"),                     // 1 cell, table has 3 cols
                Arrays.asList("x", "y", "z", "extra")   // 4 cells, last is dropped
        ), new int[]{3, 3, 3}, Mode.TRUNCATE);
        check("ragged rows produce 5 lines", rag.split("\n").length == 5);
        check("ragged row 1 has empty cols 2,3",
                rag.split("\n")[1].equals("| a   |     |     |"));
        check("ragged row 2 truncates to 3 cells",
                rag.split("\n")[3].equals("| x   | y   | z   |"));

        // ---- Empty input ----
        check("empty rows -> empty output", t.render(new ArrayList<>(), new int[]{3}, Mode.WRAP).isEmpty());

        // ---- Width 1 still works ----
        String w1 = t.render(Arrays.asList(Arrays.asList("a", "b", "c")),
                new int[]{1, 1, 1}, Mode.TRUNCATE);
        check("width=1 row", w1.split("\n")[1].equals("| a | b | c |"));
    }

    private static void check(String label, boolean cond) {
        System.out.println((cond ? "OK   " : "FAIL ") + label);
    }
}
