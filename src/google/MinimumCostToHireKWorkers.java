package google;

import java.util.*;

public class MinimumCostToHireKWorkers {
    public double mincostToHireWorkers(int[] q, int[] w, int K) {
        if(q == null || w == null || q.length != w.length || q.length < K) {
            return 0;
        }
        double result = Double.MAX_VALUE;
        double[][] workers = new double[q.length][2];
        for (int i = 0; i < q.length; i++) {
            workers[i] = new double[]{(double)(w[i]) / q[i], (double)q[i]};
        }
        Arrays.sort(workers, (a, b) -> (Double.compare(a[0], b[0])));
        PriorityQueue<Double> pq = new PriorityQueue<>(q.length, (a, b) -> Double.compare(b, a));
        double qsum = 0;
        for (double[] worker : workers) {
            qsum += worker[1];
            pq.add(worker[1]);
            if (pq.size() > K) {
                qsum -= pq.poll();
            }
            if (pq.size() == K) {
                result = Math.min(result, worker[0] * qsum);
            }
        }
        return result;
    }
}
