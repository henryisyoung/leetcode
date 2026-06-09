package airbnb.New2026;
/*
LeetCode 631: Design Excel Sum Formula.

Implement a small Excel-like sheet that supports value cells AND sum
formulas, with formulas auto-updating when their inputs change.

API
  Excel(int height, char width)
      A height x (width-'A'+1) sheet, rows 1..height, columns 'A'..width.
      All cells start at 0.

  void set(int row, char column, int val)
      Sets a cell to a constant value.  ANY existing formula on this cell
      is cleared.  Dependents (cells whose formulas reference this one)
      must be recomputed.

  int get(int row, char column)
      Returns the cell's current value.

  int sum(int row, char column, String[] numbers)
      Sets the cell to be the sum of the given expressions and returns
      the new value.  Each expression is either:
          "A1"      -- a single cell
          "A1:B3"   -- a rectangular range, inclusive on both ends
      The cell becomes a formula cell; if any of its inputs change later,
      the cell auto-updates.

Example
  Excel sheet = new Excel(3, 'C');
  sheet.set(1, 'A', 2);
  sheet.sum(3, 'C', ["A1", "A1:B2"]);   // -> 4   (A1 + A1+A2+B1+B2 = 2+2+0+0+0)
  sheet.set(2, 'B', 2);                  //  cascade to (3,'C')
  sheet.get(3, 'C');                     // -> 6   (now 2 + 2+0+0+2)

Assumptions
  - Single-letter columns 'A'..'Z'                 (LC bounds).
  - Range expressions never have a row or col gap.
  - The dependency graph is acyclic (LC test data does not create cycles).

Complexity
  set       O(D)            D = number of transitive dependents
  get       O(1)
  sum       O(K + D)        K = number of cells inside the formula's expansion
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
Design

  Each cell carries either a value or a formula (a list of cell/range
  expressions).  When a formula references cell X, we add a back-edge:
  X stores the set of cells that depend on it.

  Eager evaluation:
    On set / sum, we recompute the changed cell and then walk its
    dependents recursively, recomputing each from its own formula.
    Because the graph is acyclic, the recursion terminates in O(D).

  Why eager (not lazy):
    get() is called every time the UI repaints; eager keeps it O(1).
    The trade-off is that set() pays the cascade.  For Excel-style
    workloads this is the right side of the trade.

  Edge bookkeeping:
    When a cell's formula changes (or is cleared), we must REMOVE the
    cell from each old input's dependent set.  Skipping this leaks
    edges and corrupts later cascades.

  Storage:
    Cells are addressed by their string id ("A1", "B3", ...).  Three
    maps -- value, formula, dependents -- keep all state.  Empty cells
    aren't materialized; getOrDefault returns 0.  This makes the sheet
    sparse-friendly and avoids the 2D-array / generic-array / packed-key
    machinery a grid layout would need.
*/
public class DesignExcelSumFormula {

    public static class Excel {
        // id -> current numeric value.  Missing entry means 0.
        private final Map<String, Integer> values = new HashMap<>();

        // id -> raw formula expressions, e.g. ["A1", "A1:B2"].  Absent for value cells.
        private final Map<String, List<String>> formulas = new HashMap<>();

        // id -> set of cells whose formulas reference this one (back-edges for cascade).
        private final Map<String, Set<String>> dependents = new HashMap<>();

        public Excel(int height, char width) {
            // Sparse storage; height/width carried only to validate calls if desired.
            // We don't enforce bounds here -- LC tests stay within them.
        }

        public void set(int row, char column, int v) {
            String id = id(row, column);
            clearFormula(id);
            values.put(id, v);
            cascade(id);
        }

        public int get(int row, char column) {
            return values.getOrDefault(id(row, column), 0);
        }

        public int sum(int row, char column, String[] numbers) {
            String id = id(row, column);
            clearFormula(id);
            formulas.put(id, new ArrayList<>(Arrays.asList(numbers)));

            // Wire up new dependencies: every input cell now points at `id`.
            for (String expr : numbers) {
                for (String input : expand(expr)) {
                    dependents.computeIfAbsent(input, k -> new HashSet<>()).add(id);
                }
            }
            recompute(id);
            cascade(id);
            return values.getOrDefault(id, 0);
        }

        /* ---------- internal ---------- */

        private void clearFormula(String id) {
            List<String> old = formulas.remove(id);
            if (old == null) return;
            for (String expr : old) {
                for (String input : expand(expr)) {
                    Set<String> back = dependents.get(input);
                    if (back != null) back.remove(id);
                }
            }
        }

        /** Recompute id's value if it has a formula; no-op for plain values. */
        private void recompute(String id) {
            List<String> f = formulas.get(id);
            if (f == null) return;
            int s = 0;
            for (String expr : f) {
                for (String input : expand(expr)) {
                    s += values.getOrDefault(input, 0);
                }
            }
            values.put(id, s);
        }

        /**
         * Walk dependents recursively, recomputing each.  Acyclic graph
         * by assumption -> terminates without a visited set.  A diamond
         * (A -> B, A -> C, both -> D) recomputes D twice; we accept that
         * for simplicity.  At Excel scale we'd switch to a topological pass.
         */
        private void cascade(String id) {
            Set<String> back = dependents.get(id);
            if (back == null) return;
            // Snapshot: a recompute could in theory mutate the set on weird
            // re-entry; cheap insurance against ConcurrentModificationException.
            for (String dep : new ArrayList<>(back)) {
                recompute(dep);
                cascade(dep);
            }
        }

        /** Expand "A1" or "A1:B3" into the list of cell ids it covers. */
        private static List<String> expand(String expr) {
            List<String> out = new ArrayList<>();
            int colon = expr.indexOf(':');
            if (colon < 0) {
                out.add(expr);
                return out;
            }
            int[] a = parse(expr.substring(0, colon));
            int[] b = parse(expr.substring(colon + 1));
            int r1 = Math.min(a[0], b[0]), r2 = Math.max(a[0], b[0]);
            int c1 = Math.min(a[1], b[1]), c2 = Math.max(a[1], b[1]);
            for (int r = r1; r <= r2; r++) {
                for (int c = c1; c <= c2; c++) {
                    out.add(idFromIdx(r, c));
                }
            }
            return out;
        }

        /** "A1" -> {row=1, col=0}.  Single-letter columns only (matches LC bounds). */
        private static int[] parse(String s) {
            int col = s.charAt(0) - 'A';
            int row = Integer.parseInt(s.substring(1));
            return new int[]{row, col};
        }

        private static String id(int row, char col) { return "" + col + row; }

        private static String idFromIdx(int row, int col) {
            return "" + (char) ('A' + col) + row;
        }
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        // LC 631 example.
        Excel sh = new Excel(3, 'C');
        sh.set(1, 'A', 2);
        check("LC sum 1", sh.sum(3, 'C', new String[]{"A1", "A1:B2"}), 4);
        sh.set(2, 'B', 2);
        check("LC get after cascade", sh.get(3, 'C'), 6);

        // get() basic.
        Excel a = new Excel(3, 'C');
        check("default get is 0", a.get(2, 'B'), 0);
        a.set(2, 'B', 7);
        check("set then get", a.get(2, 'B'), 7);

        // Overwriting a formula with set() removes the formula.
        Excel b = new Excel(3, 'C');
        b.set(1, 'A', 5);
        b.sum(2, 'A', new String[]{"A1"});
        check("formula reads input", b.get(2, 'A'), 5);
        b.set(2, 'A', 100);
        check("set replaces formula with constant", b.get(2, 'A'), 100);
        b.set(1, 'A', 0);
        check("changing prev input does not touch overwritten cell",
                b.get(2, 'A'), 100);

        // Replacing a formula with another formula clears old dependencies.
        Excel c = new Excel(3, 'C');
        c.set(1, 'A', 1);
        c.set(1, 'B', 100);
        c.sum(3, 'C', new String[]{"A1"});           // depends on A1 only
        check("first formula = A1", c.get(3, 'C'), 1);
        c.sum(3, 'C', new String[]{"B1"});           // now depends on B1 only
        check("second formula = B1", c.get(3, 'C'), 100);
        c.set(1, 'A', 999);                            // must NOT cascade to (3,C) anymore
        check("A1 changes don't affect (3,C) after re-sum", c.get(3, 'C'), 100);
        c.set(1, 'B', 50);                             // SHOULD cascade
        check("B1 changes still cascade", c.get(3, 'C'), 50);

        // Range expansion.
        Excel d = new Excel(3, 'C');
        d.set(1, 'A', 1);
        d.set(1, 'B', 2);
        d.set(2, 'A', 3);
        d.set(2, 'B', 4);
        check("range A1:B2 sum",
                d.sum(3, 'C', new String[]{"A1:B2"}), 1 + 2 + 3 + 4);

        // Transitive cascade: A1 -> B1 -> C1 (each is a sum of the prior).
        Excel e = new Excel(3, 'C');
        e.set(1, 'A', 5);
        e.sum(1, 'B', new String[]{"A1"});           // B1 = A1 = 5
        e.sum(1, 'C', new String[]{"B1"});           // C1 = B1 = 5
        check("transitive A->B->C initial", e.get(1, 'C'), 5);
        e.set(1, 'A', 9);
        check("transitive A->B->C after set", e.get(1, 'C'), 9);

        // Repeated reference to the same cell counts each occurrence.
        Excel f = new Excel(3, 'C');
        f.set(1, 'A', 3);
        check("same cell listed twice is summed twice",
                f.sum(2, 'A', new String[]{"A1", "A1"}), 6);

        // sum() returns the freshly computed value.
        Excel g = new Excel(3, 'C');
        g.set(1, 'A', 10);
        check("sum returns computed value",
                g.sum(2, 'A', new String[]{"A1"}), 10);
    }

    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK    " : "FAIL  ") + label
                + " expected=" + expected + " got=" + got);
    }
}
