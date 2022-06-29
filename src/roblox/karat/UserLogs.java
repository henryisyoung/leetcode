package roblox.karat;

import java.util.*;
import java.util.stream.Collectors;

//https://www.1point3acres.com/bbs/thread-809484-1-1.html
public class UserLogs {
    public static Map<String, int[]> userLogs(String[][] logs) {
        Map<String, int[]> map = new HashMap<>();
        for (String[] log : logs){
            String userId = log[1];
            Integer timestamp = Integer.parseInt(log[0]);
            map.putIfAbsent(userId, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE});
            map.get(userId)[0] = Math.min(map.get(userId)[0], timestamp);
            map.get(userId)[1] = Math.max(map.get(userId)[1], timestamp);
        }

        return map;
    }

    static class Result {
        String resourceId;
        int count;
        public Result(String resourceId, int count) {
            this.count = count;
            this.resourceId = resourceId;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "resourceId='" + resourceId + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    public static Result findMostRequestedResource(String[][] logs) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (String[] log : logs){
            String rid = log[2];
            Integer timestamp = Integer.parseInt(log[0]);
            map.putIfAbsent(rid, new ArrayList<>());
            map.get(rid).add(timestamp);
        }
        int max = 0;
        String rid = "";

        for (String id : map.keySet()) {
            int count = findCount(map.get(id));
            if (count > max) {
                max = count;
                rid = id;
            }
        }

        return new Result(rid, max);
    }

    private static int findCount(List<Integer> times) {
        Collections.sort(times);
        int maxFreq = 0;
        int r = 0, n = times.size();
        for (int l = 0; l < n; l++) {
            while (r < n && times.get(r) <= 300 + times.get(l)) {
                r++;
                maxFreq = Math.max(r - l, maxFreq);
            }
        }
        return maxFreq;
    }

    public static Map<String, Map<String, Double>> buildTransition(String[][] logs) {
        // Create Map(user -> logs)
        Map<String, List<String[]>> userLogs = new HashMap<>();
        for (String[] log: logs) {
            userLogs.computeIfAbsent(log[1], k -> new ArrayList<>()).add(log);
        }

        // Create Map(re -> Map(next, freq))
        Map<String, Map<String, Integer>> reToNextFreqs = new HashMap<>();
        for (List<String[]> logList: userLogs.values()) {
            logList.sort(Comparator.comparing(l -> Integer.parseInt(l[0])));

            String prev = "START";
            for (String[] log: logList) {
                String curr = log[2];
                reToNextFreqs.computeIfAbsent(prev, k -> new HashMap<>()).merge(curr, 1, Integer::sum);
                prev = curr;
            }
            reToNextFreqs.computeIfAbsent(prev, k -> new HashMap<>()).merge("END", 1, Integer::sum);
        }

        // Convert to transit graph with probabilities
        Map<String, Map<String, Double>> res = new HashMap<>();
        reToNextFreqs.forEach((re, nextFreqs) -> {
            int total = nextFreqs.values().stream().mapToInt(i -> i).sum();
            Map<String, Double> probMap = nextFreqs.entrySet().stream().collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            e -> (double) e.getValue() / total
                    )
            );
            res.put(re, probMap);
        });
        return res;
    }

    public static void main(String[] args) {
        String[][] logs = {
                {"58523", "user_1", "resource_1"},
                {"62314", "user_2", "resource_2"},
                {"54001", "user_1", "resource_3"},
                {"200", "user_6", "resource_5"},
                {"215", "user_6", "resource_4"},
                {"54060", "user_2", "resource_3"},
                {"53760", "user_3", "resource_3"},
                {"58522", "user_22", "resource_1"},
                {"53651", "user_5", "resource_3"},
                {"2", "user_6", "resource_1"},
                {"100", "user_6", "resource_6"},
                {"400", "user_7", "resource_2"},
                {"100", "user_8", "resource_6"},
                {"54359", "user_1", "resource_3"}
        };
//        for (Map.Entry<String, int[]> entry : userLogs(logs).entrySet()) {
//            System.out.println("userID: " + entry.getKey() + " w/ window " + Arrays.toString(entry.getValue()));
//        }
//        System.out.println(userLogs(logs));
        System.out.println(findMostRequestedResource(logs));

        String[][] logs2 = {
                {"58523", "user_1", "resource_1"},
                {"62314", "user_2", "resource_2"},
                {"54001", "user_1", "resource_3"},
                {"200", "user_6", "resource_5"},
                {"215", "user_6", "resource_4"},
                {"54060", "user_2", "resource_3"},
                {"53760", "user_3", "resource_3"},
                {"53761", "user_3", "resource_3"},
                {"53762", "user_3", "resource_3"},
                {"58522", "user_22", "resource_1"},
                {"53651", "user_5", "resource_3"},
                {"58524", "user_6", "resource_1"},
                {"58525", "user_6", "resource_1"},
                {"100", "user_6", "resource_6"},
                {"400", "user_7", "resource_2"},
                {"100", "user_8", "resource_6"},
                {"54359", "user_1", "resource_3"}
        };
        System.out.println(findMostRequestedResource(logs2));

    }

}
