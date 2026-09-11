package reddit.New2026;

import java.util.*;

/*
================================================================================
  ModSystem — Reddit onsite (2026), 4-question progression
================================================================================

  You're building a system that tracks moderator privileges for an online
  community from a time-ordered log of moderation actions. Log entries capture
  when a user gains ("added") or loses ("removed") mod status.

  RANKING RULE
    Among currently active mods, whoever became a moderator EARLIER outranks
    whoever became a moderator LATER (based on the most recent time they became
    a moderator — so if a user is removed then re-added, their "current
    mod-since" resets to the re-add timestamp).

  --------------------------------------------------------------------------
  HOW TO READ THIS FILE
  --------------------------------------------------------------------------
    Each question gets its OWN self-contained class, using only what that
    question actually needs. Resist the urge to start from the final answer:
    the interview signal is largely in noticing WHAT FORCES each upgrade.

      Q1Simple       Map<user, ts>                        rank = sort by ts
      Q2Communities  Map<community, Map<user, ts>>        same idea, nested
      Q3Demote       per-community DLL + Map<user, Node>  order is now explicit
      Q4Badge        Q3 + sticky first-promotion map      two different clocks

    The DLL is duplicated between Q3 and Q4 on purpose — each class should read
    standalone, the way you'd actually build it up live at a whiteboard.

  --------------------------------------------------------------------------
  Q1  Base system (single community)                        → class Q1Simple
  --------------------------------------------------------------------------
    Q1Simple(List<String> logs)
        Log format: "target,action,actor,timestamp"
    boolean canRemoveMod(String target, String actor)
        true iff both are currently mods AND actor != target AND actor
        outranks target (i.e. actor's mod-since is earlier).
    List<String> getModRanking()
        Current mods, highest → lowest rank.

    Data:     Map<String, Long> addedAt   — user → most-recent add ts
    Ranking:  sort by value ascending     (earliest ts = highest rank)

    Watch for: "added" for someone who is ALREADY a mod must be a no-op, not a
    timestamp refresh — they never stopped being a mod, so nothing "became".
    Also match the action strings EXACTLY ("added"/"removed"); an unconditional
    `else` that treats every non-add as a removal will silently wipe mods on any
    unrelated action type.

  --------------------------------------------------------------------------
  Q2  Add a community layer                            → class Q2Communities
  --------------------------------------------------------------------------
    All state becomes per-community. Log format gains a leading field
        "community,target,action,actor,timestamp"
    and every read/write API takes a `community` argument.

    Data:  Map<Community, Map<User, Long>>   — same shape, one level down

    Nothing about the ALGORITHM changes here; this question is checking that
    you keep the communities fully isolated (being a mod in r/rust grants
    nothing in r/golang) and that you don't over-engineer yet.

  --------------------------------------------------------------------------
  Q3  Support demote(community, user)                     → class Q3Demote
  --------------------------------------------------------------------------
    Demote moves the given user DOWN by exactly one position in the current
    ranking. No-op if user is not a mod, or if user is already the lowest
    ranked mod.

    ⚠ THIS IS THE PIVOT OF THE WHOLE PROBLEM.
    Until now "rank" was DERIVED from the add timestamp. Demote makes the
    ordering diverge from the timestamps, so a derived ordering no longer works
    — the order has to become an explicit, materialized structure.

    Why a doubly-linked list: demote is "unlink me, re-insert after my former
    successor", which is O(1) given a node pointer, and a Map<User, Node> gives
    us that pointer in O(1). An ArrayList would make demote a swap (also O(1))
    but remove() an O(n) shift; a TreeMap of fractional ranks works but needs
    rebalancing/renumbering. The DLL is the least machinery for the job.

    Data (per community):
      DLL head↔tail with sentinels             — head.next = highest rank
      Map<User, Node>                          — O(1) lookup for edit ops
      → all of {add, remove, demote} run in O(1); allMod() is O(m).

    canRemoveMod must also be rewritten: the rule is now "actor's POSITION is
    before target's position", not a timestamp comparison. That's an O(m) walk.
    Getting it to O(1) needs an order-maintenance structure (rank-balanced BST
    or skip list) — worth naming out loud, not worth implementing.

    ⚠ The pointer-order trap in demote: `unlink` deliberately nulls out
    node.prev/node.next to avoid stale pointers into a detached node, which
    means the node can no longer be used to NAVIGATE afterwards. Read the
    successor, and check it isn't the tail, BEFORE unlinking — otherwise you
    either NPE or silently drop the node from the list while leaving it in the
    map.

  --------------------------------------------------------------------------
  Q4  "Mod-since" honor badge surviving demotion/re-add     → class Q4Badge
  --------------------------------------------------------------------------
    Add an API returning each mod's FIRST-EVER promotion timestamp within this
    community, so an early mod who has since been demoted (or removed and
    re-added) still shows their original mod-since date.

    The insight: you now need TWO timestamps with DIFFERENT lifetimes.
      - current mod-since  → resets on re-add   (and after Q3, no longer even
                              determines rank, so we stop storing it at all)
      - honor badge        → written once, never overwritten, survives both
                              demotion and remove→re-add

    Data (per community):
      Map<User, Long> firstEverPromotion       — sticky, survives removal
      + each Node carries `firstPromotedAt`    — O(1) badge read during a scan

    Why both: the badge has to outlive the Node (remove() destroys the Node),
    so it can't live only on the Node. The Node copy is just a cache so a
    ranking scan doesn't need a second map lookup per entry.

    Invariants:
      - firstEverPromotion[u] is set on FIRST "added" and never overwritten.
      - On re-add after removal we restore that value onto the new Node, so the
        badge survives remove → re-add.

  --------------------------------------------------------------------------
  Complexity   (L = # log entries, C = # communities,
                m = # mods in the community being touched)
  --------------------------------------------------------------------------
    Q1Simple / Q2Communities   (Map of timestamps)
      constructor              O(L)          one hashmap op per entry
      canRemoveMod             O(1)          2 map gets + compare
      getModRanking            O(m log m)    sort entries by timestamp
      space                    O(distinct users), ×C for Q2

    Q3Demote / Q4Badge         (per community: DLL + Map<user, Node>)
      constructor              O(L)          each entry is an O(1) DLL edit
      canRemoveMod             O(m)          pos() walks the DLL
      getModRanking            O(m)          one linear DLL scan
      demote                   O(1)          unlink + insertAfter neighbor
      getModRankingWithBadge   O(m)          single DLL scan
      getFirstPromotion        O(1)          two map gets
      add / remove (internal)  O(1)          DLL edit + hashmap put/remove
      space                    O(C + total mods + total ever-promoted users)
================================================================================
*/
public class ModSystem {

    private ModSystem() {}   // namespace only — each question lives in its own class

    /* ============================================================
       Q1 — single community: Map<user, timestamp> + sort
       ============================================================ */
    public static final class Q1Simple {
        private final Map<String, Long> addedAt = new HashMap<>();

        /** Log: "target,action,actor,timestamp". Time O(L) — one hashmap op per entry. */
        public Q1Simple(List<String> logs) {
            for (String log : logs) {
                String[] arr = log.split(",");
                String target = arr[0], action = arr[1];
                long time = Long.parseLong(arr[3]);

                if ("added".equals(action)) {
                    addedAt.putIfAbsent(target, time);          // already a mod → no refresh
                } else if ("removed".equals(action)) {
                    addedAt.remove(target);
                }
            }
        }

        /** Time O(1) — two hashmap gets and a compare. */
        public boolean canRemoveMod(String target, String actor) {
            if (target.equals(actor)) return false;
            Long tTs = addedAt.get(target), aTs = addedAt.get(actor);
            if (tTs == null || aTs == null) return false;
            return aTs < tTs;                                   // actor promoted earlier → outranks
        }

        /** Time O(m log m) — sort the m active mods by promotion timestamp. */
        public List<String> getModRanking() {
            List<Map.Entry<String, Long>> list = new ArrayList<>(addedAt.entrySet());
            list.sort(Map.Entry.comparingByValue());

            List<String> result = new ArrayList<>();
            for (Map.Entry<String, Long> entry : list) result.add(entry.getKey());
            return result;
        }
    }

    /* ============================================================
       Q2 — community layer: same solution, nested one level.
            Deliberately still NO ordered structure: nothing has asked
            us to reorder mods yet.
       ============================================================ */
    public static final class Q2Communities {
        private final Map<String, Map<String, Long>> communityMap = new HashMap<>();

        /** Log: "community,target,action,actor,timestamp". Time O(L) — one hashmap op per entry. */
        public Q2Communities(List<String> logs) {
            for (String log : logs) {
                String[] arr = log.split(",");
                String community = arr[0], target = arr[1], action = arr[2];
                long time = Long.parseLong(arr[4]);

                if ("added".equals(action)) {
                    Map<String, Long> addedAt = communityMap.computeIfAbsent(community, k -> new HashMap<>());
                    addedAt.putIfAbsent(target, time);
                } else if ("removed".equals(action)) {
                    Map<String, Long> addedAt = communityMap.get(community);
                    if (addedAt != null) addedAt.remove(target);
                }
            }
        }

        /** Time O(1) — two hashmap gets and a compare. Communities are fully isolated. */
        public boolean canRemoveMod(String community, String target, String actor) {
            if (target.equals(actor)) return false;
            Map<String, Long> addedAt = communityMap.get(community);
            if (addedAt == null) return false;

            Long tTs = addedAt.get(target), aTs = addedAt.get(actor);
            if (tTs == null || aTs == null) return false;
            return aTs < tTs;                                   // actor promoted earlier → outranks
        }

        /** Time O(m log m) — sort the m active mods by promotion timestamp. */
        public List<String> getModRanking(String community) {
            Map<String, Long> addedAt = communityMap.get(community);
            if (addedAt == null) return Collections.emptyList();

            List<Map.Entry<String, Long>> list = new ArrayList<>(addedAt.entrySet());
            list.sort(Map.Entry.comparingByValue());

            List<String> result = new ArrayList<>();
            for (Map.Entry<String, Long> entry : list) result.add(entry.getKey());
            return result;
        }
    }

    /* ============================================================
       Q3 — demote(user) moves someone down one slot.
            Rank can no longer be derived from timestamps, so the
            ordering becomes an explicit DLL and the timestamps stop
            being needed at all.
       ============================================================ */
    public static final class Q3Demote {

        private static final class Node {
            final String name;
            Node prev, next;

            public Node() { this(null); }                       // sentinel

            public Node(String name) {
                this.name = name;
            }
        }

        /** DLL of mods with sentinels; head.next = highest rank, tail.prev = lowest. */
        private static final class Community {
            final Node head, tail;
            final Map<String, Node> map = new HashMap<>();

            public Community() {
                this.head = new Node();
                this.tail = new Node();
                head.next = tail;
                tail.prev = head;
            }

            /** O(1). New mods enter at the BOTTOM — they were promoted most recently. */
            public void add(String name) {
                if (map.containsKey(name)) return;              // idempotent

                Node node = new Node(name);
                insertBefore(tail, node);

                map.put(name, node);
            }

            /** O(1). */
            public void remove(String name) {
                Node node = map.remove(name);
                if (node != null) unlink(node);
            }

            /** O(1) — the whole reason for the DLL. */
            public void demote(String name) {
                Node node = map.get(name);
                if (node == null) return;                       // not a mod → no-op

                Node next = node.next;                          // read BEFORE unlink nulls it
                if (next == tail) return;                       // already lowest → no-op

                unlink(node);
                insertAfter(next, node);
            }

            /** 0-based position, -1 if not a mod. O(m). */
            public int pos(String name) {
                Node node = map.get(name);
                if (node == null) return -1;

                Node n = head.next;
                int i = 0;
                while (n != tail) {
                    if (n == node) return i;
                    i++;
                    n = n.next;
                }

                return -1;
            }

            /** O(m). */
            public List<String> allMod() {
                List<String> result = new ArrayList<>();

                Node n = head.next;
                while (n != tail) {
                    result.add(n.name);
                    n = n.next;
                }

                return result;
            }

            private void insertBefore(Node ref, Node node) {
                ref.prev.next = node;
                node.prev = ref.prev;

                node.next = ref;
                ref.prev = node;
            }

            /** Caller must not pass `tail` as ref — guarding here would hide the mistake. */
            private void insertAfter(Node ref, Node node) {
                ref.next.prev = node;
                node.next = ref.next;

                ref.next = node;
                node.prev = ref;
            }

            private void unlink(Node node) {
                node.prev.next = node.next;
                node.next.prev = node.prev;

                node.prev = null;
                node.next = null;
            }
        }

        private final Map<String, Community> communities = new HashMap<>();

        /** Log: "community,target,action,actor,timestamp". Time O(L) — each entry is an O(1) DLL edit.
         *  Note the timestamp is now unused: the log order already encodes promotion order. */
        public Q3Demote(List<String> logs) {
            for (String log : logs) {
                String[] arr = log.split(",");
                String community = arr[0], target = arr[1], action = arr[2];

                if ("added".equals(action)) {
                    Community cmt = communities.computeIfAbsent(community, k -> new Community());
                    cmt.add(target);
                } else if ("removed".equals(action)) {
                    Community cmt = communities.get(community);
                    if (cmt != null) cmt.remove(target);
                }
            }
        }

        /** Time O(m) — two DLL position walks. Compares POSITION now, not timestamp. */
        public boolean canRemoveMod(String community, String target, String actor) {
            if (target.equals(actor)) return false;
            Community cmt = communities.get(community);
            if (cmt == null) return false;

            int tPos = cmt.pos(target), aPos = cmt.pos(actor);
            if (tPos == -1 || aPos == -1) return false;
            return aPos < tPos;
        }

        /** Time O(m) — single linear DLL scan. */
        public List<String> getModRanking(String community) {
            Community cmt = communities.get(community);
            if (cmt == null) return Collections.emptyList();

            return cmt.allMod();
        }

        /** Time O(1) — hashmap lookup + unlink + insertAfter neighbor. */
        public void demote(String community, String user) {
            Community cmt = communities.get(community);
            if (cmt == null) return;

            cmt.demote(user);
        }
    }

    /* ============================================================
       Q4 — honor badge: first-ever promotion ts, sticky across
            demotion AND remove→re-add. Q3's DLL plus a second,
            longer-lived timestamp.
       ============================================================ */
    public static final class Q4Badge {

        private static final class Node {
            final String name;
            final long firstPromotedAt;                         // sticky honor timestamp
            Node prev, next;

            public Node() { this(null, -1); }                   // sentinel

            public Node(String name, long firstPromotedAt) {
                this.name = name;
                this.firstPromotedAt = firstPromotedAt;
            }
        }

        private static final class Community {
            final Node head, tail;
            final Map<String, Node> map = new HashMap<>();

            public Community() {
                this.head = new Node();
                this.tail = new Node();
                head.next = tail;
                tail.prev = head;
            }

            public boolean has(String name) { return map.containsKey(name); }

            /** O(1). `firstTs` is the honor timestamp, already resolved by the caller. */
            public void add(String name, long firstTs) {
                if (map.containsKey(name)) return;              // idempotent

                Node node = new Node(name, firstTs);
                insertBefore(tail, node);

                map.put(name, node);
            }

            public void remove(String name) {
                Node node = map.remove(name);
                if (node != null) unlink(node);
            }

            public void demote(String name) {
                Node node = map.get(name);
                if (node == null) return;

                Node next = node.next;                          // read BEFORE unlink nulls it
                if (next == tail) return;                       // already lowest → no-op

                unlink(node);
                insertAfter(next, node);
            }

            public int pos(String name) {
                Node node = map.get(name);
                if (node == null) return -1;

                Node n = head.next;
                int i = 0;
                while (n != tail) {
                    if (n == node) return i;
                    i++;
                    n = n.next;
                }

                return -1;
            }

            public List<String> allMod() {
                List<String> result = new ArrayList<>();

                Node n = head.next;
                while (n != tail) {
                    result.add(n.name);
                    n = n.next;
                }

                return result;
            }

            /** Same scan as allMod(), carrying each node's honor timestamp. O(m). */
            public List<Badge> allBadges() {
                List<Badge> result = new ArrayList<>();

                Node n = head.next;
                while (n != tail) {
                    result.add(new Badge(n.name, n.firstPromotedAt));
                    n = n.next;
                }

                return result;
            }

            private void insertBefore(Node ref, Node node) {
                ref.prev.next = node;
                node.prev = ref.prev;

                node.next = ref;
                ref.prev = node;
            }

            /** Caller must not pass `tail` as ref — guarding here would hide the mistake. */
            private void insertAfter(Node ref, Node node) {
                ref.next.prev = node;
                node.next = ref.next;

                ref.next = node;
                node.prev = ref;
            }

            private void unlink(Node node) {
                node.prev.next = node.next;
                node.next.prev = node.prev;

                node.prev = null;
                node.next = null;
            }
        }

        /** The honor timestamp outlives the Node, so it can't live only on the Node. */
        private final Map<String, Community>         communities        = new HashMap<>();
        private final Map<String, Map<String, Long>> firstEverPromotion = new HashMap<>();

        /** Log: "community,target,action,actor,timestamp". Time O(L). */
        public Q4Badge(List<String> logs) {
            for (String log : logs) {
                String[] arr = log.split(",");
                String community = arr[0], target = arr[1], action = arr[2];
                long time = Long.parseLong(arr[4]);

                if ("added".equals(action)) {
                    Community cmt = communities.computeIfAbsent(community, k -> new Community());
                    if (cmt.has(target)) continue;              // idempotent

                    Map<String, Long> firsts =
                            firstEverPromotion.computeIfAbsent(community, k -> new HashMap<>());
                    long firstTs = firsts.computeIfAbsent(target, k -> time);   // sticky: write once
                    cmt.add(target, firstTs);
                } else if ("removed".equals(action)) {
                    Community cmt = communities.get(community);
                    if (cmt != null) cmt.remove(target);
                }
            }
        }

        /** Time O(m) — two DLL position walks. */
        public boolean canRemoveMod(String community, String target, String actor) {
            if (target.equals(actor)) return false;
            Community cmt = communities.get(community);
            if (cmt == null) return false;

            int tPos = cmt.pos(target), aPos = cmt.pos(actor);
            if (tPos == -1 || aPos == -1) return false;
            return aPos < tPos;
        }

        /** Time O(m) — single linear DLL scan. */
        public List<String> getModRanking(String community) {
            Community cmt = communities.get(community);
            if (cmt == null) return Collections.emptyList();

            return cmt.allMod();
        }

        /** Time O(1). */
        public void demote(String community, String user) {
            Community cmt = communities.get(community);
            if (cmt == null) return;

            cmt.demote(user);
        }

        /** Current mods with their honor-badge timestamp, high → low rank. Time O(m). */
        public List<Badge> getModRankingWithBadge(String community) {
            Community cmt = communities.get(community);
            if (cmt == null) return Collections.emptyList();

            return cmt.allBadges();
        }

        /** First-ever promotion ts in this community, or -1. Survives removal entirely. Time O(1). */
        public long getFirstPromotion(String community, String user) {
            Map<String, Long> firsts = firstEverPromotion.get(community);
            if (firsts == null) return -1;

            return firsts.getOrDefault(user, -1L);
        }
    }

    public static final class Badge {
        public final String user;
        public final long firstPromotedAt;
        Badge(String u, long ts) { user = u; firstPromotedAt = ts; }

        @Override public String toString() { return user + "@" + firstPromotedAt; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Badge)) return false;
            Badge b = (Badge) o;
            return firstPromotedAt == b.firstPromotedAt && Objects.equals(user, b.user);
        }
        @Override public int hashCode() { return Objects.hash(user, firstPromotedAt); }
    }

    /* ============================================================
       Tests / demo — one block per question
       ============================================================ */

    public static void main(String[] args) {

        /* ---------------- Q1: single community ---------------- */
        Q1Simple q1 = new Q1Simple(Arrays.asList(
                "alice,added,root,1",
                "bob,added,root,2",
                "carol,added,root,3",
                "bob,removed,alice,4",
                "bob,added,root,5"
        ));
        expect("Q1 ranking — re-added bob drops to last",
                q1.getModRanking(), Arrays.asList("alice", "carol", "bob"));
        expect("Q1 alice can remove bob",     q1.canRemoveMod("bob",   "alice"), true);
        expect("Q1 bob cannot remove alice",  q1.canRemoveMod("alice", "bob"),   false);
        expect("Q1 self-remove rejected",     q1.canRemoveMod("alice", "alice"), false);
        expect("Q1 non-mod actor rejected",   q1.canRemoveMod("alice", "eve"),   false);
        expect("Q1 removed target rejected",  q1.canRemoveMod("eve",   "alice"), false);

        Q1Simple q1dup = new Q1Simple(Arrays.asList(
                "alice,added,root,1",
                "bob,added,root,2",
                "alice,added,root,9"          // already a mod → must NOT refresh to 9
        ));
        expect("Q1 re-add of a current mod is a no-op",
                q1dup.getModRanking(), Arrays.asList("alice", "bob"));

        Q1Simple q1weird = new Q1Simple(Arrays.asList(
                "alice,added,root,1",
                "alice,flaired,root,2"        // unrelated action must not strip mod status
        ));
        expect("Q1 unknown action is ignored",
                q1weird.getModRanking(), Arrays.asList("alice"));

        /* ---------------- Q2: community isolation ---------------- */
        Q2Communities q2 = new Q2Communities(Arrays.asList(
                "rust,alice,added,root,1",
                "rust,bob,added,root,2",
                "rust,carol,added,root,3",
                "golang,dave,added,root,4",
                "rust,bob,removed,alice,5",
                "rust,bob,added,root,6"
        ));
        expect("Q2 rust ranking",   q2.getModRanking("rust"),   Arrays.asList("alice", "carol", "bob"));
        expect("Q2 golang ranking", q2.getModRanking("golang"), Arrays.asList("dave"));
        expect("Q2 unknown community", q2.getModRanking("elixir"), Collections.emptyList());
        expect("Q2 rust mod has no power in golang",
                q2.canRemoveMod("golang", "dave", "alice"), false);
        expect("Q2 alice outranks bob in rust",
                q2.canRemoveMod("rust", "bob", "alice"), true);

        Q2Communities q2orphan = new Q2Communities(Arrays.asList(
                "elixir,alice,removed,root,1"  // 'removed' before any 'added' must not throw
        ));
        expect("Q2 orphan removal is ignored",
                q2orphan.getModRanking("elixir"), Collections.emptyList());

        /* ---------------- Q3: demote via DLL ---------------- */
        List<String> log = Arrays.asList(
                "rust,alice,added,root,1",
                "rust,bob,added,root,2",
                "rust,carol,added,root,3",
                "golang,dave,added,root,4",
                "rust,bob,removed,alice,5",
                "rust,bob,added,root,6"
        );
        Q3Demote q3 = new Q3Demote(log);
        expect("Q3 initial ranking matches Q2",
                q3.getModRanking("rust"), Arrays.asList("alice", "carol", "bob"));

        q3.demote("rust", "alice");                       // → carol, alice, bob
        expect("Q3 demote(alice) moves her down one",
                q3.getModRanking("rust"), Arrays.asList("carol", "alice", "bob"));
        expect("Q3 carol (now top) can remove alice",
                q3.canRemoveMod("rust", "alice", "carol"), true);
        expect("Q3 alice (now middle) cannot remove carol",
                q3.canRemoveMod("rust", "carol", "alice"), false);

        q3.demote("rust", "alice");                       // → carol, bob, alice
        expect("Q3 demote again",
                q3.getModRanking("rust"), Arrays.asList("carol", "bob", "alice"));
        q3.demote("rust", "alice");                       // already lowest
        expect("Q3 demoting the lowest mod is a no-op",
                q3.getModRanking("rust"), Arrays.asList("carol", "bob", "alice"));
        expect("Q3 lowest mod is still ranked after the no-op",
                q3.canRemoveMod("rust", "alice", "carol"), true);
        q3.demote("rust", "ghost");                       // not a mod
        expect("Q3 demoting a non-mod is a no-op",
                q3.getModRanking("rust"), Arrays.asList("carol", "bob", "alice"));
        q3.demote("elixir", "alice");                     // unknown community
        expect("Q3 demote in unknown community is a no-op",
                q3.getModRanking("elixir"), Collections.emptyList());
        expect("Q3 demote didn't leak across communities",
                q3.getModRanking("golang"), Arrays.asList("dave"));

        Q3Demote q3orphan = new Q3Demote(Arrays.asList("elixir,alice,removed,root,1"));
        expect("Q3 orphan removal is ignored",
                q3orphan.getModRanking("elixir"), Collections.emptyList());

        /* ---------------- Q4: honor badge ---------------- */
        Q4Badge q4 = new Q4Badge(log);                    // bob re-added: badge must stay 2
        q4.demote("rust", "alice");
        q4.demote("rust", "alice");                       // → carol, bob, alice
        expect("Q4 ranking after two demotes",
                q4.getModRanking("rust"), Arrays.asList("carol", "bob", "alice"));
        expect("Q4 badges reflect first-ever add, not current rank",
                q4.getModRankingWithBadge("rust"),
                Arrays.asList(new Badge("carol", 3), new Badge("bob", 2), new Badge("alice", 1)));
        expect("Q4 badge survives remove → re-add",
                q4.getFirstPromotion("rust", "bob"), 2L);
        expect("Q4 badge survives demotion",
                q4.getFirstPromotion("rust", "alice"), 1L);
        expect("Q4 unknown user has no badge",
                q4.getFirstPromotion("rust", "ghost"), -1L);
        expect("Q4 unknown community has no badge",
                q4.getFirstPromotion("elixir", "alice"), -1L);

        Q4Badge q4removed = new Q4Badge(Arrays.asList(
                "rust,alice,added,root,1",
                "rust,alice,removed,root,10"
        ));
        expect("Q4 badge outlives removal entirely",
                q4removed.getFirstPromotion("rust", "alice"), 1L);
        expect("Q4 removed mod is not in the ranking",
                q4removed.getModRanking("rust"), Collections.emptyList());
        expect("Q4 removed actor can't remove anyone",
                q4removed.canRemoveMod("rust", "bob", "alice"), false);
    }

    /* --------------------------- helpers --------------------------- */

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
