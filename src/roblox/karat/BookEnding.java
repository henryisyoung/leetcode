package roblox.karat;

import java.util.*;

public class BookEnding {
    // https://leetcode.com/discuss/interview-question/2144701/indeed-karat-question

    public static int findEnding(int[] endingPages, int[][] pageOption, int optionToPick) {
        Set<Integer> endings = new HashSet<>();
        int n = 0;
        for (int i : endingPages) {
            n = Math.max(i , n);
            endings.add(i);
        }

        Map<Integer, Integer> map = new HashMap<>();
        for (int[] option : pageOption) {
            int from = option[0], to = option[optionToPick];
            map.put(from, to);
        }
        Set<Integer> visited = new HashSet<>();
        int i = 1;
        while (i <= n) {
            if (endings.contains(i)) {
                return i;
            }
            if (visited.contains(i)) {
                return -1;
            }
            visited.add(i);
            if (map.containsKey(i)) {
                i = map.get(i);
            } else {
                i++;
            }
        }
        return -1;
    }

    public static Set<Integer> findHappyEndings(int[] goodEndingPages, int[] badEndingPages, int[][] pageOption) {
        Set<Integer> result = new HashSet<>();
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] option : pageOption) {
            int from = option[0], t1 = option[1], t2 = option[2];
            map.put(from, new ArrayList<>(Arrays.asList(t1, t2)));
        }
        Set<Integer> visited = new HashSet<>();
        Set<Integer> happy = new HashSet<>();
        for (int i : goodEndingPages) {
            happy.add(i);
        }
        Set<Integer> bad = new HashSet<>();
        for (int i : badEndingPages){
            bad.add(i);
        }
        dfsFindAllEndings(result, happy, bad, visited, map, 1);
        return result;
    }

    private static void dfsFindAllEndings(Set<Integer> result, Set<Integer> happy, Set<Integer> bad,
                                   Set<Integer> visited, Map<Integer, List<Integer>> map, int curPage) {
        if (happy.contains(curPage)) {
            result.add(curPage);
            return;
        }
        if (bad.contains(curPage) || visited.contains(curPage)) {
            return;
        }
        visited.add(curPage);
        if (map.containsKey(curPage)) {
            for (int next : map.get(curPage)) {
                dfsFindAllEndings(result, happy, bad, visited, map, next);
            }
        } else {
            dfsFindAllEndings(result, happy, bad, visited, map, curPage + 1);
        }
    }

    public static void main(String[] args) {
//        int[] endingPages = {9,15, 20};
//        int[][] pageOption = {{3,5,11}};
//        int optionToPick = 1;
//        System.out.println(findEnding(endingPages, pageOption, optionToPick));

        int[] goodEndingPages = {10, 15, 25, 34};
        int[] badEndingPages = {21, 30, 40};
        int[][] pageOption = {{9, 16, 26}, {13, 31, 14}, {14, 16, 13}, {27, 12, 24}, {32, 34, 15}};
        System.out.println(findHappyEndings( goodEndingPages, badEndingPages, pageOption));
    }
}
