package rippling;

import java.util.*;

public class EmplyeeRate {
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
    public static double maxTeamAvg(List<EmployeeInRate> employees) {
        for (EmployeeInRate employee : employees) {
            dfsFindAllNodes(employee);
        }
        return max;
    }

    private static double[] dfsFindAllNodes(EmployeeInRate employee) {
        if (employee.subs.isEmpty()) {
            max = Math.max(max, employee.rate);
            return new double[]{employee.rate, 1};
        }
        double count = 1;
        double sum = employee.rate;
        for (EmployeeInRate next : employee.subs) {
            double[] cur = dfsFindAllNodes(next);
            sum += cur[0];
            count += cur[1];
        }
        max = Math.max(max, sum / count);
        return new double[]{sum, count};
    }

    public static void main(String[] args) {
        EmployeeInRate b = new EmployeeInRate("B", new ArrayList<>(), 8);
        EmployeeInRate d = new EmployeeInRate("D", new ArrayList<>(), 8.5);
        EmployeeInRate e = new EmployeeInRate("E", new ArrayList<>(), 9);
        EmployeeInRate c = new EmployeeInRate("C", Arrays.asList(d, e), 6);
        EmployeeInRate a = new EmployeeInRate("A", Arrays.asList(b, c), 1115);

        List<EmployeeInRate> employees = Arrays.asList(a, c);
        System.out.println(maxTeamAvg(employees));;
    }
}
