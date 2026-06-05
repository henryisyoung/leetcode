package airbnb.New2026;

import java.util.Arrays;

/*
Terrain Rendering + Water Drop Simulation  (Airbnb).

Part 1 — render terrain
  Given column heights, print an ASCII column chart: each column has
  heights[i] '+' stacked on a shared baseline, printed top-down.

Part 2 — drop water
  dumpWater(heights, waterAmount, column): drop W units of water one at a
  time at `column`. Each unit flows downhill and pools in valleys, then we
  re-render with 'W' for settled water and '+' for ground.

Flow rule for ONE unit (per this prompt's variant)
  Let resting[i] = heights[i] + water[i]. From the drop column c:
    - scan LEFT  until a column whose resting height is >= resting[c] (a wall)
      or the edge; the left "reachable min" is the lowest resting height seen.
    - scan RIGHT symmetrically.
    - the side with the STRICTLY LOWER reachable min wins; ties go LEFT.
    - if neither side can reach strictly lower than resting[c], the unit rests
      at c.
  State persists across units (later units sit on earlier ones).

  NOTE: this "lower-min side wins" rule differs from the classic LC-755
  "PourWater" left-priority rule (which always prefers left if it can fall
  left at all). Clarify the convention with the interviewer.

Complexity
  dumpWater: O(W · n) worst case (each unit scans outward).
  render:    O(maxHeight · n).
*/
public class TerrainWaterDrop {

    /* ----------------------------- Part 1 ----------------------------- */

    /** Render terrain only (no water) and return the multi-line string. */
    public static String renderTerrain(int[] heights) {
        return render(heights, new int[heights.length]);
    }

    /* ----------------------------- Part 2 ----------------------------- */

    /** Drop `waterAmount` units at `column`; return the resulting water[] per column. */
    public static int[] dumpWater(int[] heights, int waterAmount, int column) {
        int[] water = new int[heights.length];
        for (int unit = 0; unit < waterAmount; unit++) {
            int dest = settle(heights, water, column);
            water[dest]++;
        }
        return water;
    }

    /** Drop water and return the rendered terrain+water picture. */
    public static String dumpWaterAndRender(int[] heights, int waterAmount, int column) {
        int[] water = dumpWater(heights, waterAmount, column);
        return render(heights, water);
    }

    /** Resting position of one unit dropped at column c, given current water state. */
    private static int settle(int[] h, int[] w, int c) {
        int n = h.length;
        int rc = combinedHeight(h, w, c);

        // Walk left over a non-increasing run; land at the far (leftmost) low end.
        int li = c;
        while (li - 1 >= 0 && combinedHeight(h, w, li - 1) <= combinedHeight(h, w, li)) li--;
        // Walk right over a non-increasing run; land at the far (rightmost) low end.
        int ri = c;
        while (ri + 1 < n && combinedHeight(h, w, ri + 1) <= combinedHeight(h, w, ri)) ri++;

        int lh = combinedHeight(h, w, li), rh = combinedHeight(h, w, ri);
        boolean leftOk = lh < rc, rightOk = rh < rc;

        if (leftOk && rightOk) return lh <= rh ? li : ri;   // lower min wins; tie -> left
        if (leftOk) return li;
        if (rightOk) return ri;
        return c;                                            // both block -> rest in place
    }

    private static int combinedHeight(int[] h, int[] w, int i) {
        return h[i] + w[i];
    }

    /* ----------------------------- rendering ----------------------------- */

    private static String render(int[] heights, int[] water) {
        int n = heights.length;
        int max = 0;
        for (int i = 0; i < n; i++) max = Math.max(max, heights[i] + water[i]);

        StringBuilder sb = new StringBuilder();
        for (int level = max; level >= 1; level--) {           // top row down to baseline
            for (int i = 0; i < n; i++) {
                if (level <= heights[i]) sb.append('+');
                else if (level <= heights[i] + water[i]) sb.append('W');
                else sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /* ----------------------------- demo ----------------------------- */

    public static void main(String[] args) {
        int[] heights = {5, 4, 3, 2, 1, 3, 4, 0, 3, 4};

        System.out.println("Part 1 — terrain:");
        System.out.print(renderTerrain(heights));

        System.out.println("\nPart 2 — dumpWater(W=8, column=1):");
        System.out.print(dumpWaterAndRender(heights, 8, 1));

        // Water conservation sanity check.
        int[] w = dumpWater(heights, 8, 1);
        int total = 0;
        for (int x : w) total += x;
        System.out.println("\nwater[] = " + Arrays.toString(w) + "  total=" + total + " (expect 8)");

        // Drop at the right end; water should fall left into the col-7 pit.
        System.out.println("\ndumpWater(W=3, column=9):");
        System.out.print(dumpWaterAndRender(heights, 3, 9));

        // Flat terrain: water cannot flow, stacks straight up at the column.
        int[] flat = {2, 2, 2};
        System.out.println("\nflat terrain, dumpWater(W=2, column=1):");
        System.out.print(dumpWaterAndRender(flat, 2, 1));
    }
}
