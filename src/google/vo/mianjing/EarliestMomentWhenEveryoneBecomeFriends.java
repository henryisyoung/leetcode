package google.vo.mianjing;

import leetcode.union.Union;

import java.util.Arrays;

public class EarliestMomentWhenEveryoneBecomeFriends {
    public int earliestAcq(int[][] logs, int n) {
        Arrays.sort(logs, (a,b) ->(a[0] - b[0])) ;
        Union union = new Union(n);

        for (int[] log : logs) {
            int a = log[1], b = log[2], time = log[0];
            union.union(a, b);
            if (union.count == 1) {
                return time;
            }
        }
        return -1;
    }
}
