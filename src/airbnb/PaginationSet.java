package airbnb;

import java.util.*;

public class PaginationSet {
    public static List<String> displayPages(List<String> input, int pageSize) {
        List<String> result = new ArrayList<>();
        Iterator<String> iterator = input.iterator();
        Set<String> visited = new HashSet<>();
        boolean end = false;
        int count = 0;
        while (iterator.hasNext()) {
            String s = iterator.next();
            String hostID = s.split(",")[0];
            if (!visited.contains(hostID) || end) {
                visited.add(hostID);
                count++;
                iterator.remove();
                result.add(s);
            }
            if (count == pageSize) {
                count = 0;
                visited.clear();
                end = false;
                if (!input.isEmpty()) {
                    result.add(" ");
                }
            } else {
                if (!iterator.hasNext()) {
                    end = true;
                    iterator = input.iterator();
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<String> input = new ArrayList<>();
        input.add("1,28,310.6,SF");
        input.add("4,5,204.1,SF");
        input.add("20,7,203.2,Oakland");
        input.add("6,8,202.2,SF");
        input.add("16,10,199.1,SF");
        input.add("1,16,190.4,SF");
        input.add("6,29,185.2,SF");
        input.add("7,20,180.1,SF");
        input.add("26,21,162.1,SF");
        input.add("2,18,161.2,SF");
        input.add("2,30,149.1,SF");
        input.add("3,76,146.2,SF");
        input.add("2,14,141.1,San Jose");
        List<String> result = displayPages(input, 5);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
    }
}
