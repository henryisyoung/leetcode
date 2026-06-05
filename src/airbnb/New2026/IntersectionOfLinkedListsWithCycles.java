package airbnb.New2026;
/*
Intersection of Two Linked Lists (possibly cyclic).

Given the heads of two singly-linked lists `a` and `b`, either of
which may contain a cycle, decide whether the two lists share at
least one node (by identity, NOT by value). If they do, return that
node; otherwise return null.

I/O
  Input : ListNode a, ListNode b
  Output: ListNode (a shared node, or null)

Constraints
  Up to ~1e5 nodes per list.
  Each list is either acyclic or contains exactly one cycle (the only
  shapes a singly-linked list can have: ρ-shape or straight line).

Examples
  acyclic + acyclic, joined Y-shape -> returns the merge node
  one cyclic, one acyclic           -> always null (proof below)
  both cyclic, same cycle           -> returns a shared node
  both cyclic, different cycles     -> null
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
Key observation

  A singly-linked list is either acyclic (ends in null) or has a tail
  that loops back to an earlier node — exactly one cycle, shaped like
  the Greek letter ρ. Once a node is "in the cycle", every node it
  reaches is also in that same cycle.

  Therefore: if list X has a cycle and list Y doesn't, they CANNOT
  share a node. Proof: if node n is shared, then following X forever
  visits the cycle, so does following Y from n — but Y is acyclic.
  Contradiction. So we can short-circuit when the cycle-status of the
  two lists disagrees.

Algorithm

  Step 1: for each list, detect a cycle and (if present) find its
          entry node, using Floyd's tortoise-and-hare.

  Step 2: case-split on (entryA, entryB):

    (a) entryA == null  AND  entryB == null
          Both acyclic. Classic intersection: count lengths, advance
          the longer head by the difference, then walk together
          comparing references. O(m + n) time, O(1) space.

    (b) (entryA == null) XOR (entryB == null)
          One cyclic, one not. Return null.

    (c) entryA == entryB
          Both cyclic and share the SAME cycle entry. The merge
          happens in the acyclic prefix (or exactly at the entry).
          Run the classic acyclic intersection but with `entryA` as
          the "end-of-list" sentinel instead of null.

    (d) entryA != entryB, both non-null
          Both cyclic but with different entries. They either share
          the same cycle (then the cycle was entered from two
          different directions and ANY cycle node is shared — pick
          entryA or entryB), or they have disjoint cycles. To tell:
          walk one full lap from entryA; if we ever see entryB along
          the way, the cycles are the same and we return entryA (or
          entryB — both are valid shared nodes). Otherwise null.

  Why "return entryA" is valid in case (d): in a single-list-with-
  cycle, every node in the cycle is reachable from the entry and
  reachable from any node in the cycle. So if both lists share the
  cycle, entryA is in list B's reachable set and vice versa.

Complexity
  Time:   O(m + n)
  Memory: O(1)
*/
public class IntersectionOfLinkedListsWithCycles {

    public static class ListNode {
        public int val;
        public ListNode next;
        public ListNode(int val) { this.val = val; }
    }

    public ListNode getIntersection(ListNode a, ListNode b) {
        if (a == null || b == null) return null;

        ListNode entryA = cycleEntry(a);
        ListNode entryB = cycleEntry(b);

        // Case (a): both acyclic.
        if (entryA == null && entryB == null) {
            return acyclicIntersect(a, b, null);
        }

        // Case (b): exactly one has a cycle.
        if ((entryA == null) != (entryB == null)) return null;

        // Case (c): both cyclic, same entry — merge is in the acyclic prefix.
        if (entryA == entryB) {
            return acyclicIntersect(a, b, entryA);
        }

        // Case (d): both cyclic, different entries — check if same cycle.
        ListNode p = entryA.next;
        while (p != entryA) {
            if (p == entryB) return entryA;             // any cycle node is a valid answer
            p = p.next;
        }
        return null;
    }

    /** Floyd: returns the cycle-entry node, or null if the list is acyclic. */
    private static ListNode cycleEntry(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {                          // meet inside cycle
                slow = head;
                while (slow != fast) { slow = slow.next; fast = fast.next; }
                return slow;
            }
        }
        return null;
    }

    /**
     * Find the first shared node of two acyclic paths that both terminate
     * at the same sentinel `end` (null for plain acyclic lists, or a
     * shared cycle entry for case (c)). Returns null if disjoint.
     */
    private static ListNode acyclicIntersect(ListNode a, ListNode b, ListNode end) {
        int la = lengthUntil(a, end);
        int lb = lengthUntil(b, end);
        while (la > lb) { a = a.next; la--; }
        while (lb > la) { b = b.next; lb--; }
        while (a != end && b != end) {
            if (a == b) return a;
            a = a.next;
            b = b.next;
        }
        // If both walked to `end` without meeting, one of them might
        // still equal `end` itself (when `end` IS the merge node).
        return a == b ? a : null;
    }

    private static int lengthUntil(ListNode head, ListNode end) {
        int n = 0;
        ListNode p = head;
        while (p != end) {
            n++;
            p = p.next;
        }
        return n;
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

    /**
     * Stdin (one test per 3 lines, repeated):
     *   line 1: space-separated values for list A
     *   line 2: space-separated values for list B
     *   line 3: "<aCycleIdx> <bCycleIdx> <mergeIdxInA> <mergeIdxInB>"
     *           use -1 for no cycle / no merge.
     * Prints "YES <val>" or "NO".
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        IntersectionOfLinkedListsWithCycles solver = new IntersectionOfLinkedListsWithCycles();
        String la;
        while ((la = br.readLine()) != null) {
            String lb = br.readLine();
            String lc = br.readLine();
            if (lb == null || lc == null) break;
            int[] cfg = parseInts(lc);
            ListNode A = build(parseInts(la));
            ListNode B = build(parseInts(lb));
            // Merge B into A at mergeIdxInA: drop B's tail at mergeIdxInB and link to A's node.
            if (cfg.length >= 4 && cfg[2] >= 0 && cfg[3] >= 0) {
                ListNode shared = nodeAt(A, cfg[2]);
                ListNode bTail  = nodeAt(B, cfg[3] - 1);
                if (bTail != null) bTail.next = shared;
                else B = shared;
            }
            if (cfg.length >= 1 && cfg[0] >= 0) makeCycle(A, cfg[0]);
            if (cfg.length >= 2 && cfg[1] >= 0) makeCycle(B, cfg[1]);

            ListNode hit = solver.getIntersection(A, B);
            System.out.println(hit == null ? "NO" : "YES " + hit.val);
        }
    }

    private static void runDemos() {
        IntersectionOfLinkedListsWithCycles solver = new IntersectionOfLinkedListsWithCycles();

        // ---- (a) Acyclic Y-shape ----
        //   A: 1 -> 2 -> 3 \
        //                    -> 7 -> 8 -> 9
        //   B:      4 -> 5 /
        ListNode common = build(new int[]{7, 8, 9});
        ListNode A1 = appendNew(build(new int[]{1, 2, 3}), common);
        ListNode B1 = appendNew(build(new int[]{4, 5}),    common);
        check("acyclic Y-shape, merges at 7",
                valOf(solver.getIntersection(A1, B1)), 7);

        // ---- (a) Acyclic disjoint ----
        ListNode A2 = build(new int[]{1, 2, 3});
        ListNode B2 = build(new int[]{4, 5, 6});
        check("acyclic disjoint", valOf(solver.getIntersection(A2, B2)), Integer.MIN_VALUE);

        // ---- (b) One cyclic, one acyclic ----
        ListNode A3 = build(new int[]{1, 2, 3, 4, 5});
        makeCycle(A3, 2);                        // 3 -> 4 -> 5 -> 3
        ListNode B3 = build(new int[]{9, 8, 7});
        check("one cyclic, one acyclic", valOf(solver.getIntersection(A3, B3)), Integer.MIN_VALUE);

        // ---- (c) Both cyclic with SAME cycle entry — merge in acyclic prefix ----
        //   A: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 3 (cycle entry = node "3")
        //   B:      7 ----------/
        //   B joins A at node "3" (the cycle entry). Intersection = node 3.
        ListNode A4 = build(new int[]{1, 2, 3, 4, 5, 6});
        ListNode mergeNode = nodeAt(A4, 2);      // node with val=3
        makeCycle(A4, 2);
        ListNode B4 = build(new int[]{7});
        B4.next = mergeNode;
        check("both cyclic, same entry, merge in prefix",
                valOf(solver.getIntersection(A4, B4)), 3);

        // ---- (d) Both cyclic, SAME cycle, DIFFERENT entries ----
        //   A: 1 -> 2 -> 3 -> 4 -> 5 -> 3   (entry = "3")
        //   B: 9 -> 8 -> 5 -> 3 -> 4 -> 5   (entry = "5")  shares the cycle 3-4-5
        //   Build A first, then B's acyclic prefix points into A's cycle at node "5".
        ListNode A5 = build(new int[]{1, 2, 3, 4, 5});
        makeCycle(A5, 2);
        ListNode jumpTo5InA = nodeAt(A5, 4);     // node with val=5 in A's cycle
        ListNode B5 = build(new int[]{9, 8});
        // attach B's tail to A's node "5"
        nodeAt(B5, 1).next = jumpTo5InA;
        // Now B is: 9 -> 8 -> 5 -> 3 -> 4 -> 5 -> ...  (entry = 5 from B's POV)
        int got5 = valOf(solver.getIntersection(A5, B5));
        // Either "3" (entryA) or "5" (entryB) is a valid shared node.
        check("both cyclic, same cycle, different entries (got 3 or 5)",
                got5 == 3 || got5 == 5, true);

        // ---- (d) Both cyclic, DIFFERENT cycles ----
        ListNode A6 = build(new int[]{1, 2, 3, 4});
        makeCycle(A6, 1);                        // cycle: 2->3->4->2
        ListNode B6 = build(new int[]{5, 6, 7, 8});
        makeCycle(B6, 0);                        // cycle: 5->6->7->8->5  (disjoint)
        check("both cyclic, disjoint cycles", valOf(solver.getIntersection(A6, B6)), Integer.MIN_VALUE);

        // ---- Self / null ----
        check("both null", valOf(solver.getIntersection(null, null)), Integer.MIN_VALUE);
        check("one null",  valOf(solver.getIntersection(A1, null)),    Integer.MIN_VALUE);
        check("same head", valOf(solver.getIntersection(A1, A1)),      1);

        // ---- Stress: long acyclic merge ----
        int N = 100_000;
        ListNode tail = build(new int[]{42});
        ListNode bigA = appendChain(N, tail);
        ListNode bigB = appendChain(N - 5, tail);
        long t0 = System.nanoTime();
        int got = valOf(solver.getIntersection(bigA, bigB));
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress acyclic merge", got, 42);
        System.out.println("Stress 2x" + N + " in " + ms + " ms");
    }

    /* --------------------------- helpers --------------------------- */

    private static ListNode build(int[] xs) {
        if (xs == null || xs.length == 0) return null;
        ListNode head = new ListNode(xs[0]);
        ListNode cur = head;
        for (int i = 1; i < xs.length; i++) { cur.next = new ListNode(xs[i]); cur = cur.next; }
        return head;
    }

    /** Append `tail` (existing chain) to the end of a freshly-built `head` chain. */
    private static ListNode appendNew(ListNode head, ListNode tail) {
        if (head == null) return tail;
        ListNode cur = head;
        while (cur.next != null) cur = cur.next;
        cur.next = tail;
        return head;
    }

    /** Make a length-N prefix and link its tail to `tail`. */
    private static ListNode appendChain(int prefixLen, ListNode tail) {
        ListNode head = new ListNode(0);
        ListNode cur = head;
        for (int i = 1; i < prefixLen; i++) { cur.next = new ListNode(i); cur = cur.next; }
        cur.next = tail;
        return head;
    }

    /** Find node at 0-based index, or null if out of range. */
    private static ListNode nodeAt(ListNode head, int idx) {
        for (int i = 0; head != null && i < idx; i++) head = head.next;
        return head;
    }

    /** Link the last node to node at index `entryIdx`, creating a cycle. */
    private static void makeCycle(ListNode head, int entryIdx) {
        ListNode entry = nodeAt(head, entryIdx);
        ListNode tail = head;
        while (tail.next != null) tail = tail.next;
        tail.next = entry;
    }

    private static int valOf(ListNode n) { return n == null ? Integer.MIN_VALUE : n.val; }

    private static int[] parseInts(String s) {
        if (s == null || s.trim().isEmpty()) return new int[0];
        String[] tok = s.trim().split("\\s+");
        int[] r = new int[tok.length];
        for (int i = 0; i < tok.length; i++) r[i] = Integer.parseInt(tok[i]);
        return r;
    }

    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + show(got) + " expected=" + show(expected));
    }
    private static void check(String label, boolean got, boolean expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static String show(int v) { return v == Integer.MIN_VALUE ? "null" : Integer.toString(v); }
}
