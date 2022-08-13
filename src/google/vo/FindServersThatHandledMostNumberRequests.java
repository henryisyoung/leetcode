package google.vo;

import java.util.*;

public class FindServersThatHandledMostNumberRequests {
    public static List<Integer> busiestServers(int k, int[] arrival, int[] load) {
        int[] counter = new int[k];
        // use a tree to track available servers
        TreeSet<Integer> available = new TreeSet<Integer>();
        for (int num = 0; num < k; num++) {
            available.add(num);
        }
        // use a PQ to maintain the availability at current arrival time
        Queue<int[]> busyServers = new PriorityQueue<>((a, b)->(a[0] - b[0]));

        for (int idx = 0; idx < arrival.length; idx++) {
            int curTime = arrival[idx];
            int endTime = curTime + load[idx];
            while (!busyServers.isEmpty() && busyServers.peek()[0] <= curTime) {
                int freedServer = busyServers.poll()[1];
                available.add(freedServer);
            }
            if (available.size() == 0) continue; // all busy
            Integer assignNum = available.ceiling(idx % k);
            if (assignNum == null) {
                assignNum = available.first();
            }
            counter[assignNum]++;
            available.remove(assignNum);
            busyServers.offer(new int[] {endTime, assignNum});
        }

        return findMaxesInCounter(counter);
    }

    private static List<Integer> findMaxesInCounter(int[] counter) {
        int max = 0;
        for (int i = 0; i < counter.length; i++) {
            max = Math.max(max, counter[i]);
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < counter.length; i++) {
            if (counter[i] == max) {
                result.add(i);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arrival = {1,2,3,4,8,9,10}, load = {5,2,10,3,1,2,2};
        int k = 3;
        System.out.println(busiestServers(k, arrival, load));
    }
}
