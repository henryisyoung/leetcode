package uber;

import java.util.*;

public class ReachingPoints {

    public boolean reachingPoints(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        if (sx + sy < tx) return reachingPoints(sx + sy, sy, tx, ty);
        if(sy + sx < ty) return reachingPoints(sx, sy + sx, tx, ty);
        return false;
    }

    public static void main(String[] args) {
        int sx = 1, sy = 1, tx = 3, ty = 5;
        ReachingPoints solution = new ReachingPoints();
        System.out.println(solution.reachingPoints(sx, sy, tx, ty));
    }
}
