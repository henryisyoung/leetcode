package uber;

import java.util.*;

public class ReachingPoints {

    public boolean reachingPoints(int sx, int sy, int tx, int ty) {
      while (tx >= sx && ty >= sy) {
          if (tx > ty) {
              if (ty == sy) return (tx - sx) % ty == 0;
              tx %= ty;
          } else {
              if (tx == sx) return (ty - sy) % tx == 0;
              ty %= tx;
          }
      }
      return tx == sx && ty == sy;
    }

    public boolean reachingPointsSlow(int sx, int sy, int tx, int ty) {
        if (sx > tx || sy > ty) return false;
        if (sx == tx && sy == ty) return true;
        if (sx == tx) return (ty - sy) % sx == 0;
        if (sy == ty) return (tx - sx) % sy == 0;
        return reachingPoints(sx + sy, sy, tx, ty) || reachingPoints(sx, sy + sx, tx, ty);
    }

    public static void main(String[] args) {
        int sx = 1, sy = 1, tx = 3, ty = 5;
        ReachingPoints solution = new ReachingPoints();
        System.out.println(solution.reachingPointsSlow(sx, sy, tx, ty));
        System.out.println(solution.reachingPoints(sx, sy, tx, ty));
    }
}
