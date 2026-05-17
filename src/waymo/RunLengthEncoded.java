package waymo;
/*
Run-Length Encoding with index queries.

Spec
  encode({'B','A','A','E','E','E','C'}) -> "B1A2E3C1"

Required
  char find(int p)        // character at original (decoded) index p

Follow-up (input is assumed SORTED ASCENDING)
  char findByValue(char target, int left, int right)
      -> the SMALLEST character strictly greater than target within the
         original index range [left, right], or '\0' if none exists.

Why those queries are interesting on RLE
  The encoding compresses long runs into one (char, count) entry, so we
  can binary-search the per-run prefix-sums to locate the run containing
  any original index without expanding the data.  Both queries run in
  O(log R) where R is the number of runs (<= n, often << n).

Assumptions on the value alphabet
  Values are non-digit characters; counts are non-negative decimal
  integers.  This keeps the textual format (e.g. "A10B3") unambiguous
  to parse with the simple "letter then digits" rule.

Complexity
  encode / parse / toString : O(n) on the data length
  find                      : O(log R)
  findByValue               : O(log R)
  memory                    : O(R)
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public final class RunLengthEncoded {

    /** Sentinel returned by {@link #findByValue} when no greater char exists in range. */
    public static final char NONE = '\0';

    private final char[] runChars;     // runChars[i] = the character of run i
    private final int[]  runLens;      // runLens[i]  = count of run i
    private final int[]  runStarts;    // runStarts[i] = sum of runLens[0..i-1]
    private final int    totalLength;

    private RunLengthEncoded(char[] runChars, int[] runLens) {
        this.runChars = runChars;
        this.runLens  = runLens;
        this.runStarts = new int[runChars.length];
        long total = 0;
        for (int i = 0; i < runChars.length; i++) {
            if (runLens[i] <= 0) throw new IllegalArgumentException("run length must be positive");
            runStarts[i] = (int) total;
            total += runLens[i];
        }
        if (total > Integer.MAX_VALUE) throw new IllegalArgumentException("total length overflow");
        this.totalLength = (int) total;
    }

    /* --------------------------- Build / serialize --------------------------- */

    /** Build an RLE from a raw char array.  Empty input is allowed. */
    public static RunLengthEncoded encode(char[] data) {
        if (data == null) throw new IllegalArgumentException("data is null");
        if (data.length == 0) return new RunLengthEncoded(new char[0], new int[0]);

        // First pass: count runs so we can size the arrays exactly.
        int runs = 1;
        for (int i = 1; i < data.length; i++) if (data[i] != data[i - 1]) runs++;

        char[] chars = new char[runs];
        int[] lens = new int[runs];
        int idx = 0;
        chars[0] = data[0];
        lens[0]  = 1;
        for (int i = 1; i < data.length; i++) {
            if (data[i] == data[i - 1]) {
                lens[idx]++;
            } else {
                idx++;
                chars[idx] = data[i];
                lens[idx]  = 1;
            }
        }
        return new RunLengthEncoded(chars, lens);
    }

    /** Parse an encoded string in the "B1A2E3C1" format. */
    public static RunLengthEncoded parse(String encoded) {
        if (encoded == null) throw new IllegalArgumentException("encoded is null");
        List<Character> chars = new ArrayList<>();
        List<Integer> lens = new ArrayList<>();
        int i = 0, n = encoded.length();
        while (i < n) {
            char c = encoded.charAt(i++);
            if (Character.isDigit(c)) throw new IllegalArgumentException("expected value char at " + (i - 1));
            int count = 0, started = i;
            while (i < n && Character.isDigit(encoded.charAt(i))) {
                count = count * 10 + (encoded.charAt(i) - '0');
                i++;
            }
            if (i == started) throw new IllegalArgumentException("missing count after '" + c + "'");
            chars.add(c);
            lens.add(count);
        }
        char[] chArr = new char[chars.size()];
        int[]  lnArr = new int[chars.size()];
        for (int k = 0; k < chars.size(); k++) {
            chArr[k] = chars.get(k);
            lnArr[k] = lens.get(k);
        }
        return new RunLengthEncoded(chArr, lnArr);
    }

    /** Serialise back to the textual "B1A2E3C1" form. */
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < runChars.length; i++) sb.append(runChars[i]).append(runLens[i]);
        return sb.toString();
    }

    /** Length of the original (decoded) sequence. */
    public int length() { return totalLength; }

    /** Decode back to the original char[] (mostly useful for tests). */
    public char[] decode() {
        char[] out = new char[totalLength];
        int p = 0;
        for (int i = 0; i < runChars.length; i++) {
            Arrays.fill(out, p, p + runLens[i], runChars[i]);
            p += runLens[i];
        }
        return out;
    }

    /* --------------------------- find(p) --------------------------- */

    /** Returns the character at original index {@code p}.  O(log R). */
    public char find(int p) {
        if (p < 0 || p >= totalLength) throw new IndexOutOfBoundsException("p=" + p + " len=" + totalLength);
        return runChars[runIndexAt(p)];
    }

    /** Largest run index {@code i} such that {@code runStarts[i] <= p}.  Caller ensures p is in bounds. */
    private int runIndexAt(int p) {
        int lo = 0, hi = runChars.length - 1;
        while (lo < hi) {
            int mid = (lo + hi + 1) >>> 1;
            if (runStarts[mid] <= p) lo = mid;
            else hi = mid - 1;
        }
        return lo;
    }

    /* --------------------------- findByValue (follow-up) --------------------------- */

    /**
     * Returns the smallest character strictly greater than {@code target} in the
     * original index range {@code [left, right]}, or {@link #NONE} ('\0') if no
     * such character exists in range.
     *
     * Assumes the underlying data is SORTED ASCENDING (so {@code runChars[]} is
     * strictly increasing — the alphabet at higher indices is monotonically
     * larger).
     */
    public char findByValue(char target, int left, int right) {
        if (left < 0 || right >= totalLength || left > right) {
            throw new IndexOutOfBoundsException("range [" + left + ", " + right + "] vs len " + totalLength);
        }

        // Char at index `left` is the smallest char in [left, right] (data is sorted).
        // If it's already > target, we're done.
        int leftRun = runIndexAt(left);
        if (runChars[leftRun] > target) return runChars[leftRun];

        // Otherwise find the first run whose char > target (chars[] is strictly
        // increasing under the sorted-input assumption).
        int gtIdx = firstRunCharGreater(target);
        if (gtIdx == runChars.length) return NONE;       // no char > target exists at all
        if (runStarts[gtIdx] > right) return NONE;        // exists but starts past `right`
        return runChars[gtIdx];
    }

    /** Smallest index i in runChars[] with runChars[i] > target, or runChars.length if none. */
    private int firstRunCharGreater(char target) {
        int lo = 0, hi = runChars.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (runChars[mid] > target) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    /* --------------------------- Demo + tests --------------------------- */

    public static void main(String[] args) {
        /* ---- 1. encode / toString round-trip ---- */
        char[] spec = {'B', 'A', 'A', 'E', 'E', 'E', 'C'};
        RunLengthEncoded r = RunLengthEncoded.encode(spec);
        expect("B1A2E3C1".equals(r.toEncodedString()), "encode -> string");
        expect(Arrays.equals(spec, r.decode()), "decode round-trip");
        expect(r.length() == 7, "length");

        /* ---- 2. parse round-trip ---- */
        RunLengthEncoded p = RunLengthEncoded.parse("B1A2E3C1");
        expect(Arrays.equals(spec, p.decode()), "parse round-trip");
        // parse handles multi-digit counts:
        RunLengthEncoded big = RunLengthEncoded.parse("A10B3");
        expect(big.length() == 13, "parse multi-digit count");
        expect(big.find(9) == 'A' && big.find(10) == 'B', "multi-digit content");

        /* ---- 3. find(p) on every index ---- */
        // original = "BAAEEEC"
        char[] expected = {'B', 'A', 'A', 'E', 'E', 'E', 'C'};
        for (int i = 0; i < expected.length; i++) {
            expect(r.find(i) == expected[i], "find(" + i + ")");
        }
        // out-of-bounds throws
        try { r.find(-1); throw new AssertionError("find(-1) should throw"); }
        catch (IndexOutOfBoundsException ok) { /* expected */ }
        try { r.find(7); throw new AssertionError("find(7) should throw"); }
        catch (IndexOutOfBoundsException ok) { /* expected */ }

        /* ---- 4. findByValue on a sorted input ---- */
        // original = "AABBCCCDDDDD" (length 12)
        // runs: A(0..1) B(2..3) C(4..6) D(7..11)
        RunLengthEncoded s = RunLengthEncoded.encode("AABBCCCDDDDD".toCharArray());
        expect("A2B2C3D5".equals(s.toEncodedString()), "sorted encode");

        // strictly-greater queries over the full range
        expect(s.findByValue('A', 0, 11) == 'B', "first > A in full range");
        expect(s.findByValue('B', 0, 11) == 'C', "first > B in full range");
        expect(s.findByValue('C', 0, 11) == 'D', "first > C in full range");
        expect(s.findByValue('D', 0, 11) == NONE, "first > D in full range -> none");
        expect(s.findByValue('Z', 0, 11) == NONE, "first > Z -> none");
        // Below the alphabet -> smallest in range.
        expect(s.findByValue((char)('A' - 1), 0, 11) == 'A', "below-alphabet target");

        // Range queries: B is at [2,3], C is at [4,6].
        expect(s.findByValue('B', 0, 3) == NONE, "first > B in [0,3] (no C yet)");
        expect(s.findByValue('B', 0, 4) == 'C',  "first > B in [0,4] (C just enters)");
        expect(s.findByValue('A', 5, 11) == 'C', "left=5: char at 5 is C, > A -> return C");
        expect(s.findByValue('A', 7, 11) == 'D', "[7,11] holds only Ds");
        expect(s.findByValue('B', 7, 11) == 'D', "left in D-run, > B -> D");

        /* ---- 5. Random fuzz of findByValue against linear scan ---- */
        Random rnd = new Random(31);
        int fails = 0;
        for (int trial = 0; trial < 500; trial++) {
            int len = 1 + rnd.nextInt(50);
            char[] data = new char[len];
            for (int i = 0; i < len; i++) data[i] = (char) ('a' + rnd.nextInt(8));
            Arrays.sort(data);
            RunLengthEncoded rle = RunLengthEncoded.encode(data);

            int left = rnd.nextInt(len);
            int right = left + rnd.nextInt(len - left);
            char target = (char) ('a' + rnd.nextInt(10));   // can be outside the alphabet [a..h]

            char got = rle.findByValue(target, left, right);
            char want = NONE;
            for (int i = left; i <= right; i++) {
                if (data[i] > target) { want = data[i]; break; }
            }
            if (got != want) {
                fails++;
                System.out.println("FUZZ MISMATCH data=" + new String(data)
                        + " target=" + target + " [" + left + "," + right + "]"
                        + " got=" + (int) got + " want=" + (int) want);
            }
        }
        System.out.println("findByValue fuzz: " + (500 - fails) + "/500 ok");

        /* ---- 6. Random fuzz of find(p) against linear scan ---- */
        fails = 0;
        for (int trial = 0; trial < 500; trial++) {
            int len = 1 + rnd.nextInt(100);
            char[] data = new char[len];
            for (int i = 0; i < len; i++) data[i] = (char) ('a' + rnd.nextInt(5));
            RunLengthEncoded rle = RunLengthEncoded.encode(data);
            for (int i = 0; i < len; i++) {
                if (rle.find(i) != data[i]) { fails++; break; }
            }
        }
        System.out.println("find(p) fuzz: " + (500 - fails) + "/500 ok");

        System.out.println("All tests passed.");
    }

    private static void expect(boolean cond, String name) {
        if (!cond) throw new AssertionError("FAIL: " + name);
        System.out.println("OK   " + name);
    }

    /** Tiny utility used by some callers / clients — left in for completeness. */
    @SuppressWarnings("unused")
    private static char requireNonNull(Character c) {
        if (c == null) throw new NoSuchElementException();
        return c;
    }
}
