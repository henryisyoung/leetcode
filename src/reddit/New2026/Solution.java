package reddit.New2026;

import java.util.*;

public class Solution {
    public static void main(String[] args) {

    }


    private static class  Transaction {
        private String id, userId;
        private Map<String, Long> amounts;
        private long time;
        private boolean overwrite = false;
        private boolean redo = false;
        private boolean undo = false;

        public Transaction(String id, String userid, Map<String, Long> amounts, long time) {
            this.id = id;
            this.amounts = amounts;
            this.time = time;
            this.userId = userid;
        }

        public Transaction(String id, String userid, Map<String, Long> amounts, long time, boolean overwrite) {
            this.id = id;
            this.amounts = amounts;
            this.time = time;
            this.userId = userid;
            this.overwrite = overwrite;
        }

        public Transaction(String id, String userid, Map<String, Long> amounts, long time, boolean undo, boolean redo) {
            this.id = id;
            this.amounts = amounts;
            this.time = time;
            this.userId = userid;
            this.redo = redo;
            this.undo = undo;
        }

    }

    public static class BillStatus {
        private static class  History {
            private Map<String, Long> snapValues;
            private Transaction tx;

            public History(Map<String, Long> values, Transaction tx) {
                this.snapValues = values;
                this.tx = tx;
            }

        }

        private Map<String, Long> values = new HashMap<>();
        private Stack<Transaction> redoStack = new Stack<>();
        private Stack<History> undoStack = new Stack<>();

        public BillStatus(List<String> cols) {
            for(String col : cols) {
                values.put(col, 0l);
            }
        }

        public void apply(Transaction tx) {
            if (tx.undo) {
                if (!undoStack.isEmpty()) {
                    History history = undoStack.pop();
                    values.clear();
                    values.putAll(history.snapValues);
                    redoStack.add(history.tx);
                }
            } else if (tx.redo) {
                if (!redoStack.isEmpty()) {
                    Transaction newTX = redoStack.pop();
                    applyRegular(newTX);
                }
            } else {
                applyRegular(tx);
                redoStack.clear();
            }
        }

        public void applyRegular(Transaction tx) {
            Map<String, Long> snapValues = new HashMap<>(values);

            for(Map.Entry<String, Long> entry : tx.amounts.entrySet()) {
                if (values.containsKey(entry.getKey())) {
                    if (tx.overwrite) {
                        values.put(entry.getKey(), entry.getValue());
                    } else {
                        values.merge(entry.getKey(), entry.getValue(), Long::sum);
                    }
                }
            }

            undoStack.add(new History(snapValues, tx));
        }
    }

    public Map<String, BillStatus> process(List<String> cols, List<Transaction> txs) {
        Map<String, BillStatus> map = new HashMap<>();
        if (cols == null || txs == null) {
            return map;
        }

        List<Transaction> ordered = new ArrayList<>(txs);
        ordered.sort(Comparator.comparingLong(t -> t.time));

        for(Transaction tx : ordered) {
            BillStatus status = map.computeIfAbsent(tx.userId, k -> new BillStatus(cols));
            status.apply(tx);
        }
        return map;
    }

}
