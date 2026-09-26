package reddit.New2026;

import java.util.*;

public class Solution {
    public static void main(String[] args) {
        List<String> cols = Arrays.asList("ad_delivery_pennies", "payment_pennies");

        List<Transaction> txs = Arrays.asList(
                new Transaction("id1", "userId1", Map.of("ad_delivery_pennies", 1l, "payment_pennies", 1l)),
                new Transaction("id2", "userId1", Map.of("ad_delivery_pennies", 11l, "payment_pennies", 11l)),
                new Transaction("id2", "userId2", Map.of("ad_delivery_pennies", 10l, "payment_pennies", 10l)),
                new Transaction("id2", "userId2", Map.of("ad_delivery_pennies", 120l, "payment_pennies", 120l), true),
                new Transaction("id2", "userId2", Map.of("ad_delivery_pennies", 120l, "payment_pennies", 120l), false, false, true),
                new Transaction("id2", "userId2", Map.of("ad_delivery_pennies", 120l, "payment_pennies", 120l), true),
                new Transaction("id2", "userId2", Map.of("ad_delivery_pennies", 120l, "payment_pennies", 120l), false, true, false)

        );

        System.out.println(process(cols, txs));
    }

    public static class Transaction {
        String id, userId;
        Map<String, Long> amounts;
        boolean overwrite = false;
        boolean redo = false;
        boolean undo = false;

        public Transaction(String id, String userId, Map<String, Long> amounts ) {
            this.userId = userId;
            this.id = id;
            this.amounts = amounts;
        }

        public Transaction(String id, String userId, Map<String, Long> amounts, boolean overwrite ) {
            this.userId = userId;
            this.id = id;
            this.amounts = amounts;
            this.overwrite = overwrite;
        }

        public Transaction(String id, String userId, Map<String, Long> amounts, boolean overwrite , boolean redo, boolean undo ) {
            this.userId = userId;
            this.id = id;
            this.amounts = amounts;
            this.overwrite = overwrite;
            this.redo = redo;
            this.undo = undo;
        }

        public String toString() {
            return "id " + id + " userId " + userId + " amounts " + amounts.toString();
        }
    }
    public static class History {
        Transaction tx;
        Map<String, Long> values;
        public History(Transaction tx, Map<String, Long> values) {
            this.tx = tx;
            this.values = values;
        }
    }
    public static class BillStatus {
        Map<String, Long> values = new HashMap<>();
        Stack<Transaction> redo = new Stack<>();
        Stack<History> undo = new Stack<>();

        public BillStatus(List<String> cols) {
            for(String col : cols) {
                values.put(col, 0l);
            }
        }

        public void apply(Transaction tx) {
            if (tx.undo) {
                if (!undo.isEmpty()) {
                    History history = undo.pop();
                    values.clear();
                    values.putAll(history.values);
                    redo.add(history.tx);
                }
            } else if(tx.redo) {
                if (!redo.isEmpty()) {
                    Transaction newTx = redo.pop();
                    applyRegular(newTx);
                }
            } else {
                applyRegular(tx);
                redo.clear();;
            }
        }

        public void applyRegular(Transaction tx) {
            Map<String, Long> snap = new HashMap<>(values);
            for(Map.Entry<String, Long> entry : tx.amounts.entrySet()) {
                if (values.containsKey(entry.getKey())) {
                    if (tx.overwrite) {
                        values.put(entry.getKey(), entry.getValue());
                    } else {
                        values.merge(entry.getKey(), entry.getValue(), Long::sum);
                    }
                }
            }

            undo.add(new History(tx, snap));
        }

        public String toString() {
            return "values " + values.toString();
        }
    }

    public static Map<String, BillStatus> process(List<String> cols, List<Transaction> txs) {
        Map<String, BillStatus> result = new HashMap<>();

        for(Transaction tx : txs) {
            String userId = tx.userId;
            BillStatus status = result.computeIfAbsent(userId, k -> new BillStatus(cols));
            status.apply(tx);
        }

        return result;
    }
}
