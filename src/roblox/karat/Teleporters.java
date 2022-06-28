package roblox.karat;

import java.util.*;

public class Teleporters {
    public Set<Integer> findNextMove(int n, String[] teleporters, int start, int end) {
        Set<Integer> result = new HashSet<>();
        Map<Integer, Integer> map = new HashMap<>();
        for (String teleporter : teleporters) {
            String[] array = teleporter.split(",");
            int from = Integer.parseInt(array[0]), to = Integer.parseInt(array[1]);
            map.put(from, to);
         }
        for (int i = 1; i <= n; i++) {
            int next = start + i;
            if (map.containsKey(next)) {
                result.add(map.get(next));
            } else {
                result.add(next);
            }
        }
        return result;
    }

    static boolean result = false;
    public static boolean finishable(int n, String[] teleporters, int start, int end) {
        Map<Integer, Integer> map = new HashMap<>();
        for (String teleporter : teleporters) {
            String[] array = teleporter.split(",");
            int from = Integer.parseInt(array[0]), to = Integer.parseInt(array[1]);
            map.put(from, to);
        }
        Set<Integer> visited = new HashSet<>();
        dfsSearch(visited, start, end, map, n);
        return result;
    }

    private static void dfsSearch(Set<Integer> visited, int cur, int end, Map<Integer, Integer> map, int n) {
        if (cur > end) return;
        if (cur == end) {
            result = true;
            return;
        }
        if (visited.contains(cur)) return;
        visited.add(cur);
        for (int i = 1; i <= n; i++) {
            int next = cur + i;
            if (map.containsKey(next)) {
                next = map.get(next);
            }
            dfsSearch(visited, next, end, map, n);
        }
    }

    public static void main(String[] args) {
        String[] teleporters1 = {"10,8", "11,5", "12,7", "13,9"};
        String[] teleporters2 = {"10,8", "11,5", "12,7", "13,9", "2,15"};
        String[] teleporters3 = {"10,8", "11,5", "12,1", "13,9", "2,15"};
        String[] teleporters5 = {"4,21", "11,18", "13,17", "16,17", "18,21", "22,11", "26,25", "27,9", "31,38", "32,43", "34,19", "35,19", "36,39", "38,25", "41,31"};
        String[] teleporters4 = {"2,4", "9,8", "11,7", "12,6", "18,14",
                "19,16", "20,9", "21,14", "22,6", "23,26", "25,10",
                "28,19", "29,27", "31,29", "38,33", "39,17", "41,30", "42,28", "45,44", "46,36"};
//        System.out.println(finishable2(4, teleporters2, 9 , 20));
//        System.out.println(finishable2(4, teleporters3, 9 , 20));
//        System.out.println(finishable2(4, teleporters4, 0 , 50));
//        System.out.println(finishable2(4, teleporters5, 0 , 50));
        System.out.println(finishable(2, teleporters5, 0 , 50));
    }
}
