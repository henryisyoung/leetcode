package waymo;
/*
Problem: Parse a CSV String into a Usable Data Structure (with Corruption Handling)

Given a CSV-formatted string s that may be clean or corrupted, parse it into a
data structure convenient for downstream use.

Input
  - The first line is the header (column names), guaranteed correct.
  - Lines are separated by '\n'.
  - Fields in a line are separated by ','.

Output (row-oriented map):
  rows: Map<rowIndex, Map<String, Object>>
    rowIndex starts at 0 for the first DATA row (header excluded).
    Inner map: column-name → field value, plus a boolean "is_valid".

Validity rules
  - Field count == header column count → is_valid = true.
  - Less or more fields                 → is_valid = false.
    - Less: fill missing columns with "".
    - More: place the extras under "extra_fields".

Constraints
  - All values are treated as strings (no type inference).
  - Linear-time processing: O(L) where L is the input length.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
Algorithm:
  1. Split input on '\n'  (keep empty trailing fields so we don't lose a final
     empty data row that was actually "all blank").
  2. The first non-null line is the header; comma-split it once and remember
     both the column names and the count m.
  3. For each subsequent line:
        a. Comma-split with -1 limit to preserve empty fields ("a,,b" → 3 fields).
        b. Let k be the field count.
        c. Build the row map:
              - For c in [0, min(k, m)):  put header[c] → fields[c]
              - For c in [min(k, m), m):  put header[c] → ""        (missing → empty)
              - If k > m:                 store fields[m..k-1] under "extra_fields"
        d. is_valid = (k == m).
  4. Skip exactly one trailing empty line if the input ended in '\n' (so a
     normal "...row\n" doesn't produce a phantom invalid empty row).

Complexity:
  Each character is touched O(1) times during the splits, and each field is
  written into the output exactly once.  Total work is O(L) where L = |input|.
  Memory is O(total bytes in fields) for the output, plus O(rows * cols) map
  entries.

Note:
  This parser does NOT handle quoted fields, embedded commas/newlines, or
  escaped characters — the spec keeps the format minimal.  See
  airbnb.pre2026.CSVparser for a quoted-field variant if needed.
*/
public class ParseCsvWithCorruption {

    /** A single parsed data row.  Strongly-typed companion to the spec map shape. */
    public static final class Row {
        public final boolean isValid;
        /** Column name → field value.  Has exactly the header's columns, in order. */
        public final Map<String, String> fields;
        /** Trailing fields beyond the header width (empty list when k ≤ m). */
        public final List<String> extraFields;

        public Row(boolean isValid, Map<String, String> fields, List<String> extraFields) {
            this.isValid = isValid;
            this.fields = fields;
            this.extraFields = extraFields;
        }

        @Override
        public String toString() {
            return "Row{is_valid=" + isValid + ", fields=" + fields
                    + (extraFields.isEmpty() ? "" : ", extra_fields=" + extraFields) + "}";
        }
    }

    /** Parse and return the row-oriented map as specified.  Inner values are String / Boolean / List<String>. */
    public Map<Integer, Map<String, Object>> parse(String csv) {
        Map<Integer, Row> typed = parseTyped(csv);
        Map<Integer, Map<String, Object>> out = new LinkedHashMap<>();
        for (Map.Entry<Integer, Row> e : typed.entrySet()) {
            Row row = e.getValue();
            Map<String, Object> m = new LinkedHashMap<>(row.fields);
            m.put("is_valid", row.isValid);
            if (!row.extraFields.isEmpty()) m.put("extra_fields", row.extraFields);
            out.put(e.getKey(), m);
        }
        return out;
    }

    /** Strongly-typed variant.  Same parsing, returns {@link Row} objects instead of nested maps. */
    public Map<Integer, Row> parseTyped(String csv) {
        Map<Integer, Row> rows = new LinkedHashMap<>();
        if (csv == null || csv.isEmpty()) return rows;

        String[] lines = csv.split("\n", -1);
        if (lines.length == 0) return rows;

        String[] header = lines[0].split(",", -1);
        int m = header.length;

        int lastIdx = lines.length - 1;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            // Allow exactly one trailing newline: an empty final line is the EOF marker,
            // not a (single-blank-field) data row.
            if (line.isEmpty() && i == lastIdx) continue;

            String[] fields = line.split(",", -1);
            int k = fields.length;

            Map<String, String> map = new LinkedHashMap<>();
            int common = Math.min(k, m);
            for (int c = 0; c < common; c++) {
                map.put(header[c], fields[c]);
            }
            for (int c = common; c < m; c++) {
                map.put(header[c], "");
            }

            List<String> extras;
            if (k > m) {
                extras = new ArrayList<>(k - m);
                for (int c = m; c < k; c++) extras.add(fields[c]);
            } else {
                extras = Collections.emptyList();
            }

            rows.put(i - 1, new Row(k == m, map, extras));
        }
        return rows;
    }

    /* --------------------------- Demo + tests --------------------------- */

    public static void main(String[] args) {
        ParseCsvWithCorruption parser = new ParseCsvWithCorruption();

        // Test 1: Clean data.
        runTest(parser, "Test 1: clean",
                "col1,col2,col3\nv1,v2,v3",
                expected -> {
                    expected.put(0, row(true, kv("col1", "v1", "col2", "v2", "col3", "v3")));
                });

        // Test 2: Missing fields.
        runTest(parser, "Test 2: missing fields",
                "c1,c2,c3\nx,y",
                expected -> {
                    expected.put(0, row(false, kv("c1", "x", "c2", "y", "c3", "")));
                });

        // Test 3: Extra fields.
        runTest(parser, "Test 3: extra fields",
                "c1,c2\nx,y,z",
                expected -> {
                    Map<String, Object> r = row(false, kv("c1", "x", "c2", "y"));
                    r.put("extra_fields", Arrays.asList("z"));
                    expected.put(0, r);
                });

        // Test 4: Mixed rows.
        runTest(parser, "Test 4: mixed",
                "a,b,c\n1,2,3\n4,5\n6,7,8,9",
                expected -> {
                    expected.put(0, row(true, kv("a", "1", "b", "2", "c", "3")));
                    expected.put(1, row(false, kv("a", "4", "b", "5", "c", "")));
                    Map<String, Object> r2 = row(false, kv("a", "6", "b", "7", "c", "8"));
                    r2.put("extra_fields", Arrays.asList("9"));
                    expected.put(2, r2);
                });

        // Test 5: Header only.
        runTest(parser, "Test 5: header only",
                "c1,c2,c3",
                expected -> { /* empty */ });

        // Test 6: Trailing newline after a row should NOT create a phantom row.
        runTest(parser, "Test 6: trailing newline",
                "c1,c2\nv1,v2\n",
                expected -> {
                    expected.put(0, row(true, kv("c1", "v1", "c2", "v2")));
                });

        // Test 7: Blank line in the middle is treated as a 1-field row (invalid for header width > 1).
        runTest(parser, "Test 7: blank line mid-stream",
                "c1,c2\nv1,v2\n\nv3,v4",
                expected -> {
                    expected.put(0, row(true, kv("c1", "v1", "c2", "v2")));
                    expected.put(1, row(false, kv("c1", "", "c2", "")));
                    expected.put(2, row(true, kv("c1", "v3", "c2", "v4")));
                });

        // Test 8: All-empty fields row is a VALID row of empty strings.
        runTest(parser, "Test 8: all-empty fields are valid",
                "c1,c2,c3\n,,",
                expected -> {
                    expected.put(0, row(true, kv("c1", "", "c2", "", "c3", "")));
                });

        // Test 9: Empty input.
        runTest(parser, "Test 9: empty input",
                "",
                expected -> { /* empty */ });

        // Test 10: Single-column header with extras.
        runTest(parser, "Test 10: 1-col header, row has 3 fields",
                "name\nalice,bob,carol",
                expected -> {
                    Map<String, Object> r = row(false, kv("name", "alice"));
                    r.put("extra_fields", Arrays.asList("bob", "carol"));
                    expected.put(0, r);
                });
    }

    /* --------------------------- Test plumbing --------------------------- */

    @FunctionalInterface
    private interface ExpectedBuilder {
        void build(Map<Integer, Map<String, Object>> expected);
    }

    private static void runTest(ParseCsvWithCorruption parser, String name,
                                String input, ExpectedBuilder builder) {
        Map<Integer, Map<String, Object>> expected = new LinkedHashMap<>();
        builder.build(expected);
        Map<Integer, Map<String, Object>> actual = parser.parse(input);

        boolean ok = expected.equals(actual);
        System.out.println((ok ? "OK   " : "FAIL ") + name);
        if (!ok) {
            System.out.println("  input    = " + escape(input));
            System.out.println("  expected = " + expected);
            System.out.println("  actual   = " + actual);
        }
    }

    /** Builds an inner row map: column fields + is_valid. */
    private static Map<String, Object> row(boolean isValid, Map<String, String> fields) {
        Map<String, Object> m = new LinkedHashMap<>(fields);
        m.put("is_valid", isValid);
        return m;
    }

    /** Tiny varargs map builder: kv("a", "1", "b", "2") → {a=1, b=2}, ordered. */
    private static Map<String, String> kv(String... kvs) {
        if ((kvs.length & 1) != 0) throw new IllegalArgumentException("odd number of args");
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kvs.length; i += 2) m.put(kvs[i], kvs[i + 1]);
        return m;
    }

    private static String escape(String s) {
        return s.replace("\n", "\\n");
    }
}
