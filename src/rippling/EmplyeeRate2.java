package rippling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmplyeeRate2 {
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
    static class EmployeeInRate {
        String name;
        List<EmployeeInRate> subs;
        double rate;
        public EmployeeInRate(String name, List<EmployeeInRate> subs, double rate) {
            this.name = name;
            this.subs = subs;
            this.rate = rate;
        }

        @Override
        public String toString() {
            return "EmployeeInRate{" +
                    "name='" + name + '\'' +
                    ", subs=" + subs +
                    ", rate=" + rate +
                    '}';
        }
    }

    static double max = 0;
    public static double maxTeamAvg(EmployeeInRate root) {
        dfsFindAllNodes(root);
        return max;
    }

    public static double maxTeamSum(EmployeeInRate root) {
        return dfsFindAllNodesSum(root)[1];
    }

    private static double[] dfsFindAllNodesSum(EmployeeInRate root) {
        if (root == null) {
            return new double[2];
        }
        // notUse don't use current level; use
        double notUse = 0, use = root.rate;

        for (EmployeeInRate next : root.subs) {
            double[] val = dfsFindAllNodesSum(next);
            notUse += val[1];
            use += val[0];
        }
        return new double[]{notUse, Math.max(notUse, use)};
    }

    private static double[] dfsFindAllNodes(EmployeeInRate node) {
        if (node.subs.isEmpty()) {
            max = Math.max(node.rate, max);
            return new double[]{node.rate, 1};
        }
        double count = 1;
        double sum = node.rate;
        for (EmployeeInRate next : node.subs) {
            double[] cur = dfsFindAllNodes(next);
            count += cur[1];
            sum += cur[0];
        }
        max = Math.max(sum/count, max);
        return new double[]{sum, count};
    }

    public static void main(String[] args) {
        EmployeeInRate b = new EmployeeInRate("B", new ArrayList<>(), 8);
        EmployeeInRate d = new EmployeeInRate("D", new ArrayList<>(), 8.5);
        EmployeeInRate e = new EmployeeInRate("E", new ArrayList<>(), 9);
        EmployeeInRate c = new EmployeeInRate("C", Arrays.asList(d, e), 600);
        EmployeeInRate a = new EmployeeInRate("A", Arrays.asList(b, c), 115);

        List<EmployeeInRate> employees = Arrays.asList(a, c);
        System.out.println(maxTeamAvg(a));;
        System.out.println(maxTeamSum(a));;
    }
}
