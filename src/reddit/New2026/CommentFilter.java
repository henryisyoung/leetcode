package reddit.New2026;

import java.util.*;

/*
================================================================================
  CommentFilter — Reddit onsite (2026)
================================================================================

  Given a dictionary of Reddit comments (id, parent_comment, body, cat, dog).
  Comments form a tree via parent_comment.

  Modes
    CAT_PERSON  → doesn't want dog comments
    DOG_PERSON  → doesn't want cat comments

  Rule: if a comment is about the unwanted animal, exclude it AND all of its
  descendants. Return the set of excluded ids.

    5 (dog) └── 6 └── 7 └── 8      CAT_PERSON → {5, 6, 7, 8}

  Approach
    1. Build parent → children map.                         O(n)
    2. For every "bad" comment, DFS its subtree (recursive) and add
       every node to `excluded`. Skip nodes already excluded, so each comment
       is visited at most once even when bad comments are nested.

  Things to say out loud
    - parent_comment may point to an id that isn't in the input (the example:
      comment 2's parent 1 is missing). Treat it as a root; its own flags
      still decide.
    - A comment can be both cat and dog → excluded in both modes.
    - Recursion depth = height of the reply tree. A very long reply chain
      (tens of thousands deep) can overflow the call stack; switch to an
      explicit stack if the interviewer asks.
    - Bad input with a cycle: the `excluded` check also stops the walk.

  Complexity  (n = number of comments)
    Time   O(n)   build children map once; every comment is pushed at most once
    Space  O(n)   children map + excluded set + recursion depth O(h), h ≤ n

    Follow-ups
    - Alternative: for each comment, walk UP until a bad ancestor or the root,
      memoizing the answer per id → also O(n), no children map needed.
    - Streaming (new comments arrive one by one): a new comment is excluded
      iff it is bad OR its parent is excluded → O(1) per new comment.
    - Many modes / animals: same walk, `isBad` becomes "flags ∩ unwanted ≠ ∅".

  --------------------------------------------------------------------------
  Part 2 — popular groups stay visible
  --------------------------------------------------------------------------
    Each comment has a `score`. Given `target`: a group (a bad comment + all
    its descendants) is NOT hidden if the group's score sum > target.

    Assumptions (say them out loud):
      - The sum is over the bad comment's whole subtree.
      - Each bad comment is judged on its own. A popular bad group stays
        visible, but a small bad reply INSIDE it can still be hidden:
            hidden(c) = hidden(parent) || (isBad(c) && subtreeSum(c) <= target)
      - "超过 / exceeds" = strictly greater.

    Approach — two recursive DFS passes from the roots:
      1. dfsSum  (post-order, bottom-up): sum(c) = score(c) + Σ sum(child).
      2. dfsHide (pre-order, top-down):   pass `parentHidden` down, apply the
         rule above.

    Why not one top-down walk alone? A node's subtree sum depends on all its
    descendants, so sums must be computed bottom-up first.

    Complexity: O(n) time, O(n) space (children map, sums, recursion depth h).
    target = Long.MAX_VALUE → no group can exceed it → same answer as Part 1.
================================================================================
*/
public class CommentFilter {

    public enum Mode { CAT_PERSON, DOG_PERSON }

    public static class Comment {
        final int id;
        final Integer parentComment;   // null for a top-level comment
        final String body;
        final boolean cat, dog;
        final long score;               // Part 2

        public Comment(int id, Integer parentComment, String body, boolean cat, boolean dog) {
            this(id, parentComment, body, cat, dog, 0);
        }

        public Comment(int id, Integer parentComment, String body, boolean cat, boolean dog, long score) {
            this.id = id;
            this.parentComment = parentComment;
            this.body = body;
            this.cat = cat;
            this.dog = dog;
            this.score = score;
        }
    }

    public static Set<Integer> getCommentsToExclude(Map<Integer, Comment> comments, Mode mode) {
        Set<Integer> excluded = new HashSet<>();
        if (comments == null || comments.isEmpty()) return excluded;

        Map<Integer, List<Integer>> children = new HashMap<>();
        for (Comment c : comments.values()) {
            if (c.parentComment != null) {
                children.computeIfAbsent(c.parentComment, k -> new ArrayList<>()).add(c.id);
            }
        }

        for (Comment c : comments.values()) {
            if (isBad(c, mode)) {
                dfs(c.id, children, excluded);
            }
        }
        return excluded;
    }

    private static void dfs(int cur, Map<Integer, List<Integer>> children, Set<Integer> excluded) {
        if (excluded.contains(cur)) return;
        excluded.add(cur);
        for (int child : children.getOrDefault(cur, Collections.emptyList())) {
            dfs(child, children, excluded);
        }
    }

    /* ------------------------------ Part 2 ------------------------------ */

    public static Set<Integer> getCommentsToExclude(Map<Integer, Comment> comments, Mode mode, long target) {
        Set<Integer> excluded = new HashSet<>();
        if (comments == null || comments.isEmpty()) return excluded;

        Map<Integer, List<Integer>> children = new HashMap<>();
        List<Integer> roots = new ArrayList<>();
        for (Comment c : comments.values()) {
            Integer p = c.parentComment;
            if (p != null && comments.containsKey(p)) {
                children.computeIfAbsent(p, k -> new ArrayList<>()).add(c.id);
            } else {
                roots.add(c.id);                         // root (no parent, or parent missing)
            }
        }

        Map<Integer, Long> sum = new HashMap<>();
        for (int root : roots) {
            dfsSum(root, comments, children, sum);
        }
        for (int root : roots) {
            dfsHide(root, false, comments, children, sum, mode, target, excluded);
        }
        return excluded;
    }

    private static long dfsSum(int cur, Map<Integer, Comment> comments,
                               Map<Integer, List<Integer>> children, Map<Integer, Long> sum) {
        long s = comments.get(cur).score;
        for (int child : children.getOrDefault(cur, Collections.emptyList())) {
            s += dfsSum(child, comments, children, sum);
        }
        sum.put(cur, s);
        return s;
    }

    private static void dfsHide(int cur, boolean parentHidden, Map<Integer, Comment> comments,
                                Map<Integer, List<Integer>> children, Map<Integer, Long> sum,
                                Mode mode, long target, Set<Integer> excluded) {
        boolean hidden = parentHidden || (isBad(comments.get(cur), mode) && sum.get(cur) <= target);
        if (hidden) {
            excluded.add(cur);
        }
        for (int child : children.getOrDefault(cur, Collections.emptyList())) {
            dfsHide(child, hidden, comments, children, sum, mode, target, excluded);
        }
    }

    private static boolean isBad(Comment c, Mode mode) {
        return mode == Mode.CAT_PERSON ? c.dog : c.cat;
    }

    /* ============================== Tests ============================== */

    public static void main(String[] args) {
        // The prompt's example: 11 is about dogs, 2's parent (1) is missing.
        Map<Integer, Comment> example = new HashMap<>();
        add(example, 0, null, "Look! A cute baby elephant taking a nap!", false, false);
        add(example, 2, 1, "I agree!", false, false);
        add(example, 11, 0, "Almost as cute as my poodle!", false, true);
        expect("example CAT_PERSON", getCommentsToExclude(example, Mode.CAT_PERSON), Set.of(11));
        expect("example DOG_PERSON", getCommentsToExclude(example, Mode.DOG_PERSON), Set.of());

        // 5 (dog) → 6 → 7 → 8
        Map<Integer, Comment> chain = new HashMap<>();
        add(chain, 5, null, "my dog", false, true);
        add(chain, 6, 5, "nice", false, false);
        add(chain, 7, 6, "agree", false, false);
        add(chain, 8, 7, "+1", false, false);
        expect("chain CAT_PERSON", getCommentsToExclude(chain, Mode.CAT_PERSON), Set.of(5, 6, 7, 8));
        expect("chain DOG_PERSON", getCommentsToExclude(chain, Mode.DOG_PERSON), Set.of());

        // Nested bad comments, a both-animals comment, and a sibling that stays.
        //   1 ── 2 (cat) ── 3 (cat) ── 4
        //    └── 5 ── 6 (cat + dog)
        //    └── 7
        Map<Integer, Comment> tree = new HashMap<>();
        add(tree, 1, null, "root", false, false);
        add(tree, 2, 1, "cat", true, false);
        add(tree, 3, 2, "another cat", true, false);
        add(tree, 4, 3, "reply", false, false);
        add(tree, 5, 1, "hmm", false, false);
        add(tree, 6, 5, "cats and dogs", true, true);
        add(tree, 7, 1, "unrelated", false, false);
        expect("tree DOG_PERSON", getCommentsToExclude(tree, Mode.DOG_PERSON), Set.of(2, 3, 4, 6));
        expect("tree CAT_PERSON", getCommentsToExclude(tree, Mode.CAT_PERSON), Set.of(6));

        expect("empty input", getCommentsToExclude(new HashMap<>(), Mode.CAT_PERSON), Set.of());

        // Long reply chain (recursion depth 1,000 is fine; ~10^5 would overflow).
        Map<Integer, Comment> deep = new HashMap<>();
        add(deep, 0, null, "dog", false, true);
        for (int i = 1; i < 1_000; i++) add(deep, i, i - 1, "reply", false, false);
        expect("deep chain size", getCommentsToExclude(deep, Mode.CAT_PERSON).size(), 1_000);

        /* ---------------------------- Part 2 ---------------------------- */

        // 5 (dog, 3) → 6 (2) → 7 (1) → 8 (1): group sum = 7
        Map<Integer, Comment> scored = new HashMap<>();
        add(scored, 5, null, "my dog", false, true, 3);
        add(scored, 6, 5, "nice", false, false, 2);
        add(scored, 7, 6, "agree", false, false, 1);
        add(scored, 8, 7, "+1", false, false, 1);
        expect("P2 sum 7 > target 6 → visible", getCommentsToExclude(scored, Mode.CAT_PERSON, 6), Set.of());
        expect("P2 sum 7 = target 7 → hidden", getCommentsToExclude(scored, Mode.CAT_PERSON, 7), Set.of(5, 6, 7, 8));
        expect("P2 sum 7 < target 10 → hidden", getCommentsToExclude(scored, Mode.CAT_PERSON, 10), Set.of(5, 6, 7, 8));

        // Popular outer dog group stays, small dog reply inside it is still hidden.
        //   1 (dog, 50) ── 2 (10) ── 3 (dog, 1) ── 4 (1)
        //               └── 5 (5)
        Map<Integer, Comment> nested = new HashMap<>();
        add(nested, 1, null, "dog thread", false, true, 50);
        add(nested, 2, 1, "reply", false, false, 10);
        add(nested, 3, 2, "my dog too", false, true, 1);
        add(nested, 4, 3, "cute", false, false, 1);
        add(nested, 5, 1, "reply", false, false, 5);
        expect("P2 nested: outer visible, inner hidden", getCommentsToExclude(nested, Mode.CAT_PERSON, 20), Set.of(3, 4));
        expect("P2 nested: target MAX = Part 1",
                getCommentsToExclude(nested, Mode.CAT_PERSON, Long.MAX_VALUE),
                getCommentsToExclude(nested, Mode.CAT_PERSON));

        // Missing parent (like the prompt's comment 2) is a root.
        Map<Integer, Comment> orphan = new HashMap<>();
        add(orphan, 2, 1, "dog", false, true, 5);
        add(orphan, 3, 2, "reply", false, false, 5);
        expect("P2 orphan root, sum 10 <= 10", getCommentsToExclude(orphan, Mode.CAT_PERSON, 10), Set.of(2, 3));

        // Long reply chain (recursion depth 1,000).
        Map<Integer, Comment> deepScored = new HashMap<>();
        add(deepScored, 0, null, "dog", false, true, 1);
        for (int i = 1; i < 1_000; i++) add(deepScored, i, i - 1, "reply", false, false, 1);
        expect("P2 deep chain visible (sum 1000 > 999)",
                getCommentsToExclude(deepScored, Mode.CAT_PERSON, 999).size(), 0);
        expect("P2 deep chain hidden (sum 1000 <= 1000)",
                getCommentsToExclude(deepScored, Mode.CAT_PERSON, 1_000).size(), 1_000);
    }

    private static void add(Map<Integer, Comment> m, int id, Integer parent, String body, boolean cat, boolean dog) {
        m.put(id, new Comment(id, parent, body, cat, dog));
    }

    private static void add(Map<Integer, Comment> m, int id, Integer parent, String body,
                            boolean cat, boolean dog, long score) {
        m.put(id, new Comment(id, parent, body, cat, dog, score));
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
