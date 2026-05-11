package waymo;
/*
Problem: Implement a Fibonacci Generator

Fibonacci definition
  F(0) = 0, F(1) = 1
  F(n) = F(n-1) + F(n-2)   for n >= 2

Requirements
  - Produce values lazily (generator semantics).  In Python that's `yield`;
    in Java the cleanest analogue is Iterator<T> — `hasNext` / `next` give
    you on-demand evaluation with O(1) state, exactly like a generator.
  - Start from F(0), in order: 0, 1, 1, 2, 3, 5, …
  - Read n from stdin, print the first n Fibonacci numbers separated by
    spaces.  Print nothing (an empty line is fine too) when n == 0.

Constraints
  0 <= n <= 1e5     (F(100000) has ~20,899 digits → MUST use BigInteger)

Example
  Input:  5
  Output: 0 1 1 2 3

Python reference solution (for completeness, given the spec says Python):

  def fibonacci():
      a, b = 0, 1
      while True:
          yield a
          a, b = b, a + b

  def main():
      n = int(input().strip())
      gen = fibonacci()
      print(' '.join(str(next(gen)) for _ in range(n)))

  if __name__ == '__main__':
      main()
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/*
Implementation notes
  - The Iterator carries only two BigIntegers of state (a, b), advancing
    one step per next() call.  hasNext() returns true forever — the
    sequence is infinite by definition; the *consumer* decides when to stop.
  - BigInteger is mandatory at n = 1e5.  F(100000) is ~21K digits, far
    beyond what long can hold (long maxes out around F(92)).
  - Output is buffered through PrintWriter — printing 100K big-number
    strings with raw System.out.println would dominate runtime.

Complexity (per next())
  Each Fibonacci number takes O(d_n) work where d_n = digit length of F(n).
  Since d_n grows linearly in n (≈ n * log10(φ) ≈ 0.209 * n), total work
  for the first n values is O(n^2 / log).  For n = 1e5 that's manageable
  in well under a second with Java BigInteger.
*/
public class FibonacciGenerator implements Iterable<BigInteger> {

    /* --------------------------- Generator (Iterator) --------------------------- */

    @Override
    public Iterator<BigInteger> iterator() {
        return new FibIterator();
    }

    /** A standalone iterator that yields F(0), F(1), F(2), … forever. */
    public static final class FibIterator implements Iterator<BigInteger> {
        private BigInteger a = BigInteger.ZERO;
        private BigInteger b = BigInteger.ONE;

        @Override
        public boolean hasNext() {
            return true;    // sequence is infinite; bound the consumer, not the generator.
        }

        @Override
        public BigInteger next() {
            BigInteger out = a;
            BigInteger nxt = a.add(b);
            a = b;
            b = nxt;
            return out;
        }
    }

    /** Convenience: collect the first n Fibonacci numbers into a list.  Throws if n < 0. */
    public static List<BigInteger> take(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0, got " + n);
        List<BigInteger> out = new ArrayList<>(n);
        Iterator<BigInteger> it = new FibIterator();
        for (int i = 0; i < n; i++) out.add(it.next());
        return out;
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        try {
            Iterator<BigInteger> it = new FibIterator();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (i > 0) sb.append(' ');
                sb.append(it.next().toString());
                // Flush periodically so we don't accumulate megabytes in memory.
                if (sb.length() > 1 << 16) {
                    out.print(sb);
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) out.print(sb);
            if (n > 0) out.println();
        } finally {
            out.flush();
        }
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        // Spec examples.
        check(5, "0 1 1 2 3");
        check(0, "");

        // First 10 values — covers the "F(1) appears twice" gotcha.
        check(10, "0 1 1 2 3 5 8 13 21 34");

        // Single value (n = 1) prints only F(0).
        check(1, "0");

        // Two values (n = 2) — F(0), F(1).
        check(2, "0 1");

        // Generator independence: a second iterator restarts from F(0).
        Iterator<BigInteger> g1 = new FibIterator();
        Iterator<BigInteger> g2 = new FibIterator();
        g1.next(); g1.next(); g1.next();           // consume F(0), F(1), F(1) from g1
        BigInteger fromG2 = g2.next();              // g2 should still produce F(0)
        boolean ok = fromG2.equals(BigInteger.ZERO);
        System.out.println((ok ? "OK   " : "FAIL ") + "independent iterators: g2.first=" + fromG2);

        // Spot-check known large values: F(50) = 12586269025, F(92) = 7540113804746346429.
        ok = take(51).get(50).equals(new BigInteger("12586269025"));
        System.out.println((ok ? "OK   " : "FAIL ") + "F(50) == 12586269025");
        ok = take(93).get(92).equals(new BigInteger("7540113804746346429"));
        System.out.println((ok ? "OK   " : "FAIL ") + "F(92) == 7540113804746346429 (last value that fits in long)");

        // Closed-form identity check (Cassini): F(n-1)*F(n+1) - F(n)^2 = (-1)^n.
        // Holds for all n >= 1.  Sample n = 100.
        List<BigInteger> fibs = take(102);
        BigInteger fnm1 = fibs.get(99), fn = fibs.get(100), fnp1 = fibs.get(101);
        BigInteger lhs = fnm1.multiply(fnp1).subtract(fn.multiply(fn));
        BigInteger rhs = BigInteger.ONE;        // (-1)^100
        System.out.println((lhs.equals(rhs) ? "OK   " : "FAIL ")
                + "Cassini at n=100: F(99)*F(101) - F(100)^2 = " + lhs + " (expected " + rhs + ")");

        // Performance: take 1e5 values.
        int N = 100_000;
        long t0 = System.nanoTime();
        List<BigInteger> big = take(N);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("take(" + N + ") in " + ms + " ms; F(" + N + ") has "
                + big.get(N - 1).toString().length() + " digits");

        // Reject negative n.
        try {
            take(-1);
            System.out.println("FAIL  expected exception for take(-1)");
        } catch (IllegalArgumentException ex) {
            System.out.println("OK    take(-1) threw " + ex.getMessage());
        }
    }

    private static void check(int n, String expected) {
        List<BigInteger> values = take(n);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(' ');
            sb.append(values.get(i).toString());
        }
        String got = sb.toString();
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + "take(" + n + ") = \"" + got + "\""
                + (ok ? "" : "  (expected \"" + expected + "\")"));
    }
}
