package rippling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmplyeeRate3 {
    /*
    第三轮一小时coding。input是一个array，每个element有三部分，employee name, employmee rating 和一个array of employee直属下‍‍属。求出最大的team rating的average。
    {
     {'A', 5, {'B', 'C'}},
     {'B', 8, {}},
     {'C', 6, {'D', 'E'}},
     {'D', 10, {}},
     {'E', 9,{}}
     } => A的team就是ABCDE，B的team就是B，C的team就是CDE之类的。
     */
    static class EmployeeInRate{
        String id;
        double rate;
        List<EmployeeInRate> subs;
        public EmployeeInRate(String id, List<EmployeeInRate> subs, double rate) {
            this.id = id;
            this.subs = subs;
            this.rate = rate;
        }
    }

    static double max ;
    public static double maxTeamAvg(EmployeeInRate root) {
        if (root == null) {
            return 0;
        }
        max = 0;
        dfsFindAllNodes(root);
        return max;
    }

    private static double[] dfsFindAllNodes(EmployeeInRate root) {
        if (root.subs.isEmpty()) {
            max = Math.max(max, root.rate);
            return new double[]{root.rate, 1};
        }
        double sum = root.rate;
        double count = 1;
        for (EmployeeInRate sub : root.subs) {
            double[] subResult = dfsFindAllNodes(sub);
            sum += subResult[0];
            count += subResult[1];
        }
        max = Math.max(max, sum / count);
        return new double[]{sum, count};
    }

    // house robber3
    public static double maxTeamSum(EmployeeInRate root) {
        if (root == null) {
            return 0;
        }
        return memoSearch(root)[1];
    }

    private static double[] memoSearch(EmployeeInRate root) {
        double[] local = new double[2];
        if (root == null) {
            return local;
        }
        double notUsed = 0, used = root.rate;
        for (EmployeeInRate sub : root.subs) {
            double[] subArray = memoSearch(sub);
            notUsed += subArray[1];
            used += subArray[0];
        }
        local[0] = notUsed;
        local[1] = Math.max(notUsed, used);
        return local;
    }

    public static void main(String[] args) {
        EmployeeInRate b = new EmployeeInRate("B", new ArrayList<>(), 8);
        EmployeeInRate d = new EmployeeInRate("D", new ArrayList<>(), 8.5);
        EmployeeInRate e = new EmployeeInRate("E", new ArrayList<>(), 9);
        EmployeeInRate c = new EmployeeInRate("C", Arrays.asList(d, e), 60);
        EmployeeInRate a = new EmployeeInRate("A", Arrays.asList(b, c), 115);

        List<EmployeeInRate> employees = Arrays.asList(a, c);
        System.out.println(maxTeamAvg(a));;
        System.out.println(maxTeamSum(a));;
    }
}
