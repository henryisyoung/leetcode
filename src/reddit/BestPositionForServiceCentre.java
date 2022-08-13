package reddit;

public class BestPositionForServiceCentre {
    public double getMinDistSum(int[][] positions) {
        double cx = 0, cy = 0, n = positions.length;
        for (int[] pos : positions) {
            cx += pos[0];
            cy += pos[1];
        }
        cx /= n;
        cy /= n;
        double curDist = findDists(positions, cx, cy);
        int step = 1;
        while (step > 0.000001) {
            boolean reduceStep = true;
            double[][] steps = {{0, step}, {step, 0}, {0, -step}, {-step, 0}};
            for (double[] nextStep : steps) {
                double nx = cx + nextStep[0];
                double ny = cy + nextStep[1];
                double nextDist = findDists(positions, nx, ny);
                if (nextDist < curDist) {
                    reduceStep = false;
                    cx = nx;
                    cy = ny;
                    curDist = nextDist;
                }
            }
            if (reduceStep) {
                step /= 10;
            }
        }
        return curDist;
    }

    private double findDists(int[][] pos, double x, double y) {
        double sum = 0;
        for (int[] p : pos) {
            sum += Math.sqrt((x - p[0]) * (x - p[0]) + (y - p[1]) * (y - p[1]));
        }
        return sum;
    }
}
