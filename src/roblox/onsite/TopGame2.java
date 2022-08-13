package roblox.onsite;

import java.util.*;

public class TopGame2 {
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
        Map<String, List<String[]>> userLogsMap = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String uid = array[1];
            userLogsMap.putIfAbsent(uid, new ArrayList<>());
            userLogsMap.get(uid).add(array);
        }
        Map<Integer, Long> gameToTime = new HashMap<>();
        for (List<String[]> userLogs : userLogsMap.values()) {
            buildGameTime(gameToTime, userLogs);
        }
        System.out.println(gameToTime);
        int maxId = -1;
        long maxTime = 0;
        for (int gid : gameToTime.keySet()) {
            if (gameToTime.get(gid) > maxTime) {
                maxId = gid;
                maxTime = gameToTime.get(gid);
            }
        }
        return maxId;
    }


    private static void buildGameTime(Map<Integer, Long> gameToTime, List<String[]> logs) {
        String[] prev = null;
        Collections.sort(logs, Comparator.comparingLong(a -> Long.parseLong(a[0])));

        for (String[] log : logs) {
            String action = log[3];
            int gid = Integer.parseInt(log[2]);
            long time = Long.parseLong(log[0]);
            if (action.equals("join")) {
                prev = log;
            } else {
                if (prev != null && prev[2].equals(log[2])) {
                    long interval = time - Long.parseLong(prev[0]);
                    gameToTime.merge(gid, interval, Long::sum);
                }
                prev = null;
            }
        }
    }

    public static int findTopGame2(String[] logs) {
        Map<String, List<String[]>> userLogsMap = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String uid = array[1];
            userLogsMap.putIfAbsent(uid, new ArrayList<>());
            userLogsMap.get(uid).add(array);
        }
        Map<Integer, Long> gameToTime = new HashMap<>();
        for (List<String[]> userLogs : userLogsMap.values()) {
            buildGameTime2(gameToTime, userLogs);
        }
        System.out.println(gameToTime);
        int maxId = -1;
        long maxTime = 0;
        for (int gid : gameToTime.keySet()) {
            if (gameToTime.get(gid) > maxTime) {
                maxId = gid;
                maxTime = gameToTime.get(gid);
            }
        }
        return maxId;
    }

    private static void buildGameTime2(Map<Integer, Long> gameToTime, List<String[]> logs) {
        String[] prevJoin = null;
        String[] prevQuit = null;
        Collections.sort(logs, Comparator.comparingLong(a -> Long.parseLong(a[0])));
        Map<String[], Long> unmatchedLogs = new HashMap<>();
        long totalTime = 0;
        int session = 0;
        for (String[] log : logs) {
            String action = log[3];
            int gid = Integer.parseInt(log[2]);
            long time = Long.parseLong(log[0]);
            if (action.equals("join")) {
                if (prevJoin != null) {
                    unmatchedLogs.put(prevJoin, time - Long.parseLong(prevJoin[0]));
                }
                prevJoin = log;
                prevQuit = null;
            } else {
                if (prevJoin != null && prevJoin[2].equals(log[2])) {
                    session++;
                    long interval = time - Long.parseLong(prevJoin[0]);
                    totalTime += interval;
                    gameToTime.merge(gid, interval, Long::sum);
                } else {
                    if (prevJoin != null) {
                        unmatchedLogs.put(prevJoin, time - Long.parseLong(prevJoin[0]));
                    } else if (prevQuit != null) {
                        unmatchedLogs.put(log, time - Long.parseLong(prevQuit[0]));
                    }
                }
                prevJoin = null;
                prevQuit = log;
            }
        }
        long avg = session == 0 ? Integer.MAX_VALUE : totalTime / session;
        for (String[] log : unmatchedLogs.keySet()) {
            int gid = Integer.parseInt(log[2]);
            long interval = Math.min(avg, unmatchedLogs.get(log));
            gameToTime.merge(gid, interval, Long::sum);
        }
    }

    public static void main(String[] args) {
        String[] logs = {
//                "1000000000,user1,1001,join", // user 1 joined game 1001
//                "1000000000,user3,1001,join", // user 3 joined game 1001
//                "1000000005,user2,1002,join", // user 2 joined game 1002
//                "1000000010,user1,1001,quit", // user 1 quit game 1001 after 10 seconds
//                "1000000020,user2,1002,quit", // user 2 quit game 1002 after 15 seconds
//                "1000000030,user3,1001,quit", // user 2 quit game 1002 after 15 seconds
                "1000000010,user3,1001,join", // user 3 quit game 1001 after 20 seconds
                "1000000020,user3,1001,quit", // user 3 quit game 1001 after 20 seconds
                "1000000025,user3,1002,join", // user 3 quit game 1001 after 20 seconds
                "1000000030,user3,1001,quit", // user 3 quit game 1001 after 20 seconds
        };
        System.out.println(findTopGame2(logs));
    }
}
