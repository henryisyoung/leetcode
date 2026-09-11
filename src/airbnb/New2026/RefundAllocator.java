package airbnb.New2026;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Refund Allocator (Airbnb).

Given ordered payment history, prior refunds against those payments, and a new
refund amount, produce the payment-level allocation for the new refund.

Rules
  1. Fully refund one payment before moving to the next.
  2. Method priority: CREDIT > CREDIT_CARD > PAYPAL.
  3. Within the same method, refund the MOST RECENT payment first.
  4. Prior refunds reduce each payment's remaining refundable amount first.

Greedy: sort by (priority asc, date desc), then take from each in turn.
O(n log n).
*/
public class RefundAllocator {

    enum Method {
        CREDIT(0),
        CREDIT_CARD(1),
        PAYPAL(2);

        private final int priority;

        Method(int priority) {
            this.priority = priority;
        }
    }

    static class Payment {
        int paymentId;
        Method method;
        LocalDate date;
        int amount;

        Payment(int paymentId, Method method, String date, int amount) {
            this.paymentId = paymentId;
            this.method = method;
            this.date = LocalDate.parse(date);
            this.amount = amount;
        }
    }

    static class PriorRefund {
        String refundId;
        int paymentId;
        int amount;

        PriorRefund(String refundId, int paymentId, int amount) {
            this.refundId = refundId;
            this.paymentId = paymentId;
            this.amount = amount;
        }
    }

    static class Allocation {
        String refundLabel;
        int paymentId;
        Method method;
        int amount;

        Allocation(String refundLabel, int paymentId, Method method, int amount) {
            this.refundLabel = refundLabel;
            this.paymentId = paymentId;
            this.method = method;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "(" + refundLabel + ", payment_id=" + paymentId + ", " + method + ", " + amount + ")";
        }
    }

    static List<Allocation> allocate(List<Payment> payments,
                                     List<PriorRefund> priorRefunds,
                                     int refundAmount) {
        List<Allocation> result = new ArrayList<>();
        if (payments == null || refundAmount <= 0) return result;

        Map<Integer, Integer> remainingByPayment = new HashMap<>();
        for (Payment payment : payments) {
            if (payment == null || payment.amount <= 0) continue;
            remainingByPayment.put(payment.paymentId,
                    remainingByPayment.getOrDefault(payment.paymentId, 0) + payment.amount);
        }

        if (priorRefunds != null) {
            for (PriorRefund priorRefund : priorRefunds) {
                if (priorRefund == null || priorRefund.amount <= 0) continue;
                int remaining = remainingByPayment.getOrDefault(priorRefund.paymentId, 0);
                remainingByPayment.put(priorRefund.paymentId, Math.max(0, remaining - priorRefund.amount));
            }
        }

        List<Payment> ordered = new ArrayList<>(payments);
        ordered.sort((a, b) -> {
            if (a == b) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            if (a.method.priority != b.method.priority) {
                return a.method.priority - b.method.priority;
            }
            return b.date.compareTo(a.date);
        });

        int labelIndex = 0;
        for (Payment payment : ordered) {
            if (refundAmount == 0) break;
            if (payment == null) continue;

            int remaining = remainingByPayment.getOrDefault(payment.paymentId, 0);
            int take = Math.min(refundAmount, remaining);
            if (take <= 0) continue;

            result.add(new Allocation(label(labelIndex++), payment.paymentId, payment.method, take));
            refundAmount -= take;
            remainingByPayment.put(payment.paymentId, remaining - take);
        }
        return result;
    }

    private static String label(int index) {
        return String.valueOf((char) ('a' + index));
    }

    /* --------------------------- demo --------------------------- */

    public static void main(String[] args) {
        List<Payment> payments = List.of(
                new Payment(1, Method.CREDIT, "2023-01-15", 40),
                new Payment(2, Method.PAYPAL, "2023-01-10", 60),
                new Payment(3, Method.PAYPAL, "2023-01-20", 40)
        );
        List<PriorRefund> priorRefunds = List.of(
                new PriorRefund("R1", 1, 20)
        );
        check("prompt example",
                allocate(payments, priorRefunds, 50).toString(),
                "[(a, payment_id=1, CREDIT, 20), (b, payment_id=3, PAYPAL, 30)]");

        check("method priority beats date",
                allocate(List.of(
                        new Payment(1, Method.PAYPAL, "2024-12-31", 100),
                        new Payment(2, Method.CREDIT_CARD, "2024-01-01", 30),
                        new Payment(3, Method.CREDIT, "2024-06-15", 20)
                ), List.of(), 60).toString(),
                "[(a, payment_id=3, CREDIT, 20), (b, payment_id=2, CREDIT_CARD, 30), (c, payment_id=1, PAYPAL, 10)]");

        check("same method newer first",
                allocate(List.of(
                        new Payment(1, Method.CREDIT, "2024-01-01", 30),
                        new Payment(2, Method.CREDIT, "2024-12-01", 30)
                ), List.of(), 25).toString(),
                "[(a, payment_id=2, CREDIT, 25)]");

        check("fully drained prior refund skipped",
                allocate(List.of(
                        new Payment(1, Method.CREDIT, "2024-01-01", 10),
                        new Payment(2, Method.PAYPAL, "2024-01-02", 50)
                ), List.of(new PriorRefund("R1", 1, 10)), 20).toString(),
                "[(a, payment_id=2, PAYPAL, 20)]");

        check("short fill when over requested",
                allocate(List.of(
                        new Payment(1, Method.PAYPAL, "2024-01-01", 10)
                ), List.of(), 100).toString(),
                "[(a, payment_id=1, PAYPAL, 10)]");

        System.out.println("All tests passed.");
    }

    private static void check(String label, String actual, String expected) {
        if (!actual.equals(expected)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
        System.out.println(label + ": " + actual);
    }
}
