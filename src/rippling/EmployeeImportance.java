package rippling;

import java.util.*;

public class EmployeeImportance {
    class Employee {
        public int id;
        public int importance;
        public List<Integer> subordinates;
    };

    public int getImportance(List<Employee> employees, int id) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        Map<Integer, Employee> employeeMap = new HashMap<>();
        for (Employee employee : employees) {
            int bossID = employee.id;
            employeeMap.put(bossID, employee);
            map.put(bossID, employee.subordinates);
        }

        int start = id;
        Queue<Employee> queue = new LinkedList<>();
        int sum = 0;
        queue.add(employeeMap.get(id));

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0 ; i < size; i++) {
                Employee cur = queue.poll();
                sum += cur.importance;

                if (map.containsKey(cur.id)) {
                    for (int next : map.get(cur.id)) {
                        queue.add(employeeMap.get(next));
                    }
                }
            }
        }

        return sum;
    }
}
