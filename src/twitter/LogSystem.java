package twitter;

import java.util.*;

public class LogSystem {
    Map<Integer, String> map;
    Map<String, Integer> indicesMap;

    public LogSystem() {
        this.map = new HashMap<>();
        this.indicesMap = new HashMap<>();
        int[] indices = new int[]{4, 7, 10, 13, 16, 19};
        String[] units = new String[]{"Year", "Month", "Day", "Hour", "Minute", "Second"};
        for (int i = 0; i < indices.length; i++) {
            indicesMap.put(units[i], indices[i]);
        }
    }

    public void put(int id, String timestamp) {
        map.put(id, timestamp);
    }

    public List<Integer> retrieve(String s, String e, String gra) {
        List<Integer> result = new ArrayList<>();
        int pos = indicesMap.get(gra);
        for (int id : map.keySet()) {
            String t = map.get(id);
            if (t.substring(0, pos).compareTo(s.substring(0, pos)) >= 0 &&
                    t.substring(0, pos).compareTo(e.substring(0, pos)) <= 0) {
                result.add(id);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        LogSystem solver = new LogSystem();
        solver.put(1, "2017:01:01:23:59:59");
        solver.put(2, "2017:01:01:22:59:59");
        solver.put(3, "2016:01:01:00:00:00");
//        List<Integer> result = solver.retrieve("2016:01:01:01:01:01","2017:01:01:23:00:00","Year");
        List<Integer> result = solver.retrieve("2016:01:01:01:01:01","2017:01:01:23:00:00","Hour");
        System.out.println(result.toString());
    }
}
