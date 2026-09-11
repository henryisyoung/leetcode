package reddit.New2026;

import java.util.*;

/*
================================================================================
  BillingStatusRecovery — Reddit onsite (2026), 3-part progression
================================================================================

  A DB with per-user billing state was dropped. Rebuild each user's
  BillingStatus by replaying the transaction log. Classic event sourcing:
  fold a stream of transactions into per-user state.

  --------------------------------------------------------------------------
  HOW TO READ THIS FILE
  --------------------------------------------------------------------------
    Each part gets its OWN self-contained class, holding only what that part
    needs. Don't start from the final answer — the interview signal is in
    noticing WHAT FORCES each upgrade.

      Q1Aggregate   Map<col, Long>                   sum everything
      Q2Overwrite   + per-column replace mode        order now matters
      Q3UndoRedo    + snapshot history / redo stack  overwrite isn't invertible

    `Transaction` is shared by all three: it's the GIVEN log schema, not part
    of the answer. Q1 simply never reads the option flags.

    The apply() bodies are deliberately near-identical so the diff between
    parts is obvious when you read them back to back.

  --------------------------------------------------------------------------
  Q1  Aggregate monetary columns per user           → class Q1Aggregate
  --------------------------------------------------------------------------
    Each transaction contributes zero-or-more monetary columns; add them to
    the user's BillingStatus. BillingStatus is created LAZILY on first sight
    of a user_id (you don't know the user set up front).

    Data:  Map<Integer, BillingStatus>, each holding Map<col, Long>

    ⚠ The ordering trap. Transactions arrive as a map keyed by uuid, so they
      are UNORDERED. Sort by `transaction_timestamp` before applying. In Q1
      you can skip the sort and still pass, because addition commutes — which
      is exactly why it's a trap. Q2 makes order load-bearing and an unsorted
      replay then produces silently wrong numbers. Sort from the start and say
      why.

  --------------------------------------------------------------------------
  Q2  Overwrite transactions                        → class Q2Overwrite
  --------------------------------------------------------------------------
    If a transaction sets `overwrite = true`, each monetary column REPLACES
    the current value instead of adding to it.

    ⚠ THE DISCRIMINATING DETAIL: only columns PRESENT in that transaction are
      replaced. Columns absent from it are left ALONE, not zeroed. So
      `overwrite` is a per-column write MODE, not a row-level reset.

      The spec's user-2 trace is what pins this down:
          i: ad += 1000             → ad 1000
          j: ad += 1000             → ad 2000
          e: ad += 1000, pay += 500 → ad 3000, pay 500
          b: ad += 1000             → ad 4000
          f: pay = 2000 (overwrite) → ad 4000, pay 2000   ← ad SURVIVES
          c: pay += 600             → ad 4000, pay 2600
      Transaction f is an overwrite carrying only payment_pennies. Implement
      it as "reset the row, then write what's present" and ad collapses
      4000 → 0.

    Diff from Q1: one branch inside the per-column loop. Nothing else.

    ⚠ WHICH COLLECTION DO YOU ITERATE? Both work, and the results are
      identical for well-formed input, but they cost differently:
        • iterate the SCHEMA and skip nulls  → O(c) per tx, can only ever
          write declared columns
        • iterate tx.amounts with a schema    → O(k) per tx, where k = columns
          membership guard                      this tx actually carries
      k is 1–2 while c can be large and the log is sparse, so the code below
      iterates the tx. Measured at 300k txs with k=2: c=2 is a wash, c=20 is
      2.4x, c=200 is 14x. The guard is what keeps the schema-bounded
      guarantee you'd otherwise get for free.

  --------------------------------------------------------------------------
  Q3  undo_last / redo_last                         → class Q3UndoRedo
  --------------------------------------------------------------------------
    Transactions can now carry `undo_last` or `redo_last`.

    First, define your terms — the code can't be written until you do:
      "REGULAR" transaction = no options at all, OR only `overwrite`.
      undo/redo transactions are NOT regular, so they never enter the history.
      (If they did, undoing an undo becomes ambiguous and the stack eats
      itself.)

    ⚠ WHAT FORCES A HISTORY: you might hope to invert each operation instead
      of storing state. Summing IS invertible — undo `+= v` by `-= v`. But
      OVERWRITE IS NOT: `values.put(col, v)` destroys the previous value, so
      there is nothing to compute an inverse from. Q2 is precisely what makes
      Q3 need recorded state rather than arithmetic.

    Mechanism (per user — each BillingStatus owns its own stacks):
      • every regular tx pushes (tx, snapshot-of-values-BEFORE) onto `history`
      • undo_last → pop history, restore the snapshot wholesale, push the
                    popped TX onto `redoStack`. No-op if history is empty.
      • redo_last → pop redoStack, re-apply as a regular tx (which pushes a
                    fresh history entry). No-op if redoStack is empty.
      • any new regular tx CLEARS redoStack — the standard editor rule that a
        fresh edit invalidates the redo branch.

    Restoring a whole snapshot also means an overwrite tx is undoable for
    free: no need to reason about inverting it.

    Memory: a full snapshot is O(c) per history entry. With c = 2 that's
    nothing. With hundreds of sparse columns you'd store an INVERSE DELTA
    (only the columns the tx actually touched), dropping O(c) → O(k). Name
    the trade-off; don't build it prematurely.

  --------------------------------------------------------------------------
  Complexity   (n = total txs, u = distinct users, c = # monetary cols,
                k = columns touched by one tx, H = ops applied to one user)
  --------------------------------------------------------------------------
    Q1 / Q2 process()
      sort by timestamp        O(n log n)
      seed each new status     O(u · c)   once per distinct user, not per tx
      apply each tx            O(n · k)
      total                    O(n log n + n · k + u · c)
      space                    O(u · c) statuses

    Q1Aggregate.apply          O(k)   one pass over the tx's own columns
    Q2Overwrite.apply          O(k)   same pass, one extra branch
    Q3UndoRedo.apply
      regular / overwrite      O(c + k)  full snapshot copy, then k writes
      undo_last                O(c)      restore snapshot + push to redoStack
      redo_last                O(c + k)  pop and re-apply as regular
      extra space              O(H · c) history + redo  (→ O(H · k) with deltas)

    Q3's O(c) floor is the SNAPSHOT, not the write loop — switching to
    inverse deltas is what would take the whole method to O(k).
================================================================================
*/
public class BillingStatusRecovery {

    private BillingStatusRecovery() {}   // namespace only — each part has its own class

    /* ============================================================
       Transaction — the GIVEN log schema, shared by all three parts.
       Q1 never reads overwrite/undoLast/redoLast.
       ============================================================ */
    public static final class Transaction {
        public final String  txId;
        public final int     userId;
        public final long    timestamp;
        public final boolean overwrite;
        public final boolean undoLast;
        public final boolean redoLast;
        public final Map<String, Long> amounts;      // only monetary columns present in the tx

        private Transaction(String txId, int userId, long ts,
                            boolean overwrite, boolean undoLast, boolean redoLast,
                            Map<String, Long> amounts) {
            this.txId = txId; this.userId = userId; this.timestamp = ts;
            this.overwrite = overwrite; this.undoLast = undoLast; this.redoLast = redoLast;
            this.amounts = Collections.unmodifiableMap(new LinkedHashMap<>(amounts));
        }

        /** Test-friendly builder. Trailing varargs are alternating (key, value) pairs.
         *  Time O(#args) — one pass over the vararg pairs. */
        public static Transaction of(String txId, int userId, long timestamp, Object... kv) {
            boolean overwrite = false, undoLast = false, redoLast = false;
            Map<String, Long> amounts = new LinkedHashMap<>();
            for (int i = 0; i < kv.length; i += 2) {
                String k = (String) kv[i];
                Object v = kv[i + 1];
                switch (k) {
                    case "overwrite": overwrite = (Boolean) v; break;
                    case "undo_last": undoLast  = (Boolean) v; break;
                    case "redo_last": redoLast  = (Boolean) v; break;
                    default:          amounts.put(k, ((Number) v).longValue());
                }
            }
            return new Transaction(txId, userId, timestamp, overwrite, undoLast, redoLast, amounts);
        }

        @Override public String toString() {
            return txId + "@" + timestamp + " u" + userId + amounts
                    + (overwrite ? " OVERWRITE" : "") + (undoLast ? " UNDO" : "") + (redoLast ? " REDO" : "");
        }
    }

    /* ============================================================
       Q1 — sum every monetary column, per user
       ============================================================ */
    public static final class Q1Aggregate {

        public static final class BillingStatus {
            private final Map<String, Long> values = new LinkedHashMap<>();

            /** Time O(c) — seed one zero per monetary column. Seeding every declared
             *  column up front lets `values` double as the schema, so apply() needs
             *  no separate column list. */
            public BillingStatus(List<String> monetaryColumns) {
                for (String c : monetaryColumns) values.put(c, 0L);
            }

            /** Time O(k) — one pass over the columns this tx actually carries. */
            public void apply(Transaction tx) {
                for (Map.Entry<String, Long> e : tx.amounts.entrySet()) {
                    String col = e.getKey();
                    if (!values.containsKey(col)) continue;    // not a declared monetary column
                    values.merge(col, e.getValue(), Long::sum);
                }
            }

            /** Time O(c) — copy of the c-entry values map. */
            public Map<String, Long> snapshot() { return new LinkedHashMap<>(values); }

            @Override public String toString() { return "BillingStatus" + values; }
        }

        /** Time O(n log n + n · k + u · c) — sort by timestamp, then per-user apply. */
        public static Map<Integer, BillingStatus> process(List<String> monetaryColumns,
                                                         Collection<Transaction> transactions) {
            List<Transaction> ordered = new ArrayList<>(transactions);
            ordered.sort(Comparator.comparingLong(t -> t.timestamp));

            Map<Integer, BillingStatus> byUser = new LinkedHashMap<>();
            for (Transaction tx : ordered) {
                BillingStatus status = byUser.get(tx.userId);
                if (status == null) {                        // first sighting of this user
                    status = new BillingStatus(monetaryColumns);
                    byUser.put(tx.userId, status);
                }
                status.apply(tx);
            }
            return byUser;
        }
    }

    /* ============================================================
       Q2 — add a per-column overwrite mode.
            Sorting stops being optional here: replace-vs-add makes
            the final value depend on the order of application.
       ============================================================ */
    public static final class Q2Overwrite {

        public static final class BillingStatus {
            private final Map<String, Long> values = new LinkedHashMap<>();

            /** Time O(c) — seed every declared column so `values` doubles as the schema. */
            public BillingStatus(List<String> monetaryColumns) {
                for (String c : monetaryColumns) values.put(c, 0L);
            }

            /** Time O(k) — same pass as Q1 plus the overwrite branch.
             *  ⚠ Visiting only the tx's OWN columns is what makes overwrite
             *  per-column: a column absent from `tx` is never reached, so it is
             *  left untouched rather than zeroed. */
            public void apply(Transaction tx) {
                for (Map.Entry<String, Long> e : tx.amounts.entrySet()) {
                    String col = e.getKey();
                    if (!values.containsKey(col)) continue;    // not a declared monetary column
                    if (tx.overwrite) values.put(col, e.getValue());
                    else              values.merge(col, e.getValue(), Long::sum);
                }
            }

            /** Time O(c). */
            public Map<String, Long> snapshot() { return new LinkedHashMap<>(values); }

            @Override public String toString() { return "BillingStatus" + values; }
        }

        /** Time O(n log n + n · k + u · c). */
        public static Map<Integer, BillingStatus> process(List<String> monetaryColumns,
                                                         Collection<Transaction> transactions) {
            List<Transaction> ordered = new ArrayList<>(transactions);
            ordered.sort(Comparator.comparingLong(t -> t.timestamp));

            Map<Integer, BillingStatus> byUser = new LinkedHashMap<>();
            for (Transaction tx : ordered) {
                BillingStatus status = byUser.get(tx.userId);
                if (status == null) {                        // first sighting of this user
                    status = new BillingStatus(monetaryColumns);
                    byUser.put(tx.userId, status);
                }
                status.apply(tx);
            }
            return byUser;
        }
    }

    /* ============================================================
       Q3 — undo_last / redo_last.
            Overwrite destroys the old value, so undo can't be
            arithmetic — we have to record state.
       ============================================================ */
    public static final class Q3UndoRedo {

        public static final class BillingStatus {
            private final Map<String, Long>   values    = new LinkedHashMap<>();
            private final Deque<HistoryEntry> history   = new ArrayDeque<>();
            private final Deque<Transaction>  redoStack = new ArrayDeque<>();

            private static final class HistoryEntry {
                final Transaction       tx;
                final Map<String, Long> prev;          // full snapshot of `values` BEFORE apply
                HistoryEntry(Transaction tx, Map<String, Long> prev) { this.tx = tx; this.prev = prev; }
            }

            /** Time O(c) — seed every declared column so `values` doubles as the schema. */
            public BillingStatus(List<String> monetaryColumns) {
                for (String c : monetaryColumns) values.put(c, 0L);
            }

            /** Dispatch on the option flags. Undo/redo are O(c) because they copy a
             *  whole snapshot; a regular write is O(c) for the snapshot + O(k) to apply. */
            public void apply(Transaction tx) {
                if (tx.undoLast) {
                    if (!history.isEmpty()) {
                        HistoryEntry e = history.pop();
                        values.clear();
                        values.putAll(e.prev);                 // wholesale restore
                        redoStack.push(e.tx);
                    }
                    return;                                    // no-op if nothing to undo
                }
                if (tx.redoLast) {
                    if (!redoStack.isEmpty()) {
                        applyRegular(redoStack.pop());         // re-apply; keeps rest of redoStack
                    }
                    return;
                }
                applyRegular(tx);
                redoStack.clear();                             // a new edit invalidates redo
            }

            /** Identical to Q2's apply(), plus the snapshot push.
             *  Time O(c) for the snapshot copy + O(k) for the writes. */
            private void applyRegular(Transaction tx) {
                Map<String, Long> snap = new LinkedHashMap<>(values);
                for (Map.Entry<String, Long> e : tx.amounts.entrySet()) {
                    String col = e.getKey();
                    if (!values.containsKey(col)) continue;
                    if (tx.overwrite) values.put(col, e.getValue());
                    else              values.merge(col, e.getValue(), Long::sum);
                }
                history.push(new HistoryEntry(tx, snap));
            }

            /** Time O(c). */
            public Map<String, Long> snapshot() { return new LinkedHashMap<>(values); }

            /** Exposed for tests / debugging. Time O(1). */
            public int historyDepth() { return history.size(); }
            public int redoDepth()    { return redoStack.size(); }

            @Override public String toString() { return "BillingStatus" + values; }
        }

        /** Time O(n log n + n · (c + k)) — Q3's snapshot per regular tx adds the O(c). */
        public static Map<Integer, BillingStatus> process(List<String> monetaryColumns,
                                                         Collection<Transaction> transactions) {
            List<Transaction> ordered = new ArrayList<>(transactions);
            ordered.sort(Comparator.comparingLong(t -> t.timestamp));

            Map<Integer, BillingStatus> byUser = new LinkedHashMap<>();
            for (Transaction tx : ordered) {
                BillingStatus status = byUser.get(tx.userId);
                if (status == null) {                        // first sighting of this user
                    status = new BillingStatus(monetaryColumns);
                    byUser.put(tx.userId, status);
                }
                status.apply(tx);
            }
            return byUser;
        }
    }

    /* ============================================================
       Tests — one block per part
       ============================================================ */
    public static void main(String[] args) {
        List<String> cols = Arrays.asList("ad_delivery_pennies", "payment_pennies");

        /* ---------------- Q1: plain aggregation ---------------- */
        List<Transaction> q1log = Arrays.asList(
                Transaction.of("t1", 1, 1500000001L, "ad_delivery_pennies", 1000),
                Transaction.of("t2", 1, 1500000002L, "ad_delivery_pennies", 1000),
                Transaction.of("t3", 1, 1500000003L, "payment_pennies",     500),
                Transaction.of("t4", 1, 1500000004L, "ad_delivery_pennies", 1000, "payment_pennies", 500)
        );
        Map<Integer, Q1Aggregate.BillingStatus> r1 = Q1Aggregate.process(cols, q1log);
        expect("Q1: ad_delivery summed",  r1.get(1).snapshot().get("ad_delivery_pennies"), 3000L);
        expect("Q1: payment summed",      r1.get(1).snapshot().get("payment_pennies"),     1000L);

        // Lazily created per user, and users stay independent.
        Map<Integer, Q1Aggregate.BillingStatus> r1b = Q1Aggregate.process(cols, Arrays.asList(
                Transaction.of("x", 7, 1L, "payment_pennies", 10),
                Transaction.of("y", 9, 2L, "payment_pennies", 20)
        ));
        expect("Q1: user 7 isolated", r1b.get(7).snapshot().get("payment_pennies"), 10L);
        expect("Q1: user 9 isolated", r1b.get(9).snapshot().get("payment_pennies"), 20L);
        expect("Q1: unseen user absent", r1b.containsKey(3), false);
        expect("Q1: untouched column stays 0",
                r1b.get(7).snapshot().get("ad_delivery_pennies"), 0L);

        /* ---------------- Q2: overwrite ---------------- */
        // Deliberately out of timestamp order in the list, to exercise the sort.
        List<Transaction> q2log = Arrays.asList(
                Transaction.of("a", 1, 1500000001L, "ad_delivery_pennies", 1000, "overwrite", false),
                Transaction.of("b", 2, 1500000004L, "ad_delivery_pennies", 1000),
                Transaction.of("c", 2, 1500000007L, "payment_pennies",     600,  "overwrite", false),
                Transaction.of("d", 1, 1500000002L, "ad_delivery_pennies", 1000, "overwrite", false),
                Transaction.of("e", 2, 1500000003L, "ad_delivery_pennies", 1000, "payment_pennies", 500, "overwrite", false),
                Transaction.of("f", 2, 1500000005L, "payment_pennies",     2000, "overwrite", true),
                Transaction.of("g", 1, 1500000003L, "payment_pennies",     500,  "overwrite", false),
                Transaction.of("h", 1, 1500000004L, "ad_delivery_pennies", 1000, "payment_pennies", 500, "overwrite", true),
                Transaction.of("i", 2, 1500000001L, "ad_delivery_pennies", 1000),
                Transaction.of("j", 2, 1500000002L, "ad_delivery_pennies", 1000),
                Transaction.of("k", 1, 1500000013L, "payment_pennies",     100)
        );
        Map<Integer, Q2Overwrite.BillingStatus> r2 = Q2Overwrite.process(cols, q2log);
        expect("Q2: user 1 ad (overwritten to 1000, nothing after)",
                r2.get(1).snapshot().get("ad_delivery_pennies"), 1000L);
        expect("Q2: user 1 payment (overwrite 500, then +100)",
                r2.get(1).snapshot().get("payment_pennies"),     600L);
        expect("Q2: user 2 ad (never overwritten, all summed)",
                r2.get(2).snapshot().get("ad_delivery_pennies"), 4000L);
        expect("Q2: user 2 payment (overwrite 2000, then +600)",
                r2.get(2).snapshot().get("payment_pennies"),     2600L);

        // THE discriminating case, isolated: an overwrite carrying only ONE column
        // must not disturb the other.
        Map<Integer, Q2Overwrite.BillingStatus> partial = Q2Overwrite.process(cols, Arrays.asList(
                Transaction.of("p1", 1, 1L, "ad_delivery_pennies", 4000),
                Transaction.of("p2", 1, 2L, "payment_pennies", 2000, "overwrite", true)
        ));
        expect("Q2: partial overwrite leaves the absent column alone",
                partial.get(1).snapshot().get("ad_delivery_pennies"), 4000L);
        expect("Q2: partial overwrite does set its own column",
                partial.get(1).snapshot().get("payment_pennies"),     2000L);

        // Q2 is a strict superset of Q1: with no overwrite flags they agree.
        expect("Q2 == Q1 when nothing overwrites",
                Q2Overwrite.process(cols, q1log).get(1).snapshot(),
                Q1Aggregate.process(cols, q1log).get(1).snapshot());

        /* ---------------- Q3: undo / redo ---------------- */
        List<Transaction> q3log = Arrays.asList(
                Transaction.of("t1", 1, 1500000001L, "ad_delivery_pennies", 1000),
                Transaction.of("t2", 1, 1500000002L, "undo_last", true),
                Transaction.of("t3", 1, 1500000003L, "payment_pennies",     500),
                Transaction.of("t4", 1, 1500000004L, "ad_delivery_pennies", 1000, "payment_pennies", 500)
        );
        Map<Integer, Q3UndoRedo.BillingStatus> r3 = Q3UndoRedo.process(cols, q3log);
        expect("Q3: ad (1000 undone, then re-added by t4)",
                r3.get(1).snapshot().get("ad_delivery_pennies"), 1000L);
        expect("Q3: payment (500 + 500)",
                r3.get(1).snapshot().get("payment_pennies"),     1000L);

        // Q3 is a strict superset of Q2: with no undo/redo they agree.
        expect("Q3 == Q2 when nothing undoes",
                Q3UndoRedo.process(cols, q2log).get(2).snapshot(),
                Q2Overwrite.process(cols, q2log).get(2).snapshot());

        // undo on empty history is a no-op, not a crash
        Q3UndoRedo.BillingStatus b = new Q3UndoRedo.BillingStatus(cols);
        b.apply(Transaction.of("u", 1, 1L, "undo_last", true));
        expect("Q3: undo on empty history is a no-op",
                b.snapshot().get("ad_delivery_pennies"), 0L);
        expect("Q3: empty undo pushes nothing to redo", b.redoDepth(), 0);

        // redo brings the value back
        b.apply(Transaction.of("r1", 1, 2L, "ad_delivery_pennies", 500));   // 500
        b.apply(Transaction.of("r2", 1, 3L, "undo_last", true));            // 0
        expect("Q3: undo moved the tx to the redo stack", b.redoDepth(), 1);
        b.apply(Transaction.of("r3", 1, 4L, "redo_last", true));            // 500 again
        expect("Q3: redo re-applies the undone tx",
                b.snapshot().get("ad_delivery_pennies"), 500L);
        expect("Q3: redo consumed the stack", b.redoDepth(), 0);

        // a new regular tx invalidates the redo branch
        b.apply(Transaction.of("r4", 1, 5L, "undo_last", true));            // 0, redo=1
        b.apply(Transaction.of("r5", 1, 6L, "payment_pennies", 42));        // regular → clears redo
        expect("Q3: new regular tx cleared the redo stack", b.redoDepth(), 0);
        b.apply(Transaction.of("r6", 1, 7L, "redo_last", true));            // no-op now
        expect("Q3: redo after a new edit is discarded (ad)",
                b.snapshot().get("ad_delivery_pennies"), 0L);
        expect("Q3: redo after a new edit is discarded (payment)",
                b.snapshot().get("payment_pennies"), 42L);

        // This is the case that proves undo can't be arithmetic: overwrite is
        // not invertible, so only a recorded snapshot can restore 700.
        Q3UndoRedo.BillingStatus c = new Q3UndoRedo.BillingStatus(cols);
        c.apply(Transaction.of("c1", 1, 1L, "ad_delivery_pennies", 700));
        c.apply(Transaction.of("c2", 1, 2L, "ad_delivery_pennies", 100, "overwrite", true));   // 100
        c.apply(Transaction.of("c3", 1, 3L, "undo_last", true));                               // 700
        expect("Q3: undo of an overwrite restores the lost value",
                c.snapshot().get("ad_delivery_pennies"), 700L);

        // Undo stacks: two undos walk back two steps.
        Q3UndoRedo.BillingStatus d = new Q3UndoRedo.BillingStatus(cols);
        d.apply(Transaction.of("d1", 1, 1L, "payment_pennies", 1));
        d.apply(Transaction.of("d2", 1, 2L, "payment_pennies", 2));
        d.apply(Transaction.of("d3", 1, 3L, "payment_pennies", 4));        // 7
        d.apply(Transaction.of("d4", 1, 4L, "undo_last", true));           // 3
        d.apply(Transaction.of("d5", 1, 5L, "undo_last", true));           // 1
        expect("Q3: consecutive undos walk back", d.snapshot().get("payment_pennies"), 1L);
        d.apply(Transaction.of("d6", 1, 6L, "redo_last", true));           // 3
        d.apply(Transaction.of("d7", 1, 7L, "redo_last", true));           // 7
        expect("Q3: consecutive redos walk forward", d.snapshot().get("payment_pennies"), 7L);

        // History is PER USER — user 1's undo must not touch user 2.
        Map<Integer, Q3UndoRedo.BillingStatus> perUser = Q3UndoRedo.process(cols, Arrays.asList(
                Transaction.of("m1", 1, 1L, "payment_pennies", 100),
                Transaction.of("m2", 2, 2L, "payment_pennies", 200),
                Transaction.of("m3", 1, 3L, "undo_last", true)
        ));
        expect("Q3: undo affected only user 1", perUser.get(1).snapshot().get("payment_pennies"), 0L);
        expect("Q3: user 2 untouched",          perUser.get(2).snapshot().get("payment_pennies"), 200L);
    }

    /* --------------------------- helpers --------------------------- */

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
