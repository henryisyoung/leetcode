package airbnb.New2026;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
================================================================================
  Refund Allocator (Airbnb)
================================================================================

  Given a list of past payments and a refund amount, decide which payment(s)
  to refund and how much from each.

  Rules
    1. Refund a payment IN FULL before moving to the next one.
       (We never split a refund across payments unless a payment is exhausted.)
    2. Method priority: CREDIT  >  CREDIT_CARD  >  PAYPAL
    3. Within the same method, prefer the MOST RECENT payment (latest date).
    4. A payment may already have prior refunds against it; only the remaining
       capacity (amount − already_refunded) is available.

  Algorithm  (O(n log n))
    sortKey(p) = (p.method.priority ASC, p.date DESC)
    remaining = refundAmount
    for p in sorted(payments):
      take = min(remaining, p.capacity())
      if take > 0: emit Allocation(p, take); remaining -= take
      if remaining == 0: break

  Returns the list of Allocation rows. If `remaining > 0` at the end, the
  request couldn't be fully refunded — we return the partial allocation; a
  caller may decide whether to throw, queue a follow-up, or accept partial.

  ⚠ NOTE on the prompt's "expected" outputs
  ────────────────────────────────────────────────────────────────────────
  The prompt's two expected outputs don't fit any single set of consistent
  rules I can write down. Concretely:

  Example 1  (Credit $40, Paypal $60, refund $50)
    Prompt expects:    Payment1 Credit $30
    Natural rules give: Payment1 Credit $40, Payment2 Paypal $10  (=$50)
    The "$30" is unreachable under any priority order — Payment1 has $40
    of available capacity, so a single-line "$30" can only fully account
    for a $30 refund (not $50). Looks like a typo of "$40" plus a missing
    second line "Payment2 Paypal $10".

  Example 2  (Credit Pay1 $40 [−$20], Paypal Pay2 $60, Paypal Pay3 $40, refund $50)
    Prompt expects:    Payment1 Credit $40, Payment2 Paypal $10
    Natural rules give: Payment1 Credit $20, Payment3 Paypal $30
    The "$40 from Payment1" exceeds Payment1's $20 remaining capacity;
    "$10 from Payment2 (oldest Paypal)" contradicts rule 3 (most recent
    first). Both look like typos.

  I implement the natural reading of the four rules above and document the
  discrepancy in tests. In an interview I would surface this immediately
  with a counterexample and ask for clarification before coding.
================================================================================
*/
public class RefundAllocator {

    /** Lower priority number = refunded first. */
    public enum PaymentMethod {
        CREDIT(0),
        CREDIT_CARD(1),
        PAYPAL(2);

        final int priority;
        PaymentMethod(int p) { this.priority = p; }

        static PaymentMethod parse(String s) {
            return PaymentMethod.valueOf(s.trim().toUpperCase().replace(' ', '_'));
        }
    }

    /** A single past payment. `alreadyRefundedCents` is mutable so callers can
     *  encode prior refunds before passing in. */
    public static final class Payment {
        public final String id;
        public final PaymentMethod method;
        public final LocalDate date;
        public final long amountCents;
        public long alreadyRefundedCents;

        public Payment(String id, PaymentMethod method, LocalDate date, long amountCents) {
            this(id, method, date, amountCents, 0L);
        }
        public Payment(String id, PaymentMethod method, LocalDate date,
                       long amountCents, long alreadyRefundedCents) {
            this.id = id;
            this.method = method;
            this.date = date;
            this.amountCents = amountCents;
            this.alreadyRefundedCents = alreadyRefundedCents;
        }
        public long capacityCents() {
            return Math.max(0, amountCents - alreadyRefundedCents);
        }
    }

    /** One refund row, attributable to exactly one payment. */
    public static final class Allocation {
        public final String paymentId;
        public final PaymentMethod method;
        public final long amountCents;

        public Allocation(String paymentId, PaymentMethod method, long amountCents) {
            this.paymentId = paymentId;
            this.method = method;
            this.amountCents = amountCents;
        }
        @Override public String toString() {
            return paymentId + " (" + method + "): " + formatMoney(amountCents);
        }
    }

    public static List<Allocation> allocate(List<Payment> payments, long refundCents) {
        List<Allocation> out = new ArrayList<>();
        if (refundCents <= 0 || payments == null || payments.isEmpty()) return out;

        List<Payment> sorted = new ArrayList<>(payments);
        sorted.sort(Comparator
                .comparingInt((Payment p) -> p.method.priority)
                .thenComparing(p -> p.date, Comparator.reverseOrder()));

        long remaining = refundCents;
        for (Payment p : sorted) {
            if (remaining == 0) break;
            long take = Math.min(remaining, p.capacityCents());
            if (take <= 0) continue;
            out.add(new Allocation(p.id, p.method, take));
            remaining -= take;
        }
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
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    /**
     * Spec input on one line:  type,date,amount|type,date,amount|...|refundAmount
     * Amounts are whole dollars in the spec; we multiply by 100 internally.
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine().trim();
        String[] tokens = line.split("\\|");
        List<Payment> payments = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            String[] parts = tokens[i].split(",");
            payments.add(new Payment("P" + (i + 1),
                    PaymentMethod.parse(parts[0]),
                    LocalDate.parse(parts[1].trim()),
                    Long.parseLong(parts[2].trim()) * 100));
        }
        long refundCents = Long.parseLong(tokens[tokens.length - 1].trim()) * 100;
        for (Allocation a : allocate(payments, refundCents)) System.out.println(a);
    }

    /* --------------------------- Demos --------------------------- */

    private static void runDemos() {
        // Spec Example 1: Credit $40 (2023-01-10), Paypal $60 (2023-01-15), refund $50
        // Prompt's expected:  Payment1 Credit $30   (inconsistent — see header note)
        // Natural rules give: Payment1 Credit $40, Payment2 Paypal $10
        check("example 1 (natural rules)",
              List.of(p("P1", PaymentMethod.CREDIT,  "2023-01-10", 40_00),
                      p("P2", PaymentMethod.PAYPAL,  "2023-01-15", 60_00)),
              50_00,
              List.of("P1 (CREDIT): $40", "P2 (PAYPAL): $10"));

        // Spec Example 2: Credit P1 $40 (refunded $20), Paypal P2 $60 oldest,
        //                 Paypal P3 $40 newest, refund $50
        // Prompt's expected:  P1 $40, P2 $10   (inconsistent — see header note)
        // Natural rules give: P1 $20, P3 $30   (P1 only has $20 capacity left;
        //                                       P3 wins over P2 as the newer Paypal)
        check("example 2 (natural rules)",
              List.of(p("P1", PaymentMethod.CREDIT, "2023-01-15", 40_00, 20_00),
                      p("P2", PaymentMethod.PAYPAL, "2023-01-10", 60_00),
                      p("P3", PaymentMethod.PAYPAL, "2023-01-20", 40_00)),
              50_00,
              List.of("P1 (CREDIT): $20", "P3 (PAYPAL): $30"));

        // Exact fit on one payment.
        check("exact fit one payment",
              List.of(p("P1", PaymentMethod.CREDIT, "2024-01-01", 50_00)),
              50_00,
              List.of("P1 (CREDIT): $50"));

        // Method priority: PAYPAL is newest but CREDIT wins.
        check("method priority beats date",
              List.of(p("P1", PaymentMethod.PAYPAL,      "2024-12-31", 100_00),
                      p("P2", PaymentMethod.CREDIT_CARD, "2024-01-01",  30_00),
                      p("P3", PaymentMethod.CREDIT,      "2024-06-15",  20_00)),
              60_00,
              List.of("P3 (CREDIT): $20",
                      "P2 (CREDIT_CARD): $30",
                      "P1 (PAYPAL): $10"));

        // Same method, date is the tiebreaker (newer wins).
        check("same method, newer wins",
              List.of(p("Old", PaymentMethod.CREDIT, "2024-01-01", 30_00),
                      p("New", PaymentMethod.CREDIT, "2024-12-01", 30_00)),
              25_00,
              List.of("New (CREDIT): $25"));

        // Skip a fully-refunded payment.
        check("skip fully refunded",
              List.of(p("P1", PaymentMethod.CREDIT, "2024-01-01", 50_00, 50_00),
                      p("P2", PaymentMethod.CREDIT, "2024-02-01", 30_00)),
              20_00,
              List.of("P2 (CREDIT): $20"));

        // Partial fulfillment when refund exceeds total available.
        check("partial when over-requested",
              List.of(p("P1", PaymentMethod.PAYPAL, "2024-01-01", 10_00)),
              100_00,
              List.of("P1 (PAYPAL): $10"));

        // Zero refund → empty.
        check("zero refund",
              List.of(p("P1", PaymentMethod.CREDIT, "2024-01-01", 50_00)),
              0,
              List.of());

        // No payments → empty.
        check("no payments", List.of(), 50_00, List.of());

        // Cents work too.
        check("cents",
              List.of(p("P1", PaymentMethod.CREDIT, "2024-01-01", 12_34)),
              5_67,
              List.of("P1 (CREDIT): $5.67"));
    }

    /* --------------------------- helpers --------------------------- */

    private static Payment p(String id, PaymentMethod m, String date, long amount) {
        return new Payment(id, m, LocalDate.parse(date), amount);
    }
    private static Payment p(String id, PaymentMethod m, String date, long amount, long refunded) {
        return new Payment(id, m, LocalDate.parse(date), amount, refunded);
    }

    private static String formatMoney(long cents) {
        long dollars = cents / 100, c = cents % 100;
        return c == 0 ? "$" + dollars : String.format("$%d.%02d", dollars, c);
    }

    private static void check(String label,
                              List<Payment> payments, long refundCents,
                              List<String> expected) {
        List<Allocation> got = allocate(payments, refundCents);
        List<String> gotStrs = new ArrayList<>();
        for (Allocation a : got) gotStrs.add(a.toString());
        boolean ok = gotStrs.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + " refund=" + formatMoney(refundCents)
                + " expected=" + expected + " got=" + gotStrs);
    }
}
