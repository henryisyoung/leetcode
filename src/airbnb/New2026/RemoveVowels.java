package airbnb.New2026;
/*
Remove Vowels From a String.

Given a string, remove every vowel (a, e, i, o, u — both cases) and
return the result.

I/O
  Input : s (String)
  Output: String with no vowels

Constraints
  0 <= s.length() <= 1000
  s may contain ASCII letters, digits, whitespace, punctuation.
  Unicode characters are passed through unchanged (only ASCII vowels
  are stripped — spec doesn't mention diacritics like 'á').

Examples
  "Hello World"                      -> "Hll Wrld"
  "beautiful"                        -> "btfl"
  "AEIOU are vowels"                 -> " r vwls"
  "ABCDE...XYZabcde...xyz"           -> consonants only
  ""                                 -> ""
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
Implementation notes

  * A 128-entry boolean table indexed by ASCII char gives an O(1)
    "is-vowel" check without the cost of String.indexOf("aeiouAEIOU", c)
    on every character.

  * One pass, single StringBuilder sized to s.length() — for n <= 1000
    this is trivially fast; the table makes it scale cleanly to much
    larger inputs too.

  * Characters outside 0..127 are not vowels by definition, so we
    can skip the bounds check and just default to false.

Complexity
  Time:   O(n)
  Memory: O(n) for the output (input untouched).
*/
public class RemoveVowels {

    private static final boolean[] IS_VOWEL = new boolean[128];
    static {
        for (char c : "aeiouAEIOU".toCharArray()) IS_VOWEL[c] = true;
    }

    public String removeVowels(String s) {
        if (s == null || s.isEmpty()) return s == null ? "" : s;

        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 128 && IS_VOWEL[c]) continue;
            out.append(c);
        }
        return out.length() == s.length() ? s : out.toString();
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

    /** Stdin: one string per line. Prints the vowel-stripped result. */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        RemoveVowels solver = new RemoveVowels();
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(solver.removeVowels(line));
        }
    }

    private static void runDemos() {
        RemoveVowels solver = new RemoveVowels();

        check("ex1", solver.removeVowels("Hello World"),         "Hll Wrld");
        check("ex2", solver.removeVowels("beautiful"),           "btfl");
        check("ex3", solver.removeVowels("AEIOU are vowels"),    " r vwls");
        check("ex4", solver.removeVowels(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
                "BCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz");
        check("ex5 empty", solver.removeVowels(""), "");

        // ---- Edge cases ----
        check("null -> empty",    solver.removeVowels(null),                  "");
        check("no vowels at all", solver.removeVowels("rhythm BCD 123"),      "rhythm BCD 123");
        check("only vowels",      solver.removeVowels("aeiouAEIOU"),          "");
        check("y is NOT a vowel", solver.removeVowels("YyAa"),                "Yy");
        check("punctuation kept", solver.removeVowels("hi, world!"),          "h, wrld!");
        check("unicode passthru", solver.removeVowels("café déjà vu"),        "cfé déjà v");
        //   ^ Only ASCII a/e/u get stripped; é and à are left alone.

        // ---- Stress: 1000 chars ----
        StringBuilder big = new StringBuilder(1000);
        for (int i = 0; i < 1000; i++) big.append("aeiouAEIOUxyz".charAt(i % 13));
        long t0 = System.nanoTime();
        String r = solver.removeVowels(big.toString());
        long us = (System.nanoTime() - t0) / 1_000;
        System.out.println("Stress n=1000: out=" + r.length() + " in " + us + " us");
    }

    private static void check(String label, String got, String expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label);
        if (!ok) {
            System.out.println("  expected: \"" + expected + "\"");
            System.out.println("  got     : \"" + got + "\"");
        }
    }
}
