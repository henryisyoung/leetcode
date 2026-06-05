package airbnb.New2026;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Refund Allocator (Airbnb) — simple interview version.

Given past payments and a refund amount, decide which payment(s) to refund
and how much from each.

Rules
  1. Fully refund one payment before moving to the next.
  2. Method priority: CREDIT > CREDIT_CARD > PAYPAL.
  3. Within the same method, refund the MOST RECENT payment first.

Simplifications for the interview
  - amount = the remaining refundable amount of that payment (if a payment
    was already partially refunded, the caller passes what's left).
  - money is whole dollars (int); dates are ISO strings "YYYY-MM-DD", which
    sort lexicographically == chronologically.
  - output is a list of human-readable strings.

Greedy: sort by (priority asc, date desc), then take from each in turn.
O(n log n).
*/
public class RefundAllocator {

    static class Payment {
        String id;
        String method;     // CREDIT | CREDIT_CARD | PAYPAL
        String date;       // "2023-01-10"
        int amount;        // remaining refundable dollars

        Payment(String id, String method, String date, int amount) {
            this.id = id;
            this.method = method;
            this.date = date;
            this.amount = amount;
        }
    }

    // Lower index = refunded first.
    static final List<String> PRIORITY = Arrays.asList("CREDIT", "CREDIT_CARD", "PAYPAL");

    static List<String> allocate(List<Payment> payments, int refund) {
        List<String> result = new ArrayList<>();
        if (payments == null || refund <= 0) return result;

        payments.sort((a, b) -> {
            int pa = PRIORITY.indexOf(a.method);
            int pb = PRIORITY.indexOf(b.method);
            if (pa != pb) return pa - pb;          // higher-priority method first
            return b.date.compareTo(a.date);       // most recent first
        });

        for (Payment p : payments) {
            if (refund == 0) break;
            int take = Math.min(refund, p.amount);
            if (take <= 0) continue;
            result.add(p.id + ", " + p.method + ", $" + take);
            refund -= take;
        }
        return result;
    }

    /* --------------------------- demo --------------------------- */

    public static void main(String[] args) {
        // Example 1: Credit $40, Paypal $60, refund $50 -> P1 $40, P2 $10.
        print("example 1", allocate(new ArrayList<>(List.of(
                new Payment("P1", "CREDIT", "2023-01-10", 40),
                new Payment("P2", "PAYPAL", "2023-01-15", 60))), 50));

        // Example 2: Credit P1 $20 left, Paypal P2 $60 (old), Paypal P3 $40 (new),
        // refund $50 -> P1 $20, then newer Paypal P3 $30.
        print("example 2", allocate(new ArrayList<>(List.of(
                new Payment("P1", "CREDIT", "2023-01-15", 20),
                new Payment("P2", "PAYPAL", "2023-01-10", 60),
                new Payment("P3", "PAYPAL", "2023-01-20", 40))), 50));

        // Method priority beats date.
        print("priority beats date", allocate(new ArrayList<>(List.of(
                new Payment("P1", "PAYPAL", "2024-12-31", 100),
                new Payment("P2", "CREDIT_CARD", "2024-01-01", 30),
                new Payment("P3", "CREDIT", "2024-06-15", 20))), 60));

        // Same method, newer wins.
        print("newer wins", allocate(new ArrayList<>(List.of(
                new Payment("Old", "CREDIT", "2024-01-01", 30),
                new Payment("New", "CREDIT", "2024-12-01", 30))), 25));

        // Partial when over-requested.
        print("partial", allocate(new ArrayList<>(List.of(
                new Payment("P1", "PAYPAL", "2024-01-01", 10))), 100));
    }

    private static void print(String label, List<String> rows) {
        System.out.println(label + " -> " + rows);
    }
}
