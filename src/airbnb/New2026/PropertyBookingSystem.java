package airbnb.New2026;

/*
Property Booking System

Hack2Hire-style prompt:
  Given a list of properties, each with:
    - property id
    - neighborhood
    - capacity

  For a target neighborhood and groupSize, find the best combination of
  properties in that neighborhood that can fit the group.

Priority rules:
  1. Minimize total capacity among combinations with totalCapacity >= groupSize.
  2. If total capacity ties, use the fewest properties.

Important:
  Do NOT reverse the priority. A smaller number of properties is only a
  tie-breaker after total capacity.

Example:
  groupSize = 5
  options:
    A cap 6
    B cap 3
    C cap 2

  Best is B + C, total capacity 5, even though it uses 2 properties.
  A uses 1 property but total capacity 6, so it is worse.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
Algorithm: enumerate subsets with pruning.

  1. Filter properties by target neighborhood and positive capacity.
  2. Sort for deterministic output. We use capacity descending, then id.
     This often finds feasible combinations earlier, improving pruning.
  3. DFS include/exclude each property.

Pruning:
  - If currentCapacity >= groupSize, evaluate the current combination and stop
    expanding it, because capacities are positive and adding more properties can
    only increase total capacity.
  - If currentCapacity + remainingSuffixCapacity < groupSize, impossible.
  - If currentCapacity already exceeds the best capacity found, stop.
  - If currentCapacity equals best capacity and current count >= best count,
    stop.

Complexity:
  Worst-case O(2^n), where n is the number of properties in the neighborhood.
  Pruning is important but does not change the worst-case bound.
*/
public class PropertyBookingSystem {

    public static final class Property {
        public final String id;
        public final String neighborhood;
        public final int capacity;

        public Property(String id, String neighborhood, int capacity) {
            this.id = id;
            this.neighborhood = neighborhood;
            this.capacity = capacity;
        }

        @Override
        public String toString() {
            return id + "(" + neighborhood + "," + capacity + ")";
        }
    }

    private int groupSize;
    private int bestCapacity;
    private int bestCount;
    private List<Property> best;

    public List<Property> bestCombination(List<Property> properties, String neighborhood, int groupSize) {
        if (groupSize <= 0) return Collections.emptyList();
        if (properties == null || properties.isEmpty() || neighborhood == null) return Collections.emptyList();

        List<Property> candidates = new ArrayList<>();
        for (Property p : properties) {
            if (p != null && p.capacity > 0 && neighborhood.equals(p.neighborhood)) {
                candidates.add(p);
            }
        }
        if (candidates.isEmpty()) return Collections.emptyList();

        candidates.sort(Comparator
                .comparingInt((Property p) -> p.capacity).reversed()
                .thenComparing(p -> p.id));

        int n = candidates.size();
        int[] suffixCapacity = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            suffixCapacity[i] = suffixCapacity[i + 1] + candidates.get(i).capacity;
        }

        this.groupSize = groupSize;
        this.bestCapacity = Integer.MAX_VALUE;
        this.bestCount = Integer.MAX_VALUE;
        this.best = new ArrayList<>();

        dfs(candidates, suffixCapacity, 0, 0, new ArrayList<>());

        return new ArrayList<>(best);
    }

    private void dfs(List<Property> candidates,
                     int[] suffixCapacity,
                     int index,
                     int currentCapacity,
                     List<Property> chosen) {
        if (currentCapacity >= groupSize) {
            updateBest(currentCapacity, chosen);
            return;
        }
        if (index == candidates.size()) return;
        if (currentCapacity + suffixCapacity[index] < groupSize) return;
        if (currentCapacity > bestCapacity) return;
        if (currentCapacity == bestCapacity && chosen.size() >= bestCount) return;

        Property p = candidates.get(index);

        chosen.add(p);
        dfs(candidates, suffixCapacity, index + 1, currentCapacity + p.capacity, chosen);
        chosen.remove(chosen.size() - 1);

        dfs(candidates, suffixCapacity, index + 1, currentCapacity, chosen);
    }

    private void updateBest(int capacity, List<Property> chosen) {
        if (capacity < bestCapacity || (capacity == bestCapacity && chosen.size() < bestCount)) {
            bestCapacity = capacity;
            bestCount = chosen.size();
            best = new ArrayList<>(chosen);
        }
    }

    public static void main(String[] args) {
        PropertyBookingSystem solver = new PropertyBookingSystem();

        List<Property> props = Arrays.asList(
                p("A", "SoMa", 6),
                p("B", "SoMa", 3),
                p("C", "SoMa", 2),
                p("D", "Mission", 5)
        );
        check("capacity priority beats property count",
                solver.bestCombination(props, "SoMa", 5),
                "B,C");

        check("exact single property",
                solver.bestCombination(props, "SoMa", 6),
                "A");

        List<Property> tie = Arrays.asList(
                p("A", "N", 4),
                p("B", "N", 2),
                p("C", "N", 2),
                p("D", "N", 1)
        );
        check("same capacity picks fewer properties",
                solver.bestCombination(tie, "N", 4),
                "A");

        List<Property> over = Arrays.asList(
                p("A", "N", 8),
                p("B", "N", 5),
                p("C", "N", 4)
        );
        check("smallest over capacity",
                solver.bestCombination(over, "N", 6),
                "A");

        check("neighborhood filter",
                solver.bestCombination(props, "Mission", 5),
                "D");

        check("not enough capacity",
                solver.bestCombination(Arrays.asList(p("A", "N", 2), p("B", "N", 1)), "N", 10),
                "");

        check("zero group",
                solver.bestCombination(props, "SoMa", 0),
                "");

        System.out.println("All tests passed.");
    }

    private static Property p(String id, String neighborhood, int capacity) {
        return new Property(id, neighborhood, capacity);
    }

    private static void check(String label, List<Property> actual, String expectedIds) {
        String actualIds = ids(actual);
        if (!actualIds.equals(expectedIds)) {
            throw new AssertionError(label + " expected " + expectedIds + " but got " + actualIds
                    + " from " + actual);
        }
        System.out.println(label + ": " + actualIds);
    }

    private static String ids(List<Property> properties) {
        List<String> ids = new ArrayList<>();
        for (Property p : properties) ids.add(p.id);
        Collections.sort(ids);
        return String.join(",", ids);
    }
}
