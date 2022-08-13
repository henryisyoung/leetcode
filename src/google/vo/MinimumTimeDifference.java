package google.vo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MinimumTimeDifference {
    public static int findMinDifference(List<String> timePoints) {
        Collections.sort(timePoints);

        int min = Integer.MAX_VALUE;
        for (int i = 1; i < timePoints.size(); i++) {
            String prev = timePoints.get(i - 1), cur = timePoints.get(i);
            int prevVal = calTimeValue(prev), curVal = calTimeValue(cur);

            min = Math.min(min, curVal - prevVal);
            if (i == timePoints.size() - 1) {
                String next = timePoints.get(0);
                int nextVal = calTimeValue(next);

                min = Math.min(min, curVal - nextVal);
                min = Math.min(min, nextVal + 24 * 60 - curVal);
            }
        }
        return min;
    }

    private static int calTimeValue(String time) {
        //HH:MM
        String[] array = time.split(":");
        int h = Integer.parseInt(array[0]);
        int m = Integer.parseInt(array[1]);
        return h * 60 + m;
    }

    public static void main(String[] args) {
        List<String> timePoints = Arrays.asList("01:01","02:01","03:00");
        System.out.println(findMinDifference(timePoints));
    }
}
