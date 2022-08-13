package roblox.onsite;

import java.util.*;

public class TopGame {
    // https://www.1point3acres.com/bbs/thread-905093-1-1.html
    /*
     * - timestamp in seconds (long)
     * - user id (string)
     * - game id (int)
     * - action (string, either "join" or "quit")
     * e.g.
     * [
     * "1000000000,user1,1001,join", // user 1 joined game 1001
     * "1000000005,user2,1002,join", // user 2 joined game 1002
     * "1000000010,user1,1001,quit", // user 1 quit game 1001 after 10 seconds
     * "1000000020,user2,1002,quit", // user 2 quit game 1002 after 15 seconds
     * ];
    1. 每个user在一个时间只能存在于一个game中，不会出现user同时进入俩个游戏。
    2. 可能出现只有quit没有join的情况。
     */

    public static int findTopGame(String[] logs) {
        Map<String, List<String[]>> userMap = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String uid = array[1];
            userMap.putIfAbsent(uid, new ArrayList<>());
            userMap.get(uid).add(array);
        }
        Map<Integer, Long> gameToTime = new HashMap<>();
        for (List<String[]> userLogs : userMap.values()) {
            buildGameToTime(gameToTime, userLogs);
        }
        System.out.println(gameToTime);
        int maxId = -1;
        long time = 0;
        for (int id : gameToTime.keySet()) {
            if (gameToTime.get(id) > time) {
                time = gameToTime.get(id);
                maxId = id;
            }
        }
        return maxId;
    }

    public static int findTopGame2(String[] logs) {
        Map<String, List<String[]>> userMap = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String uid = array[1];
            userMap.putIfAbsent(uid, new ArrayList<>());
            userMap.get(uid).add(array);
        }
        Map<Integer, Long> gameToTime = new HashMap<>();
        for (List<String[]> userLogs : userMap.values()) {
            buildGameToTime2(gameToTime, userLogs);
        }
        System.out.println(gameToTime);
        int maxId = -1;
        long time = 0;
        for (int id : gameToTime.keySet()) {
            if (gameToTime.get(id) > time) {
                time = gameToTime.get(id);
                maxId = id;
            }
        }
        return maxId;
    }

    private static void buildGameToTime2(Map<Integer, Long> gameToTime, List<String[]> userLogs) {
        Map<String[], Long> unmatchedLogs = new HashMap<>();
        Collections.sort(userLogs, Comparator.comparingLong(a -> Long.parseLong(a[0])));
        String[] prevJoin = null;
        String[] prevLeft = null;
        long sum = 0;
        int session = 0;
        for (String[] log : userLogs) {
            String action = log[3];
            long time = Long.parseLong(log[0]);
            int gameId = Integer.parseInt(log[2]);
            if (action.equals("join")) {
                if (prevJoin != null) {
                    unmatchedLogs.put(prevJoin, time - Long.parseLong(prevJoin[0]));
                }
                prevJoin = log;
            } else {
                if (prevJoin != null && prevJoin[2].equals(log[2])) {
                    long interval = time - Long.parseLong(prevJoin[0]);
                    gameToTime.merge(gameId, interval, Long::sum);
                    sum += interval;
                    session++;
                } else {
                    if (prevJoin != null) {
                        unmatchedLogs.put(prevJoin, time - Long.parseLong(prevJoin[0]));
                    } else {
                        if (prevLeft != null) {
                            unmatchedLogs.put(log, time - Long.parseLong(prevLeft[0]));
                        }
                    }
                }
                prevJoin = null;
                prevLeft = log;
            }
        }
        long avg = session != 0 ? sum / session : Long.MAX_VALUE;
        for (String[] log : unmatchedLogs.keySet()) {
            long val = Math.min(unmatchedLogs.get(log), avg);
            int gameId = Integer.parseInt(log[2]);
            gameToTime.merge(gameId, val, Long::sum);
        }
    }

    private static void buildGameToTime(Map<Integer, Long> gameToTime, List<String[]> userLogs) {
        Collections.sort(userLogs, Comparator.comparingLong(a -> Long.parseLong(a[0])));
        String[] prev = null;

        for (String[] log : userLogs) {
            String action = log[3];
            long time = Long.parseLong(log[0]);
            int gameId = Integer.parseInt(log[2]);
            if (action.equals("join")) {
                prev = log;
            } else {
                if (prev != null && prev[2].equals(log[2])) {
                    long interval = time - Long.parseLong(prev[0]);
                    gameToTime.merge(gameId, interval, Long::sum);
                }
                prev = null;
            }
        }
    }

    public static void main(String[] args) {
        String[] logs = {
//                "1000000000,user1,1001,join", // user 1 joined game 1001
//                "1000000000,user3,1001,join", // user 3 joined game 1001
//                "1000000005,user2,1002,join", // user 2 joined game 1002
//                "1000000010,user1,1001,quit", // user 1 quit game 1001 after 10 seconds
//                "1000000020,user2,1002,quit", // user 2 quit game 1002 after 15 seconds
                "1000000020,user3,1001,quit", // user 3 quit game 1001 after 20 seconds
                "1000000025,user3,1002,quit", // user 3 quit game 1001 after 20 seconds
                "1000000090,user3,1001,quit", // user 3 quit game 1001 after 20 seconds
        };
        System.out.println(findTopGame2(logs));
    }
}
