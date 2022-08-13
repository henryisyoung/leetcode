package google.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MinimumAreaRectangle {
    //https://leetcode.com/problems/minimum-area-rectangle/
    public int minAreaRect(int[][] points) {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (int[] point : points) {
            int x = point[0];
            map.putIfAbsent(x, new HashSet<>());
            map.get(x).add(point[1]);
        }
        int min = Integer.MAX_VALUE;
        for (int[] p1 : points) {
            for (int[] p2 : points) {
                // find diag two points
                if (p1[0] == p2[0] || p1[1] == p2[1]) {
                    continue;
                }
                // find another two point
                int x1 = p1[0], y1 = p1[1];
                int x2 = p2[0], y2 = p2[1];
                if (map.get(x1).contains(y2) && map.get(x2).contains(y1)) {
                    int area = Math.abs(x1 - x2) * Math.abs(y1 - y2);
                    min = Math.min(min, area);
                }
            }
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }
}
