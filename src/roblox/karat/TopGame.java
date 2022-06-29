package roblox.karat;

import java.util.*;

public class TopGame {
    public static String findTopGame(String[] logs) {
        Map<String, Long> map = new HashMap<>();
        long max = 0;
        String result = "";
        for (String log : logs) {
            String[] array = log.split(",");
            Long timestamp = Long.parseLong(array[0]);
            String userId = array[1];
            String gameId = array[2];
            String action = array[3];
            if (action.equals("join")) {
                map.put(userId, timestamp);
            } else {
                if (map.containsKey(userId)) {
                    long val = map.get(userId);
                    if (timestamp - val > max) {
                        max = timestamp - val;
                        result = gameId;
                    }
                    map.remove(userId);
                }
            }
        }
        System.out.println("max : " + max);
        return result;
    }

    public static String findTopGame2(String[] logs) {
        Map<String, List<String[]>> map = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String gameId = array[2];
            map.putIfAbsent(gameId, new ArrayList<>());
            map.get(gameId).add(array);
        }
        int max = 0;
        String game = "";

        for (String gameId : map.keySet()) {
            List<String[]> list = map.get(gameId);
            Collections.sort(list, Comparator.comparingInt(a -> Integer.parseInt(a[0])));
            int count = 0;
            Integer prev = null;

            for (String[] log : list) {
                System.out.println(Arrays.toString(log));
                String action = log[3];
                if (action.equals("join")) {
                    prev = Integer.parseInt(log[0]);
                } else {
                    if (prev == null) continue;
                    count += Integer.parseInt(log[0]) - prev;
                    prev = null;
                }
            }
            if (max < count) {
                max = count;
                game = gameId;
            }
        }
        return game;
    }

    public static String findTopGamePatchMissingLogs(String[] logs) {
        Map<String, List<String[]>> map = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String uid = array[1];
            map.putIfAbsent(uid, new ArrayList<>());
            map.get(uid).add(array);
        }

        Map<String, Integer> gameTime = calcualteGameTime(map);

        int max = 0;
        String game = "";
        for (Map.Entry<String, Integer> entry : gameTime.entrySet()) {
            if (entry.getValue() > max) {
                game = entry.getKey();
                max = entry.getValue();
            }
        }
        return game;
    }

    private static Map<String, Integer> calcualteGameTime(Map<String, List<String[]>> userMap) {
        Map<String, Integer> gameMap = new HashMap<>();

        int sum = 0, count = 0, min = Integer.MAX_VALUE;
        Integer prev = null;
        List<String[]> missLogs = new ArrayList<>();
        for (String uid : userMap.keySet()) {
            List<String[]> logs = userMap.get(uid);
            Collections.sort(logs, Comparator.comparingInt(a -> Integer.parseInt(a[0])));

            for (int i = 0; i < logs.size(); i++) {
                String[] log = logs.get(i);
                String action = log[3];
                Integer time = Integer.parseInt(log[0]);
                if (action.equals("join")) {
                    if (i != 0 && prev != null) {
                        missLogs.add(logs.get(i - 1));
                        min = Math.min(min, time);
                    }
                    prev = time;
                } else {
                    if (prev == null) {
                        String[] prevLog = logs.get(i - 1);

                    }
                }
            }

        }
        return gameMap;
    }


    public static void main(String[] args) {
        String[] logs = {
                "1000000000,user1,1001,join", // user 1 joined game 1001
                "1000000005,user2,1002,join", // user 2 joined game 1002
                "1000000010,user1,1001,quit", // user 1 quit game 1001 after 10 seconds
                "1000000020,user2,1002,quit", // user 2 quit game 1002 after 15 seconds
        };
//        System.out.println(findTopGame2(logs));

        String[] logs2 = {
                "1000000000,user1,1001,join", // user 1 joined game 1001
                "1000000005,user2,1002,join", // user 2 joined game 1002
                "1000000010,user1,1001,quit", // user 1 quit game 1001 after 10 seconds
//                "1000000010,user1,1001,join", // user 1 quit game 1001 after 10 seconds
                "1000000020,user2,1002,quit", // user 2 quit game 1002 after 15 seconds
                "1000000020,user1,1001,quit", // user 2 quit game 1002 after 15 seconds
        };
        System.out.println(findTopGame2(logs2));
    }
}
