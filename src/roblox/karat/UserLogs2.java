package roblox.karat;

import java.util.*;

//https://www.1point3acres.com/bbs/thread-809484-1-1.html
public class UserLogs2 {
    public static Map<String, Long[]> userLogs(String[][] logs) {
        Map<String, Long[]> result = new HashMap<>();
        for (String[] log : logs) {
            String userId = log[1];
            Long time = Long.parseLong(log[0]);
            result.putIfAbsent(userId, new Long[]{Long.MAX_VALUE, Long.MIN_VALUE});
            result.get(userId)[0] = Math.min(result.get(userId)[0], time);
            result.get(userId)[1] = Math.max(result.get(userId)[1], time);
        }

        return result;
    }

    public static String[] findMostRequestedResource(String[][] logs) {
        if (logs == null || logs.length == 0) {
            return new String[0];
        }
        String[] result = new String[2];
        Map<String, List<Long>> map = new HashMap<>();
        for (String[] log : logs) {
            String id = log[2];
            Long time = Long.parseLong(log[0]);
            map.putIfAbsent(id, new ArrayList<>());
            map.get(id).add(time);
        }
        int max = 0;
        String maxId = "";
        for (String id : map.keySet()) {
            int count = countWindow(map.get(id));
            if (count > max) {
                maxId = id;
                max = count;
            }
        }
        result[0] = maxId;
        result[1] = Long.toString(max);
        return result;
    }

    private static int countWindow(List<Long> timestamps) {
        Collections.sort(timestamps);
        int max = 0;
        int r = 0, n = timestamps.size();
        for (int i = 0; i < n; i++) {
            while (r < n && timestamps.get(r) - 300 <= timestamps.get(i)) {
                r++;
                max = Math.max(r - i, max);
            }
        }

        return max;
    }
    public static Map<String, Map<String, Double>> buildTransition(String[][] logs) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        Map<String, List<String[]>> userLogs = new HashMap<>();
        for (String[] log : logs) {
            String uid = log[1];
            userLogs.putIfAbsent(uid, new ArrayList<>());
            userLogs.get(uid).add(log);
        }
        Map<String, Map<String, Integer>> refToNextRefMap = new HashMap<>();
        for (String uid : userLogs.keySet()) {
            List<String[]> curLogs = userLogs.get(uid);
            Collections.sort(curLogs, (a, b) -> {
                return a[0].compareTo(b[0]);
            });
            String prev = "START";
            for (String[] log : curLogs) {
                String nextRef = log[2];
                refToNextRefMap.putIfAbsent(prev, new HashMap<>());
                Map<String, Integer> nextRefMap = refToNextRefMap.get(prev);
                nextRefMap.put(nextRef, nextRefMap.getOrDefault(nextRef, 0) + 1);
                prev = nextRef;
            }
            String nextRef = "END";
            refToNextRefMap.putIfAbsent(prev, new HashMap<>());
            Map<String, Integer> nextRefMap = refToNextRefMap.get(prev);
            nextRefMap.put(nextRef, nextRefMap.getOrDefault(nextRef, 0) + 1);
        }
//        System.out.println("refToNextRefMap " + refToNextRefMap);
        for (String ref : refToNextRefMap.keySet()) {
            Map<String, Integer> nextRefMap = refToNextRefMap.get(ref);
            int total = sum(nextRefMap.values());
            Map<String, Double> value = new HashMap<>();
            for (String nextRef : nextRefMap.keySet()) {
                double prob = nextRefMap.get(nextRef) / (1.0 * total);
                value.put(nextRef, prob);
            }
            result.put(ref, value);
        }

        return result;
    }

    private static int sum(Collection<Integer> values) {
        int sum = 0;
        for (int i : values) {
            sum += i;
        }
        return sum;
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
                {"54359", "user_1", "resource_3"},
        };
//        for (Map.Entry<String, Long[]> entry : userLogs(logs).entrySet()) {
//            System.out.println("userID: " + entry.getKey() + " w/ window " + Arrays.toString(entry.getValue()));
//        }
//        System.out.println(userLogs(logs));
//        System.out.println(Arrays.toString(findMostRequestedResource(logs)));
//
//        String[][] logs2 = {
//                {"58523", "user_1", "resource_1"},
//                {"62314", "user_2", "resource_2"},
//                {"54001", "user_1", "resource_3"},
//                {"200", "user_6", "resource_5"},
//                {"215", "user_6", "resource_4"},
//                {"54060", "user_2", "resource_3"},
//                {"53760", "user_3", "resource_3"},
//                {"53761", "user_3", "resource_3"},
//                {"53762", "user_3", "resource_3"},
//                {"58522", "user_22", "resource_1"},
//                {"53651", "user_5", "resource_3"},
//                {"58524", "user_6", "resource_1"},
//                {"58525", "user_6", "resource_1"},
//                {"100", "user_6", "resource_6"},
//                {"400", "user_7", "resource_2"},
//                {"100", "user_8", "resource_6"},
//                {"54359", "user_1", "resource_3"}
//        };
        System.out.println(buildTransition(logs));

    }

}
