package google.vo.mianjing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidSquare {
    public static boolean validSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        List<int[]> points = Arrays.asList(p1, p2, p3, p4);
        Collections.sort(points, (a, b) -> (a[0] - b[0]));

        int p0p3 = calDist(points.get(0), points.get(3));
        int p1p2 = calDist(points.get(1), points.get(2));

        int p0p1 = calDist(points.get(0), points.get(1));
        int p0p2 = calDist(points.get(0), points.get(2));
        int p2p3 = calDist(points.get(2), points.get(3));
        int p1p3 = calDist(points.get(1), points.get(3));

        return p0p3 == p1p2 && p0p1 == p0p2 && p0p2 == p2p3 && p2p3 == p1p3;
    }

    private static int calDist(int[] p1, int[] p2) {
        return (p1[0] - p2[0]) * (p1[0] - p2[0]) - (p1[1] - p2[1]) * (p1[1] - p2[1]);
    }

    public static void main(String[] args) {

    }
}
