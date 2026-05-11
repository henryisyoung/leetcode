package waymo;
/*
Design a program to generate a Bingo card (75-ball, 5x5).

Rules
  - 5x5 grid with column labels B / I / N / G / O.
  - Each column draws numbers from a fixed range of 15:
        B  : 1..15        I : 16..30       N : 31..45
        G  : 46..60       O : 61..75
  - Numbers within a column are distinct (sampled without replacement).
  - The centre cell (row 2, column 2) is a FREE space — no number, no
    sampling slot for that one position.
  - We render the centre as blank (matching the spec's example).

Output
  A 5x5 int[][]; the centre cell uses 0 as the sentinel value for "FREE".
  A formatter renders the card as text, with FREE shown as blank.

Example
  B  I  N  G  O
   8 17 32 50 65
  11 23 41 51 70
  15 27    47 75
   3 16 39 60 63
   5 20 33 56 67
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/*
Algorithm: per-column reservoir-style sampling without replacement.

  For each column c (0..4):
    - The range is [c*15 + 1 .. c*15 + 15]   (B = 1..15, I = 16..30, ...).
    - We need 5 distinct samples from that range — except column 2 (N), where
      we only fill 4 slots because the middle row is the FREE cell.
    - Fisher–Yates partial shuffle: shuffle the 15-element pool just enough
      to expose the first k = 4 or 5 elements, then take them.

  Why partial Fisher–Yates and not "HashSet + retry":
    - Reject-on-collision is O(k) expected but unbounded worst case; partial
      shuffle is O(k) deterministic and just as simple.
    - Cost is dominated by the constant-time slot copies — not the RNG calls.

  Centre placement:
    - We sample 4 numbers for column N and write them into rows {0, 1, 3, 4}.
    - card[2][2] stays 0, which the formatter treats as the FREE marker.

  Determinism:
    - All randomness flows through one injectable Random.  Constructors take
      a seed so tests are reproducible.

Complexity
  Time:  O(1) per card (5 columns × 5 draws + a tiny shuffle).
  Memory: O(1) per card (just the 5x5 result).
*/
public class BingoCardGenerator {

    private static final int SIZE = 5;
    private static final int RANGE = 15;                // each column spans 15 consecutive numbers
    private static final int CENTRE_ROW = 2;
    private static final int CENTRE_COL = 2;
    private static final int FREE = 0;                  // sentinel value in the grid
    private static final String[] LABELS = {"B", "I", "N", "G", "O"};

    private final Random random;

    public BingoCardGenerator() { this(new Random()); }
    public BingoCardGenerator(long seed) { this(new Random(seed)); }
    public BingoCardGenerator(Random random) { this.random = random; }

    /* --------------------------- Generation --------------------------- */

    /** Generate a single 5×5 card.  The centre cell is {@link #FREE} (= 0). */
    public int[][] generate() {
        int[][] card = new int[SIZE][SIZE];
        for (int col = 0; col < SIZE; col++) {
            int start = col * RANGE + 1;                 // 1, 16, 31, 46, 61
            boolean isCentreColumn = (col == CENTRE_COL);
            int draws = isCentreColumn ? SIZE - 1 : SIZE;
            int[] picks = sampleWithoutReplacement(start, RANGE, draws);

            int pickIdx = 0;
            for (int row = 0; row < SIZE; row++) {
                if (isCentreColumn && row == CENTRE_ROW) {
                    card[row][col] = FREE;
                } else {
                    card[row][col] = picks[pickIdx++];
                }
            }
        }
        return card;
    }

    /** Generate {@code n} independent cards. */
    public int[][][] generate(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        int[][][] cards = new int[n][][];
        for (int i = 0; i < n; i++) cards[i] = generate();
        return cards;
    }

    /** Fisher–Yates partial shuffle: returns k distinct values from [start, start + rangeSize). */
    private int[] sampleWithoutReplacement(int start, int rangeSize, int k) {
        if (k > rangeSize) throw new IllegalArgumentException("k > rangeSize");
        int[] pool = new int[rangeSize];
        for (int i = 0; i < rangeSize; i++) pool[i] = start + i;
        for (int i = 0; i < k; i++) {
            int j = i + random.nextInt(rangeSize - i);
            int tmp = pool[i]; pool[i] = pool[j]; pool[j] = tmp;
        }
        int[] out = new int[k];
        System.arraycopy(pool, 0, out, 0, k);
        return out;
    }

    /* --------------------------- Validation --------------------------- */

    /** Throws AssertionError if the card breaks any Bingo rule.  Used by tests. */
    public static void validate(int[][] card) {
        if (card.length != SIZE) throw new AssertionError("wrong row count");
        for (int[] row : card) {
            if (row.length != SIZE) throw new AssertionError("wrong column count");
        }
        if (card[CENTRE_ROW][CENTRE_COL] != FREE) {
            throw new AssertionError("centre cell must be FREE (0)");
        }
        for (int col = 0; col < SIZE; col++) {
            int lo = col * RANGE + 1, hi = col * RANGE + RANGE;
            Set<Integer> seen = new HashSet<>();
            for (int row = 0; row < SIZE; row++) {
                int v = card[row][col];
                if (col == CENTRE_COL && row == CENTRE_ROW) {
                    if (v != FREE) throw new AssertionError("centre must be FREE");
                    continue;
                }
                if (v < lo || v > hi) {
                    throw new AssertionError("col " + col + " value " + v + " not in [" + lo + ".." + hi + "]");
                }
                if (!seen.add(v)) {
                    throw new AssertionError("duplicate in column " + col + ": " + v);
                }
            }
        }
    }

    /* --------------------------- Rendering --------------------------- */

    /** Pretty-print the card.  Renders FREE as blank, matching the spec. */
    public static String format(int[][] card) {
        StringBuilder sb = new StringBuilder();
        // Header: "B  I  N  G  O" — two spaces between letters so it aligns with 2-digit cells.
        for (int c = 0; c < SIZE; c++) {
            if (c > 0) sb.append(' ');
            sb.append(' ').append(LABELS[c]);
        }
        sb.append('\n');
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (c > 0) sb.append(' ');
                int v = card[r][c];
                if (v == FREE) {
                    sb.append("  ");                    // blank FREE cell
                } else {
                    sb.append(String.format("%2d", v)); // right-align in 2 chars
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        // Always run demos.  We don't take stdin: the spec says "Input: None".
        runDemos();
    }

    private static void runDemos() {
        // Reproducible: fixed seed.
        BingoCardGenerator solver = new BingoCardGenerator(42L);

        // ---------- One sample card (seed = 42L) ----------
        int[][] card = solver.generate();
        validate(card);
        System.out.println("Sample card (seed=42):");
        System.out.println(format(card));

        // ---------- Validate that 1000 cards all conform to the rules ----------
        int trials = 1_000;
        int[] freqByValue = new int[76];   // values 1..75; index 0 unused
        Map<Integer, Integer> centreSentinel = new HashMap<>();
        for (int t = 0; t < trials; t++) {
            int[][] c = solver.generate();
            validate(c);
            for (int r = 0; r < SIZE; r++) {
                for (int col = 0; col < SIZE; col++) {
                    int v = c[r][col];
                    if (v == FREE) {
                        centreSentinel.merge((r * SIZE) + col, 1, Integer::sum);
                    } else {
                        freqByValue[v]++;
                    }
                }
            }
        }
        // Every non-centre slot drew a real value across all trials.
        System.out.println(trials + " cards validated successfully.");

        // The centre cell — and only the centre cell — should be FREE across all trials.
        int centreKey = CENTRE_ROW * SIZE + CENTRE_COL;
        boolean centreOk = centreSentinel.size() == 1
                && centreSentinel.containsKey(centreKey)
                && centreSentinel.get(centreKey) == trials;
        System.out.println((centreOk ? "OK   " : "FAIL ")
                + "centre-cell exclusivity: " + centreSentinel);

        // ---------- Uniformity check: every value in 1..75 should appear about equally often ----------
        // Expected frequency: each column's 15 values, 5 (or 4 for N) per card.
        //   B, I, G, O columns: 5/15 cards include any given value → 5/15 * 1000 ≈ 333 hits each
        //   N column:           4/15 * 1000 ≈ 267 hits each
        // Allow ±30% tolerance for the chi-squared-ish band.
        int problems = 0;
        for (int v = 1; v <= 75; v++) {
            int col = (v - 1) / RANGE;
            double expected = (col == CENTRE_COL ? 4.0 : 5.0) * trials / RANGE;
            double diff = Math.abs(freqByValue[v] - expected);
            if (diff > 0.3 * expected) {
                problems++;
                System.out.printf("  unusual frequency for %d (col=%s): got=%d expected~=%.0f%n",
                        v, LABELS[col], freqByValue[v], expected);
            }
        }
        System.out.println((problems == 0 ? "OK   " : "FAIL ")
                + "all 75 values appear roughly uniformly across " + trials + " cards"
                + " (" + problems + " outliers > ±30%)");

        // ---------- Two seeded calls reproduce the same card ----------
        int[][] a = new BingoCardGenerator(7).generate();
        int[][] b = new BingoCardGenerator(7).generate();
        boolean reproducible = Arrays.deepEquals(a, b);
        System.out.println((reproducible ? "OK   " : "FAIL ")
                + "seed=7 reproduces the same card across two runs");

        // ---------- Multiple cards at once ----------
        int[][][] batch = solver.generate(3);
        boolean batchOk = batch.length == 3;
        for (int[][] c : batch) {
            try {
                validate(c);
            } catch (AssertionError e) {
                batchOk = false;
                break;
            }
        }
        System.out.println((batchOk ? "OK   " : "FAIL ")
                + "batch generate(3) produces 3 valid distinct cards");

        // Reject negative batch.
        try {
            solver.generate(-1);
            System.out.println("FAIL  generate(-1) should have thrown");
        } catch (IllegalArgumentException ex) {
            System.out.println("OK   generate(-1) threw " + ex.getMessage());
        }

        // ---------- Show two more rendered cards ----------
        System.out.println("\nTwo more sample cards:");
        for (int i = 0; i < 2; i++) {
            int[][] c = solver.generate();
            System.out.println(format(c));
        }
    }
}
