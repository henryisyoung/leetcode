package airbnb.New2026;

/*
Box Candy Collector

Interview prompt:
  We start with one box in a house. Each box may contain:
    - some keys
    - some child boxes
    - some candies

  Some boxes are initially open and do not need a key. Other boxes are locked
  and can only be opened after we find their key. Return the maximum candies we
  can collect.

Clarifying questions to ask before coding:
  1. Is there exactly one starting box, or a list of starting boxes?
     This implementation supports one starting box.
  2. Can a child box be discovered before we have its key?
     Yes. Keep it in a waiting set until the key appears.
  3. Can a key appear before the box is discovered?
     Yes. Store keys independently.
  4. Can some boxes never be opened?
     Yes. They contribute 0 if unreachable or locked forever.
  5. Can there be cycles / duplicate child references?
     We guard with visited/opened sets so each box is processed once.
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/*
OOD model:
  Box owns its contents:
    id
    initiallyOpen
    candies
    keys inside this box
    child box ids inside this box

Algorithm:
  BFS over boxes we are able to open.

  State:
    discoveredBoxes = boxes we have physically found
    keys            = keys we have found
    opened          = boxes already opened/processed
    waiting         = discovered but locked boxes
    queue           = discovered boxes that are openable now

  When opening a box:
    - collect candies
    - collect keys
    - discover child boxes
    - whenever a key unlocks a waiting box, enqueue it

Complexity:
  Time:  O(B + K + E)
         B = reachable/discovered boxes, K = keys found, E = child-box links
  Space: O(B + K)
*/
public class BoxCandyCollector {

    public static final class Box {
        private final int id;
        private final boolean initiallyOpen;
        private final int candies;
        private final List<Integer> keys;
        private final List<Integer> children;

        public Box(int id, boolean initiallyOpen, int candies,
                   List<Integer> keys, List<Integer> children) {
            this.id = id;
            this.initiallyOpen = initiallyOpen;
            this.candies = candies;
            this.keys = keys == null ? new ArrayList<>() : new ArrayList<>(keys);
            this.children = children == null ? new ArrayList<>() : new ArrayList<>(children);
        }

        public int id() {
            return id;
        }

        public boolean initiallyOpen() {
            return initiallyOpen;
        }

        public int candies() {
            return candies;
        }

        public List<Integer> keys() {
            return keys;
        }

        public List<Integer> children() {
            return children;
        }
    }

    public int maxCandies(Box start, Map<Integer, Box> allBoxes) {
        if (start == null || allBoxes == null) return 0;

        Set<Integer> visited = new HashSet<>();
        Set<Integer> keys = new HashSet<>();
        Set<Integer> opened = new HashSet<>();
        Set<Integer> waiting = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        discover(start.id(), start, visited, waiting, queue, keys);

        int total = 0;
        while (!queue.isEmpty()) {
            int id = queue.poll();
            if (opened.contains(id)) continue;

            Box box = allBoxes.get(id);
            if (box == null) continue;
            if (!canOpen(box, keys)) {
                waiting.add(id);
                continue;
            }

            opened.add(id);
            waiting.remove(id);
            total += box.candies();

            for (int key : box.keys()) {
                keys.add(key);
                if (waiting.contains(key)) {
                    queue.offer(key);
                }
            }

            for (int childId : box.children()) {
                Box child = allBoxes.get(childId);
                discover(childId, child, visited, waiting, queue, keys);
            }
        }

        return total;
    }

    private void discover(int boxId, Box box,
                          Set<Integer> discovered,
                          Set<Integer> waiting,
                          Queue<Integer> queue,
                          Set<Integer> keys) {
        if (box == null || !discovered.add(boxId)) return;

        if (canOpen(box, keys)) {
            queue.offer(boxId);
        } else {
            waiting.add(boxId);
        }
    }

    private boolean canOpen(Box box, Set<Integer> keys) {
        return box.initiallyOpen() || keys.contains(box.id());
    }

    public static void main(String[] args) {
        BoxCandyCollector solver = new BoxCandyCollector();

        Map<Integer, Box> boxes = mapOf(
                box(1, true, 5, keys(2), children(2, 3)),
                box(2, false, 10, keys(3), children()),
                box(3, false, 20, keys(), children())
        );
        check("chain unlock", solver.maxCandies(boxes.get(1), boxes), 35);

        Map<Integer, Box> openChild = mapOf(
                box(1, true, 1, keys(), children(2)),
                box(2, true, 7, keys(), children())
        );
        check("initially open child", solver.maxCandies(openChild.get(1), openChild), 8);

        Map<Integer, Box> unreachable = mapOf(
                box(1, true, 1, keys(), children(2)),
                box(2, false, 100, keys(), children()),
                box(3, true, 1000, keys(), children())
        );
        check("locked and unreachable ignored", solver.maxCandies(unreachable.get(1), unreachable), 1);

        Map<Integer, Box> keyBeforeBox = mapOf(
                box(1, true, 3, keys(2), children(2)),
                box(2, false, 4, keys(), children())
        );
        check("key before child box", solver.maxCandies(keyBeforeBox.get(1), keyBeforeBox), 7);

        Map<Integer, Box> cycle = mapOf(
                box(1, true, 5, keys(2), children(2)),
                box(2, false, 6, keys(1), children(1))
        );
        check("cycle safe", solver.maxCandies(cycle.get(1), cycle), 11);

        System.out.println("All tests passed.");
    }

    private static Box box(int id, boolean initiallyOpen, int candies,
                           List<Integer> keys, List<Integer> children) {
        return new Box(id, initiallyOpen, candies, keys, children);
    }

    private static List<Integer> keys(Integer... ids) {
        return Arrays.asList(ids);
    }

    private static List<Integer> children(Integer... ids) {
        return Arrays.asList(ids);
    }

    private static Map<Integer, Box> mapOf(Box... boxes) {
        Map<Integer, Box> map = new HashMap<>();
        for (Box box : boxes) {
            map.put(box.id(), box);
        }
        return map;
    }

    private static void check(String label, int actual, int expected) {
        if (actual != expected) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
        System.out.println(label + ": " + actual);
    }
}
