package apple;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        List<int[]> list = new ArrayList<>();
        Arrays.sort(intervals, (a, b) -> (a[0] - b[0]));
        int[] prev = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if (cur[0] > prev[1]) {
                list.add(prev);
                prev = cur;
            } else {
                prev[1] = Math.max(prev[1], cur[1]);
            }
        }
        list.add(prev);
        int[][] result = new int[list.size()][2];
        return list.toArray(result);
    }

    public static void test5() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<String> f = executorService.submit(()->{
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(3000L);
                System.out.println("try 3000");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "async finished";
        });

        try {
            Thread.sleep(6000L);
            System.out.println("try 6000");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("start");
        String r = f.get();
        System.out.println(r);

    }

    public static void main(String[] args) {
        try {
            test5();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
