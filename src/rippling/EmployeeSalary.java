package rippling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeSalary {
    public enum Operation {
        AVG, SUM, MAX
    }
    public class Employee {
        int id, salary;
        String gender, type, department, name;

        public Employee(int id, int salary, String gender, String type, String department, String name) {
            this.id = id;
            this.department = department;
            this.gender = gender;
            this.type = type;
            this.name = name;
            this.salary = salary;
        }
    }

    public List<Double> calSalary(List<Employee> employees, Operation operation) {
        Map<String, List<Employee>> map = new HashMap<>();
        for (Employee employee : employees) {
            map.putIfAbsent(employee.department, new ArrayList<>());
            map.get(employee.department).add(employee);
        }

        switch (operation) {
            case AVG:
                return calAVGSalary(map);
            case MAX:
                return calMAXSalary(map);
            case SUM:
                return calSumSalary(map);
            default:
                return new ArrayList<>();
        }
    }

    private List<Double> calSumSalary(Map<String, List<Employee>> map) {
        return new ArrayList<>();
    }

    private List<Double> calMAXSalary(Map<String, List<Employee>> map) {
        return new ArrayList<>();
    }

    private List<Double> calAVGSalary(Map<String, List<Employee>> map) {
        return new ArrayList<>();
    }
}
