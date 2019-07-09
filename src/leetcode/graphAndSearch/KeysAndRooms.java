package leetcode.graphAndSearch;

import java.util.*;

public class KeysAndRooms {
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        if (rooms.size() == 0) return true;
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.addAll(rooms.get(0));
        visited.add(0);
        visited.addAll(rooms.get(0));
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            for (int next : rooms.get(cur)) {
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return visited.size() == rooms.size();
    }
}
