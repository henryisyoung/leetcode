package airbnb.New2026;

/*
Bordered Sentence Table

Given a list of sentences and a fixed content width, render each sentence as one
row in an ASCII bordered table.

Rules:
  - Each sentence stays on one single line. No wrapping or splitting.
  - Content area width is fixed.
  - If a sentence is shorter than width, pad spaces on the right.
  - Row format: "| " + paddedSentence + " |"
  - Border format: "+" + "-" repeated (width + 2) + "+"
  - Print a border line above and below each sentence row.

Follow-up: multiple columns
  - Each cell has the same content width.
  - Sentences are filled row-major into `cols` columns.
  - Missing cells in the last row are rendered as blank cells.
  - Border has one segment per column.

Example:
  sentences = ["Hello world", "How are you today", "Bye"]
  width = 55

  +---------------------------------------------------------+
  | Hello world                                             |
  +---------------------------------------------------------+
  | How are you today                                       |
  +---------------------------------------------------------+
  | Bye                                                     |
  +---------------------------------------------------------+

Clarify with interviewer:
  - If sentence.length() > width, should we truncate, throw, or allow overflow?
    This implementation throws IllegalArgumentException.
  - Empty sentence is valid and renders as a blank padded row.
  - Empty input returns an empty list.

Complexity:
  Time:  O(total output characters)
  Space: O(total output characters) for returned lines
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BorderedSentenceTable {

    public List<String> render(List<String> sentences, int width) {
        if (width < 1) throw new IllegalArgumentException("width must be positive");

        List<String> result = new ArrayList<>();
        if (sentences == null || sentences.isEmpty()) return result;

        String border = buildBorder(width);
        for (String sentence : sentences) {
            String text = sentence == null ? "" : sentence;
            if (text.length() > width) {
                throw new IllegalArgumentException("sentence longer than width: " + text);
            }

            result.add(border);
            result.add("| " + rightPad(text, width) + " |");
        }
        result.add(border);
        return result;
    }

    public List<String> renderMultiColumn(List<String> sentences, int width, int cols) {
        if (width < 1) throw new IllegalArgumentException("width must be positive");
        if (cols < 1) throw new IllegalArgumentException("cols must be positive");

        List<String> result = new ArrayList<>();
        if (sentences == null || sentences.isEmpty()) return result;

        String border = buildBorder(width, cols);
        for (int i = 0; i < sentences.size(); i += cols) {
            result.add(border);

            StringBuilder row = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                int idx = i + c;
                String text = idx < sentences.size() && sentences.get(idx) != null ? sentences.get(idx) : "";
                if (text.length() > width) {
                    throw new IllegalArgumentException("sentence longer than width: " + text);
                }
                row.append("| ").append(rightPad(text, width)).append(' ');
            }
            row.append('|');
            result.add(row.toString());
        }
        result.add(border);
        return result;
    }

    private String buildBorder(int width) {
        char[] dashes = new char[width + 2];
        Arrays.fill(dashes, '-');
        return "+" + new String(dashes) + "+";
    }

    private String buildBorder(int width, int cols) {
        char[] dashes = new char[width + 2];
        Arrays.fill(dashes, '-');
        String segment = new String(dashes);

        StringBuilder sb = new StringBuilder(cols * (width + 3) + 1);
        for (int i = 0; i < cols; i++) {
            sb.append('+').append(segment);
        }
        sb.append('+');
        return sb.toString();
    }

    private String rightPad(String s, int width) {
        StringBuilder sb = new StringBuilder(width);
        sb.append(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    public static void main(String[] args) {
        BorderedSentenceTable table = new BorderedSentenceTable();

        check("sample",
                table.render(Arrays.asList("Hello world", "How are you today", "Bye"), 55),
                "+---------------------------------------------------------+",
                "| Hello world                                             |",
                "+---------------------------------------------------------+",
                "| How are you today                                       |",
                "+---------------------------------------------------------+",
                "| Bye                                                     |",
                "+---------------------------------------------------------+");

        check("exact width",
                table.render(Arrays.asList("hello"), 5),
                "+-------+",
                "| hello |",
                "+-------+");

        check("empty sentence",
                table.render(Arrays.asList(""), 3),
                "+-----+",
                "|     |",
                "+-----+");

        check("empty input",
                table.render(new ArrayList<>(), 4));

        check("multi column even",
                table.renderMultiColumn(Arrays.asList("A", "B", "C", "D"), 3, 2),
                "+-----+-----+",
                "| A   | B   |",
                "+-----+-----+",
                "| C   | D   |",
                "+-----+-----+");

        check("multi column uneven",
                table.renderMultiColumn(Arrays.asList("A", "B", "C"), 3, 2),
                "+-----+-----+",
                "| A   | B   |",
                "+-----+-----+",
                "| C   |     |",
                "+-----+-----+");

        try {
            table.render(Arrays.asList("toolong"), 3);
            throw new AssertionError("expected long sentence to throw");
        } catch (IllegalArgumentException expected) {
            System.out.println("too long: threw");
        }

        System.out.println("All tests passed.");
    }

    private static void check(String label, List<String> actual, String... expectedLines) {
        List<String> expected = Arrays.asList(expectedLines);
        if (!actual.equals(expected)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
        System.out.println(label + ": ok");
    }
}
