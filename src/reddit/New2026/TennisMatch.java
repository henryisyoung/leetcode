package reddit.New2026;

import java.util.*;

/*
================================================================================
  TennisMatch — Reddit onsite (2026), OOD / state-machine, 3-part progression
================================================================================

  Scoring rules
    Points map to names:   0=Love   1=15   2=30   3=40
    Win a game:            reach ≥ 4 points AND lead by ≥ 2
    Simplified deuce:      when BOTH players reach a tied score ≥ 3, snap
                           back to 3-3. This caps the internal representation
                           at ≤ 4 for any live game, so we never track 5-5,
                           6-6, etc.
    Once won:              add_score becomes an error — no post-game points.

  --------------------------------------------------------------------------
  HOW TO READ THIS FILE
  --------------------------------------------------------------------------
    Each part gets its own class, holding only what that part needs. Don't
    start from the final answer — the signal is in noticing WHAT FORCES the
    next layer.

      TennisGame     point-level state machine        Part 1
      Q2TennisSet    HAS-A TennisGame, counts games   Part 2
      Q3TennisSet    Q2 + sides + gamesPlayed         Part 3
      Q4StandardSet  Q2 + win-by-2 + HAS-A TieBreak   Part 4

    Q2, Q3 and Q4 all compose the SAME `TennisGame`, untouched. That reuse is
    the whole point of Part 3 — see the Part 3 notes. Q3TennisSet duplicates
    Q2TennisSet's game bookkeeping on purpose so each part reads standalone;
    the only diff is the two side fields and one swap call. Q4StandardSet
    duplicates it again for the same reason.

  --------------------------------------------------------------------------
  Part 1 — TennisGame                                  → class TennisGame
  --------------------------------------------------------------------------
    Live states are all IMPLICIT in (p1, p2, winner) — no enum needed:
      In-progress   winner=="" AND not one of the below
      Deuce         winner=="" AND p1==p2==3
      Advantage     winner=="" AND max==4, min==3  → leader is whoever is 4
      Finished      winner != ""
    Deriving state instead of storing it means there's no second source of
    truth to keep in sync.

    ⚠ GUARD ORDER in addScore is load-bearing:
        1. game-over guard        ┐ both MUST precede the increment
        2. unknown-player guard   ┘
        3. increment
        4. win check              (≥ 4 AND lead ≥ 2)
        5. else deuce reset       (tied AND ≥ 3 → snap to 3-3)
      Steps 1–2 before 3 matters: the increment is a `scores.merge(...)`,
      which INSERTS an unknown player into the map instead of rejecting them.
      A guard placed after it has already corrupted the state.
      Steps 4 and 5 are mutually exclusive (|p1−p2| ≥ 2 vs p1 == p2), so
      their relative order is not actually load-bearing — win-first just
      reads better. Don't claim otherwise in the interview.

  --------------------------------------------------------------------------
  Part 2 — a set of games                              → class Q2TennisSet
  --------------------------------------------------------------------------
    ⚠ Composition, not inheritance. TennisSet HAS-A TennisGame. Inheriting
      would leak game-level API (getHumanScore, the deuce reset) onto the set,
      where "Deuce" is meaningless. This is the main OOD signal in the round.

    Single entry point: set.addScore() forwards to currentGame. When
    currentGame reports a winner, bump gamesWon, check the set win, else
    replace currentGame with a fresh one (O(1) — no reset method needed,
    just a new object).

    firstToGames is a constructor knob, so one class models first-to-3,
    first-to-6, etc.

    ⚠ IT IS A BARE THRESHOLD — there is no 2-game margin. With
      firstToGames = 6, a score of 6:5 ends the set here, which REAL TENNIS
      WOULD NOT ALLOW: you'd play on to 7:5, or to a tiebreak at 6:6. That
      is the simplification the prompt asked for rather than an oversight,
      and there's a test in main() pinning it so it reads as a decision.

      Making it real is Part 4. The set rule turns out to be the SAME
      predicate as the game rule one level down — "reach ≥ N and lead by
      ≥ 2" — with 4 swapped for 6:
            won >= firstToGames && won - lost >= 2
      and the 6:6 tiebreak is the structural twin of the simplified deuce
      reset: both exist only to stop an unbounded race. Win-by-2 with no
      tiebreak is a historical "advantage set", which can run 8:6, 9:7, …
      indefinitely (Isner–Mahut ended 70:68).

  --------------------------------------------------------------------------
  Part 3 — side swaps between games                    → class Q3TennisSet
  --------------------------------------------------------------------------
    ⚠ THE POINT OF THIS PART: sides are metadata ABOUT players, not state OF
      the game — scoring logic never consults them. So TennisGame needs ZERO
      changes, which is exactly the payoff of having composed rather than
      inherited in Part 2. (Q3TennisSet below reuses Part 1's TennisGame
      verbatim; that's the proof, not just the claim.)

    Sides live on the set, which already owns the between-games seam where a
    swap physically happens.

    Standard rule: swap after every odd game (1, 3, 5, …). One `gamesPlayed`
    counter drives the trigger, so "every game" or "also at end of set"
    is a one-line change.

  --------------------------------------------------------------------------
  Part 4 — Requirement B, the real set rule         → class Q4StandardSet
  --------------------------------------------------------------------------
    First to 6 games takes the set, but only with a 2-game lead. At 6:6 a
    tie-break decides it. This is Part 2's bare threshold made real:

        6:4  set over        6:5  play on         7:5  set over
        6:6  tie-break       7:6  set over, decided by the tie-break

    ⚠ THE SAME PREDICATE, THREE TIMES OVER. "reach ≥ N and lead by ≥ 2" is
      the game rule (N = 4), the set rule (N = 6) AND the tie-break rule
      (N = 7). Saying that out loud is most of the answer. It is written out
      three times here rather than factored into a shared wins(a, b, n),
      because this file's house rule is that each part reads standalone —
      but naming the duplication is free, so name it.

    ⚠ WHY THE TIE-BREAK CANNOT JUST BE A TennisGame, even though both are
      "first to N, win by 2" races:
        - TennisGame's deuce reset CAPS the live score at 4. A tie-break has
          no cap — 12:10 is a legal tie-break score — and snapping 6-6 back
          to 3-3 would erase exactly the state that matters.
        - The display differs: a tie-break is scored in plain points ("6-5"),
          never Love/15/30/40, and has no Deuce/Advantage vocabulary.
      So Part 4 adds a small TieBreak class rather than generalising
      TennisGame, which stays byte-identical for the fourth part running.

    The tie-break counts as ONE game, so the set ends 7:6 no matter whether
    the tie-break itself went 7:5 or 15:13. Two different scores, two
    different scopes — don't leak the tie-break's points into getSetScore().

    Ordering inside addScore: the set-win test comes before the 6:6 test. At
    6:6 the win test is false anyway (lead 0), so the order is NOT
    load-bearing — it just reads the way you'd say it. Don't oversell it;
    swapping the two branches passes every test in this file.

    ⚠ The set-over guard is DIAGNOSTICS, not correctness. Deleting it changes
      no outcome, because whichever layer is underneath — the finished
      TennisGame, or the finished TieBreak — throws IllegalStateException
      itself, before touching any state. What it buys is a message at the
      right abstraction level:
            with the guard     "Set already over"
            without it         "Game already over" / "Tie-break already over"
      The set being over is the real reason; the game underneath is an
      implementation detail. Part 2 and Part 3 have the same property. Know
      which of your guards are load-bearing and which are just good manners.

    Orthogonal to Part 3: sides and tie-breaks never interact here. Real
    tennis also changes ends every 6 POINTS within a tie-break, which is a
    `tieBreak.pointsPlayed() % 6 == 0` trigger layered on Q3's odd-game
    rule — one more counter, no new structure.

    ⚠ firstToGames stays a knob, but win-by-2 makes 1 degenerate: at 1:0 the
      lead is only 1, so "first to 1" really means "1:1, then tie-break".
      Pass ≥ 2.

  --------------------------------------------------------------------------
  Complexity summary   (fixed 2-player schema → all live state is O(1))
  --------------------------------------------------------------------------
    TennisGame
      addScore          O(1)   2 guards + increment + ≤ 4 int compares
      getScore          O(1)
      getResult         O(1)
      getHumanScore     O(1)   at most one map lookup + array index
      space             O(1)   2-entry score map + winner string

    Q2TennisSet
      addScore          O(1)   delegate + at most one game reset
      getSetScore       O(1)
      getSetWinner      O(1)
      space             O(1)   gamesWon + the current TennisGame

    Q3TennisSet         same, plus
      getSides          O(1)   copy of a 2-entry map
      swapSides         O(1)   one tmp swap

    Q4StandardSet       same as Q2, plus
      addScore          O(1)   one extra branch: game layer or tie-break layer
      tieBreak          O(1)   nullable accessor, null until 6:6
      space             O(1)   + one TieBreak once 6:6 is reached

    TieBreak
      addScore          O(1)   2 guards + increment + 4 int compares, no reset
      getScore          O(1)
      getHumanScore     O(1)   plain point pair, no name table

    Full set of G games          O(G) time to replay every point,
                                 O(1) live state, O(G) if history is kept.
================================================================================
*/
public class TennisMatch {

    private TennisMatch() {}   // namespace only — each part has its own class

    /* ============================================================
       Part 1 — TennisGame, the point-level state machine.
                Reused UNCHANGED by both Part 2 and Part 3.
       ============================================================ */
    public static final class TennisGame {
        private static final String[] SCORE_NAMES = {"Love", "15", "30", "40"};

        private final String player1, player2;
        private final Map<String, Integer> scores = new LinkedHashMap<>();
        private String winner = "";

        /** Time O(1) — seed two score entries. */
        public TennisGame(String player1, String player2) {
            this.player1 = player1;
            this.player2 = player2;
            scores.put(player1, 0);
            scores.put(player2, 0);
        }

        /** Time O(1) — 2 guards, 1 increment, ≤ 4 int compares (+ maybe a 2-key reset).
         *  Both guards must run BEFORE the merge, which would otherwise insert
         *  an unknown player into `scores`. */
        public void addScore(String player) {
            if (!winner.isEmpty())           throw new IllegalStateException("Game already over");
            if (!scores.containsKey(player)) throw new IllegalArgumentException("Unknown player: " + player);

            scores.merge(player, 1, Integer::sum);
            int p1 = scores.get(player1), p2 = scores.get(player2);

            if (p1 >= 4 && p1 - p2 >= 2)      winner = player1;
            else if (p2 >= 4 && p2 - p1 >= 2) winner = player2;
            else if (p1 >= 3 && p1 == p2) {                 // deuce reset (implies p2 >= 3)
                scores.put(player1, 3);
                scores.put(player2, 3);
            }
        }

        /** Time O(1). */ public int[] getScore()   { return new int[]{scores.get(player1), scores.get(player2)}; }
        /** Time O(1). */ public String getResult() { return winner; }

        /** Time O(1) — two int compares + one map lookup / array index. */
        public String getHumanScore() {
            if (!winner.isEmpty()) return "Game " + winner;
            int p1 = scores.get(player1), p2 = scores.get(player2);
            if (p1 >= 3 && p2 >= 3) {
                if (p1 == p2) return "Deuce";
                return "Advantage " + (p1 > p2 ? player1 : player2);
            }
            return SCORE_NAMES[p1] + "-" + SCORE_NAMES[p2];
        }
    }

    /* ============================================================
       Part 2 — a set of games. Composes TennisGame; no sides yet.
       ============================================================ */
    public static final class Q2TennisSet {
        private final String player1, player2;
        private final int    firstToGames;
        private final Map<String, Integer> gamesWon = new LinkedHashMap<>();
        private TennisGame currentGame;
        private String setWinner = "";

        /** Time O(1) — seed the map and the first TennisGame.
         *  @param firstToGames games needed to take the set. A bare threshold with
         *         no 2-game margin, so a first-to-6 set ends at 6:5. */
        public Q2TennisSet(String player1, String player2, int firstToGames) {
            this.player1      = player1;
            this.player2      = player2;
            this.firstToGames = firstToGames;
            gamesWon.put(player1, 0);
            gamesWon.put(player2, 0);
            currentGame = new TennisGame(player1, player2);
        }

        /** Time O(1) — delegate to currentGame; at most one game-end bookkeeping step. */
        public void addScore(String player) {
            if (!setWinner.isEmpty()) throw new IllegalStateException("Set already over");

            currentGame.addScore(player);

            String gameWinner = currentGame.getResult();
            if (gameWinner.isEmpty()) return;                // game still in progress

            gamesWon.merge(gameWinner, 1, Integer::sum);

            // Bare threshold: no 2-game margin, so 6:5 ends a first-to-6 set.
            if (gamesWon.get(gameWinner) >= firstToGames) {
                setWinner = gameWinner;
                return;                                      // keep the finished game visible
            }
            currentGame = new TennisGame(player1, player2);   // fresh game
        }

        /** Time O(1). */ public int[] getSetScore()      { return new int[]{gamesWon.get(player1), gamesWon.get(player2)}; }
        /** Time O(1). */ public String getSetWinner()    { return setWinner; }
        /** Time O(1). */ public TennisGame currentGame() { return currentGame; }
    }

    /* ============================================================
       Part 3 — Q2 + side swaps. Note that TennisGame above is
                reused as-is: sides never reach the game layer.
       ============================================================ */
    public static final class Q3TennisSet {
        private final String player1, player2;
        private final int    firstToGames;
        private final Map<String, Integer> gamesWon = new LinkedHashMap<>();
        private TennisGame currentGame;
        private String setWinner = "";

        /* --- the only new state vs Q2TennisSet --- */
        private final Map<String, String> sides = new LinkedHashMap<>();
        private int gamesPlayed = 0;

        /** Time O(1) — seed the maps and the first TennisGame.
         *  @param firstToGames games needed to take the set. A bare threshold with
         *         no 2-game margin, so a first-to-6 set ends at 6:5. */
        public Q3TennisSet(String player1, String player2, int firstToGames) {
            this.player1      = player1;
            this.player2      = player2;
            this.firstToGames = firstToGames;
            gamesWon.put(player1, 0);
            gamesWon.put(player2, 0);
            currentGame = new TennisGame(player1, player2);
            sides.put(player1, "near");
            sides.put(player2, "far");
        }

        /** Time O(1) — identical to Q2 apart from the counter and the swap call. */
        public void addScore(String player) {
            if (!setWinner.isEmpty()) throw new IllegalStateException("Set already over");

            currentGame.addScore(player);

            String gameWinner = currentGame.getResult();
            if (gameWinner.isEmpty()) return;

            gamesWon.merge(gameWinner, 1, Integer::sum);
            gamesPlayed++;

            // Bare threshold: no 2-game margin, so 6:5 ends a first-to-6 set.
            if (gamesWon.get(gameWinner) >= firstToGames) {
                setWinner = gameWinner;
                return;
            }
            if (gamesPlayed % 2 == 1) swapSides();            // standard "odd game" swap
            currentGame = new TennisGame(player1, player2);
        }

        /** Time O(1) — one tmp swap of a 2-entry map. */
        private void swapSides() {
            String tmp = sides.get(player1);
            sides.put(player1, sides.get(player2));
            sides.put(player2, tmp);
        }

        /** Time O(1). */ public int[] getSetScore()      { return new int[]{gamesWon.get(player1), gamesWon.get(player2)}; }
        /** Time O(1). */ public String getSetWinner()    { return setWinner; }
        /** Time O(1). */ public TennisGame currentGame() { return currentGame; }
        /** Time O(1) — copy of a 2-entry map. */
        public Map<String, String> getSides() { return new LinkedHashMap<>(sides); }
    }

    /* ============================================================
       Part 4 — the tie-break. A "first to N, win by 2" race like
                TennisGame, but with NO deuce reset (so it can reach
                12:10) and plain-point display. That's why it is its
                own class instead of a reconfigured TennisGame.
       ============================================================ */
    public static final class TieBreak {
        private final String player1, player2;
        private final int    target;
        private final Map<String, Integer> points = new LinkedHashMap<>();
        private String winner = "";

        /** @param target points needed to take the tie-break, still subject to a 2-point lead. */
        public TieBreak(String player1, String player2, int target) {
            this.player1 = player1;
            this.player2 = player2;
            this.target  = target;
            points.put(player1, 0);
            points.put(player2, 0);
        }

        /** Time O(1). Same guard-before-merge order as TennisGame, for the same reason. */
        public void addScore(String player) {
            if (!winner.isEmpty())           throw new IllegalStateException("Tie-break already over");
            if (!points.containsKey(player)) throw new IllegalArgumentException("Unknown player: " + player);

            points.merge(player, 1, Integer::sum);
            int p1 = points.get(player1), p2 = points.get(player2);

            // Third appearance of "reach >= N and lead by >= 2". No deuce reset
            // here: capping the score would make 12:10 unrepresentable.
            if (p1 >= target && p1 - p2 >= 2)      winner = player1;
            else if (p2 >= target && p2 - p1 >= 2) winner = player2;
        }

        /** Time O(1). */ public int[] getScore()     { return new int[]{points.get(player1), points.get(player2)}; }
        /** Time O(1). */ public String getResult()   { return winner; }
        /** Time O(1). */ public int pointsPlayed()   { return points.get(player1) + points.get(player2); }

        /** Time O(1) — plain points, never Love/15/30/40. */
        public String getHumanScore() {
            if (!winner.isEmpty()) return "Tie-break " + winner;
            return points.get(player1) + "-" + points.get(player2);
        }
    }

    /* ============================================================
       Part 4 — Requirement B: first to 6 games with a 2-game lead,
                tie-break at 6:6. Composes TennisGame (unchanged)
                and, once 6:6 arrives, a TieBreak.
       ============================================================ */
    public static final class Q4StandardSet {
        private static final int STANDARD_GAMES       = 6;
        private static final int STANDARD_TIE_BREAK   = 7;

        private final String player1, player2;
        private final int    firstToGames, tieBreakPoints;
        private final Map<String, Integer> gamesWon = new LinkedHashMap<>();
        private TennisGame currentGame;
        private TieBreak   tieBreak;                  // null until the set reaches 6:6
        private String     setWinner = "";

        /** The real thing: 6 games, 2-game lead, 7-point tie-break at 6:6. */
        public Q4StandardSet(String player1, String player2) {
            this(player1, player2, STANDARD_GAMES, STANDARD_TIE_BREAK);
        }

        /** Time O(1). @param firstToGames pass >= 2 — win-by-2 makes 1 degenerate. */
        public Q4StandardSet(String player1, String player2, int firstToGames, int tieBreakPoints) {
            this.player1        = player1;
            this.player2        = player2;
            this.firstToGames   = firstToGames;
            this.tieBreakPoints = tieBreakPoints;
            gamesWon.put(player1, 0);
            gamesWon.put(player2, 0);
            currentGame = new TennisGame(player1, player2);
        }

        /**
         * Time O(1). Once the tie-break is live, a "score" is a tie-break POINT
         * rather than a game point, so the two layers never run at once.
         */
        public void addScore(String player) {
            if (!setWinner.isEmpty()) throw new IllegalStateException("Set already over");

            if (tieBreak != null) {
                tieBreak.addScore(player);
                String tbWinner = tieBreak.getResult();
                if (tbWinner.isEmpty()) return;

                gamesWon.merge(tbWinner, 1, Integer::sum);   // the tie-break is worth ONE game
                setWinner = tbWinner;                        // ...so the set ends 7:6
                return;
            }

            currentGame.addScore(player);

            String gameWinner = currentGame.getResult();
            if (gameWinner.isEmpty()) return;

            gamesWon.merge(gameWinner, 1, Integer::sum);
            int won  = gamesWon.get(gameWinner);
            int lost = gamesWon.get(gameWinner.equals(player1) ? player2 : player1);

            if (won >= firstToGames && won - lost >= 2) {     // 6:4 and 7:5, but not 6:5
                setWinner = gameWinner;
                return;                                       // keep the finished game visible
            }
            if (won == firstToGames && lost == firstToGames) {
                tieBreak = new TieBreak(player1, player2, tieBreakPoints);
                return;                                       // games are done; points decide it
            }
            currentGame = new TennisGame(player1, player2);
        }

        /** Time O(1). Games only — a tie-break contributes 1, never its own points. */
        public int[] getSetScore()   { return new int[]{gamesWon.get(player1), gamesWon.get(player2)}; }
        /** Time O(1). */ public String getSetWinner()    { return setWinner; }
        /** Time O(1). */ public TennisGame currentGame() { return currentGame; }
        /** Time O(1). */ public boolean isTieBreak()     { return tieBreak != null; }
        /** Time O(1) — null before 6:6. */
        public TieBreak tieBreak() { return tieBreak; }

        /** Time O(1) — set score plus, if we got there, the tie-break's own points. */
        public String getHumanScore() {
            int p1 = gamesWon.get(player1), p2 = gamesWon.get(player2);
            String games = p1 + "-" + p2;
            if (!setWinner.isEmpty()) return "Set " + setWinner + " (" + games + ")";
            if (tieBreak != null)     return games + " tie-break " + tieBreak.getHumanScore();
            return games;
        }
    }

    /* ============================================================
       Tests — one block per part
       ============================================================ */
    public static void main(String[] args) {

        /* -------------------- Part 1: TennisGame -------------------- */

        // Straight 4-0
        TennisGame g = new TennisGame("A", "B");
        for (int i = 0; i < 4; i++) g.addScore("A");
        expect("P1: 4-0 score",  toList(g.getScore()), Arrays.asList(4, 0));
        expect("P1: 4-0 result", g.getResult(),        "A");
        expect("P1: 4-0 human",  g.getHumanScore(),    "Game A");

        // Straight 4-2 (A,A,B,B,A,A)
        g = new TennisGame("A", "B");
        g.addScore("A"); g.addScore("A");
        g.addScore("B"); g.addScore("B");
        g.addScore("A"); g.addScore("A");
        expect("P1: 4-2 result", g.getResult(),        "A");
        expect("P1: 4-2 score",  toList(g.getScore()), Arrays.asList(4, 2));

        // Human score at 40-15
        g = new TennisGame("A", "B");
        g.addScore("A"); g.addScore("A"); g.addScore("A");
        g.addScore("B");
        expect("P1: 40-15 human", g.getHumanScore(), "40-15");

        // Deuce cycle: 3-3 → Adv A → reset to 3-3 → Adv B → B wins
        g = new TennisGame("A", "B");
        for (int i = 0; i < 3; i++) { g.addScore("A"); g.addScore("B"); }
        expect("P1: first deuce",     g.getHumanScore(),     "Deuce");
        g.addScore("A");
        expect("P1: advantage A",     g.getHumanScore(),     "Advantage A");
        g.addScore("B");
        expect("P1: reset after 4-4", toList(g.getScore()),  Arrays.asList(3, 3));
        expect("P1: deuce again",     g.getHumanScore(),     "Deuce");
        g.addScore("B");
        expect("P1: advantage B",     g.getHumanScore(),     "Advantage B");
        g.addScore("B");
        expect("P1: B wins after deuce cycle", g.getResult(), "B");
        expect("P1: winner human",    g.getHumanScore(),     "Game B");

        // Winning FROM advantage: 4-3 then leader scores → 5-3 is a win,
        // and must not be mistaken for a deuce reset.
        g = new TennisGame("A", "B");
        for (int i = 0; i < 3; i++) { g.addScore("A"); g.addScore("B"); }
        g.addScore("A");                                     // 4-3, advantage A
        g.addScore("A");                                     // 5-3, game A
        expect("P1: wins from advantage", g.getResult(),      "A");
        expect("P1: 5-3 recorded",  toList(g.getScore()),     Arrays.asList(5, 3));

        // Post-game guard
        final TennisGame gOver = new TennisGame("A", "B");
        for (int i = 0; i < 4; i++) gOver.addScore("A");
        expect("P1: throws after game over",
                throwsRuntime(() -> gOver.addScore("A"), IllegalStateException.class), true);

        // Unknown player guard — must reject WITHOUT inserting into the map
        final TennisGame gUnknown = new TennisGame("A", "B");
        expect("P1: throws unknown player",
                throwsRuntime(() -> gUnknown.addScore("C"), IllegalArgumentException.class), true);
        expect("P1: unknown player left no trace",
                toList(gUnknown.getScore()), Arrays.asList(0, 0));

        /* -------------------- Part 2: Q2TennisSet -------------------- */

        // Set 3-1 (A, B, A, A)
        Q2TennisSet s = new Q2TennisSet("A", "B", 3);
        for (int i = 0; i < 4; i++) s.addScore("A");
        expect("P2: set 1-0", toList(s.getSetScore()), Arrays.asList(1, 0));
        for (int i = 0; i < 4; i++) s.addScore("B");
        expect("P2: set 1-1", toList(s.getSetScore()), Arrays.asList(1, 1));
        for (int i = 0; i < 4; i++) s.addScore("A");
        for (int i = 0; i < 4; i++) s.addScore("A");
        expect("P2: set 3-1",    toList(s.getSetScore()), Arrays.asList(3, 1));
        expect("P2: set winner", s.getSetWinner(),        "A");

        // Game resets between games
        Q2TennisSet s2 = new Q2TennisSet("A", "B", 3);
        for (int i = 0; i < 4; i++) s2.addScore("A");
        expect("P2: current game reset to 0-0",
                toList(s2.currentGame().getScore()), Arrays.asList(0, 0));
        expect("P2: set not over yet", s2.getSetWinner(), "");

        // A game that itself goes to deuce still counts as exactly one game
        Q2TennisSet s3 = new Q2TennisSet("A", "B", 2);
        for (int i = 0; i < 3; i++) { s3.addScore("A"); s3.addScore("B"); }   // deuce
        s3.addScore("A"); s3.addScore("A");                                   // game A
        expect("P2: deuce game counts once", toList(s3.getSetScore()), Arrays.asList(1, 0));

        // firstToGames is a real knob
        Q2TennisSet quick = new Q2TennisSet("A", "B", 1);
        for (int i = 0; i < 4; i++) quick.addScore("A");
        expect("P2: firstToGames=1 ends the set", quick.getSetWinner(), "A");

        // Pins the simplification instead of leaving it looking like a bug:
        // firstToGames carries no 2-game margin, so a first-to-6 set ends at
        // 6:5, where real tennis would play on to 7:5 or a 6:6 tiebreak.
        Q2TennisSet noMargin = new Q2TennisSet("A", "B", 6);
        for (int game = 0; game < 5; game++) {                              // → 5:5
            for (int i = 0; i < 4; i++) noMargin.addScore("A");
            for (int i = 0; i < 4; i++) noMargin.addScore("B");
        }
        expect("P2: 5-5 has no winner yet", noMargin.getSetWinner(), "");
        for (int i = 0; i < 4; i++) noMargin.addScore("A");                 // → 6:5
        expect("P2: 6-5 score", toList(noMargin.getSetScore()), Arrays.asList(6, 5));
        expect("P2: 6-5 ends the set here (real tennis would not)",
                noMargin.getSetWinner(), "A");

        // Cannot score after set over
        final Q2TennisSet done = new Q2TennisSet("A", "B", 1);
        for (int i = 0; i < 4; i++) done.addScore("A");
        expect("P2: throws after set over",
                throwsRuntime(() -> done.addScore("B"), IllegalStateException.class), true);

        /* ------------- Part 3: Q3TennisSet (swap after games 1, 3, 5, …) ------------- */

        Q3TennisSet ss = new Q3TennisSet("A", "B", 3);
        Map<String, String> nearFar = new LinkedHashMap<>();
        nearFar.put("A", "near"); nearFar.put("B", "far");
        Map<String, String> farNear = new LinkedHashMap<>();
        farNear.put("A", "far");  farNear.put("B", "near");

        expect("P3: initial sides", ss.getSides(), nearFar);
        for (int i = 0; i < 4; i++) ss.addScore("A");           // game 1 → swap
        expect("P3: swap after game 1",    ss.getSides(), farNear);
        for (int i = 0; i < 4; i++) ss.addScore("B");           // game 2 → no swap
        expect("P3: no swap after game 2", ss.getSides(), farNear);
        for (int i = 0; i < 4; i++) ss.addScore("A");           // game 3 → swap
        expect("P3: swap after game 3",    ss.getSides(), nearFar);

        // getSides hands back a copy — mutating it must not corrupt the set
        Map<String, String> leaked = ss.getSides();
        leaked.put("A", "bogus");
        expect("P3: getSides returns a defensive copy", ss.getSides(), nearFar);

        // Sides are pure metadata: Part 3's set scores identically to Part 2's.
        Q2TennisSet q2 = new Q2TennisSet("A", "B", 3);
        Q3TennisSet q3 = new Q3TennisSet("A", "B", 3);
        String[] points = {"A","A","A","A", "B","B","B","B", "A","B","A","B","A","B","A","A", "A","A","A","A"};
        for (String p : points) { q2.addScore(p); q3.addScore(p); }
        expect("P3: same set score as Part 2",
                toList(q3.getSetScore()), toList(q2.getSetScore()));
        expect("P3: same set winner as Part 2",
                q3.getSetWinner(), q2.getSetWinner());

        /* ------------- Part 4: Q4StandardSet (6 games, lead 2, tie-break at 6:6) ------------- */

        // 6:4 — the ordinary way to take a set
        Q4StandardSet clean = new Q4StandardSet("A", "B");
        for (int i = 0; i < 4; i++) winGame(clean, "A");
        for (int i = 0; i < 4; i++) winGame(clean, "B");
        expect("P4: 4-4 no winner", clean.getSetWinner(), "");
        winGame(clean, "A"); winGame(clean, "A");
        expect("P4: 6-4 score",  toList(clean.getSetScore()), Arrays.asList(6, 4));
        expect("P4: 6-4 wins the set", clean.getSetWinner(), "A");

        // 6:5 must NOT end the set — this is the exact case Part 2 gets wrong
        Q4StandardSet margin = new Q4StandardSet("A", "B");
        for (int i = 0; i < 5; i++) { winGame(margin, "A"); winGame(margin, "B"); }   // 5:5
        winGame(margin, "A");                                                          // 6:5
        expect("P4: 6-5 score", toList(margin.getSetScore()), Arrays.asList(6, 5));
        expect("P4: 6-5 does NOT end the set", margin.getSetWinner(), "");
        expect("P4: 6-5 is not a tie-break either", margin.isTieBreak(), false);
        winGame(margin, "A");                                                          // 7:5
        expect("P4: 7-5 score", toList(margin.getSetScore()), Arrays.asList(7, 5));
        expect("P4: 7-5 wins the set", margin.getSetWinner(), "A");

        // Same points, opposite verdicts — Part 2 stops at 6:5, Part 4 plays on
        Q2TennisSet bare  = new Q2TennisSet("A", "B", 6);
        Q4StandardSet real = new Q4StandardSet("A", "B");
        for (int i = 0; i < 5; i++) { winGame(bare, "A"); winGame(bare, "B"); }
        for (int i = 0; i < 5; i++) { winGame(real, "A"); winGame(real, "B"); }
        winGame(bare, "A"); winGame(real, "A");
        expect("P4: Part 2 ends at 6-5", bare.getSetWinner(), "A");
        expect("P4: Part 4 does not",    real.getSetWinner(), "");

        // 6:6 → tie-break
        Q4StandardSet tb = new Q4StandardSet("A", "B");
        for (int i = 0; i < 6; i++) { winGame(tb, "A"); winGame(tb, "B"); }            // 6:6
        expect("P4: 6-6 score",           toList(tb.getSetScore()), Arrays.asList(6, 6));
        expect("P4: 6-6 triggers a tie-break", tb.isTieBreak(), true);
        expect("P4: 6-6 has no set winner yet", tb.getSetWinner(), "");
        expect("P4: tie-break starts 0-0", toList(tb.tieBreak().getScore()), Arrays.asList(0, 0));

        // In the tie-break a "score" is a POINT, and it is scored in plain numbers
        for (int i = 0; i < 5; i++) tb.addScore("A");
        for (int i = 0; i < 5; i++) tb.addScore("B");
        expect("P4: tie-break 5-5 reads as plain points", tb.tieBreak().getHumanScore(), "5-5");
        expect("P4: tie-break has no Deuce vocabulary",
                tb.tieBreak().getHumanScore().contains("Deuce"), false);
        expect("P4: set score untouched by tie-break points",
                toList(tb.getSetScore()), Arrays.asList(6, 6));

        // 7:6 in a tie-break is NOT a win — the 2-point lead applies here too
        tb.addScore("A");                                                              // 6-5
        tb.addScore("A");                                                              // 7-5
        expect("P4: tie-break 7-5 ends it", tb.tieBreak().getResult(), "A");
        expect("P4: tie-break winner takes the set", tb.getSetWinner(), "A");
        expect("P4: set ends 7-6 regardless of the tie-break's own score",
                toList(tb.getSetScore()), Arrays.asList(7, 6));

        // A tie-break with no cap: 12:10 must be reachable, which is why it
        // cannot be a TennisGame (whose deuce reset would snap 6-6 to 3-3).
        Q4StandardSet longTb = new Q4StandardSet("A", "B");
        for (int i = 0; i < 6; i++) { winGame(longTb, "A"); winGame(longTb, "B"); }
        for (int i = 0; i < 10; i++) { longTb.addScore("A"); longTb.addScore("B"); }   // 10-10
        expect("P4: tie-break reaches 10-10 uncapped",
                toList(longTb.tieBreak().getScore()), Arrays.asList(10, 10));
        expect("P4: still no set winner at 10-10", longTb.getSetWinner(), "");
        longTb.addScore("A");                                                          // 11-10
        expect("P4: 11-10 is not enough", longTb.getSetWinner(), "");
        longTb.addScore("A");                                                          // 12-10
        expect("P4: 12-10 takes the tie-break", longTb.getSetWinner(), "A");
        expect("P4: and the set is still 7-6", toList(longTb.getSetScore()), Arrays.asList(7, 6));

        // Guards
        final Q4StandardSet over = new Q4StandardSet("A", "B", 2, 3);
        winGame(over, "A"); winGame(over, "A");
        expect("P4: knobs work (first-to-2)", over.getSetWinner(), "A");
        expect("P4: throws after set over",
                throwsRuntime(() -> over.addScore("B"), IllegalStateException.class), true);
        // The type alone proves nothing here: with the set-over guard deleted, the
        // finished game underneath throws the SAME type. Only the message shows
        // which layer rejected the point, so assert the message.
        expect("P4: rejection names the set, not the game underneath",
                messageOf(() -> over.addScore("B")), "Set already over");

        final Q4StandardSet overByTieBreak = new Q4StandardSet("A", "B", 2, 2);
        for (int i = 0; i < 2; i++) { winGame(overByTieBreak, "A"); winGame(overByTieBreak, "B"); }
        overByTieBreak.addScore("A"); overByTieBreak.addScore("A");
        expect("P4: tie-break decided the set", overByTieBreak.getSetWinner(), "A");
        expect("P4: rejection names the set, not the tie-break underneath",
                messageOf(() -> overByTieBreak.addScore("B")), "Set already over");

        final Q4StandardSet unknown = new Q4StandardSet("A", "B", 2, 3);
        for (int i = 0; i < 2; i++) { winGame(unknown, "A"); winGame(unknown, "B"); }  // 2:2
        expect("P4: knobbed tie-break triggers at 2-2", unknown.isTieBreak(), true);
        expect("P4: tie-break rejects an unknown player",
                throwsRuntime(() -> unknown.addScore("C"), IllegalArgumentException.class), true);
        expect("P4: rejected point left no trace",
                toList(unknown.tieBreak().getScore()), Arrays.asList(0, 0));

        // Human score across all three phases
        Q4StandardSet human = new Q4StandardSet("A", "B", 2, 3);
        expect("P4: human 0-0", human.getHumanScore(), "0-0");
        winGame(human, "A");
        expect("P4: human 1-0", human.getHumanScore(), "1-0");
        winGame(human, "B"); winGame(human, "A"); winGame(human, "B");                 // 2:2
        expect("P4: human shows the tie-break", human.getHumanScore(), "2-2 tie-break 0-0");
        human.addScore("A");
        expect("P4: human tracks tie-break points", human.getHumanScore(), "2-2 tie-break 1-0");
        human.addScore("A"); human.addScore("A");
        expect("P4: human announces the set", human.getHumanScore(), "Set A (3-2)");
    }

    /** Feeds 4 straight points so `player` takes exactly one game. */
    private static void winGame(Q2TennisSet set, String player) {
        for (int i = 0; i < 4; i++) set.addScore(player);
    }

    /** Same, for Part 4. Only valid before the tie-break starts. */
    private static void winGame(Q4StandardSet set, String player) {
        for (int i = 0; i < 4; i++) set.addScore(player);
    }

    /* --------------------------- helpers --------------------------- */

    private static List<Integer> toList(int[] a) {
        List<Integer> out = new ArrayList<>(a.length);
        for (int x : a) out.add(x);
        return out;
    }

    private static boolean throwsRuntime(Runnable r, Class<? extends RuntimeException> cls) {
        try { r.run(); return false; }
        catch (RuntimeException e) { return cls.isInstance(e); }
    }

    /** The set-over guard is message-only, so some tests must assert the message. */
    private static String messageOf(Runnable r) {
        try { r.run(); return "<did not throw>"; }
        catch (RuntimeException e) { return e.getMessage(); }
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
