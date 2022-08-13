package google.vo;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class AmountNewAreaPaintedEachDay {
    public static int[] amountPainted(int[][] paint) {
        int n = paint.length;
        int[] result = new int[n];
        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            int[] p = paint[i];
            int start = p[0], end = p[1];
            int draw = end - start;
            Map.Entry<Integer, Integer> floor = map.floorEntry(start);
            if (floor != null) {
                if (floor.getValue() <= start) {
                    // do nothing
                } else if (floor.getValue() >= end) {
                    continue;
                } else {
                    map.remove(floor.getKey());
                    draw = end - floor.getValue();
                    start = floor.getKey();
                }
            }
            Map.Entry<Integer, Integer> ceil = map.ceilingEntry(start);
            while (ceil != null && ceil.getKey() <= end) {
                int overlap = Math.min(end, ceil.getValue()) - ceil.getKey();
                draw -= overlap;
                map.remove(ceil.getKey());
                end = Math.max(end, ceil.getValue());
                ceil = map.ceilingEntry(start);
            }
            result[i] = draw;
            map.put(start, end);
        }

        return result;
    }

    public static void main(String[] args) {
        int[][] paint = {{1,4}, {4,7},{5,8}};
        int[] result = amountPainted(paint);
        System.out.println(Arrays.toString(result));
    }
}
