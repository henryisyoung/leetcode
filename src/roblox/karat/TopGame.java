package roblox.karat;

import java.util.*;
// https://productive-horse-bb0.notion.site/Roblox-Karat-2021-5-2022-2-9b07dcbba3634de080c3854c1293d0dc
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

    public static int findTopGamePatchMissingLogs(String[] logs) {
        Map<String, List<String[]>> map = new HashMap<>();
        for (String log : logs) {
            String[] array = log.split(",");
            String uid = array[1];
            map.putIfAbsent(uid, new ArrayList<>());
            map.get(uid).add(array);
        }

        Map<Integer, Long> gameToTime = new HashMap<>();
        for (List<String[]> entries: map.values()) { // "1000000000,user1,1001,join"
            findGameInterval(entries, gameToTime);
        }

        int topGame = -1;
        long maxTime = 0;
        for (int game: gameToTime.keySet()) {
            if (gameToTime.get(game) > maxTime) {
                maxTime = gameToTime.get(game);
                topGame = game;
            }
        }
        return topGame;
    }

    private static void findGameInterval(List<String[]> logs, Map<Integer, Long> gameToTime) {
        logs.sort(Comparator.comparing(e -> Long.parseLong(e[0])));
        long sum = 0, count = 0;
        Map<String[], Long> unmatchedMap = new HashMap<>();
        String[] prev = null;
        for (String[] log : logs) {
            String action = log[3];
            long timeNow = Long.parseLong(log[0]);
            String gid = log[2];
            if (action.equals("join")) {
                if (prev != null) {
                    unmatchedMap.put(prev, timeNow - Long.parseLong(prev[0]));
                }
                prev = log;
            } else {
                if (prev != null && prev[2].equals(gid)) {
                    long span = timeNow - Long.parseLong(prev[0]);
                    sum += span;
                    count++;
                    gameToTime.merge(Integer.parseInt(gid), span, Long::sum);
                }
                prev = null;
            }
        }
        if (prev != null) {
            unmatchedMap.put(prev, Long.MAX_VALUE);
        }
        long avg = sum / count;
        for (String[] log : unmatchedMap.keySet()) {
            long span = Math.min(unmatchedMap.get(log), avg);
            int gid = Integer.parseInt(log[2]);
            gameToTime.merge(gid, span, Long::sum);
        }
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
//        System.out.println(findTopGame2(logs2));

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i <= 3; i++) {
            map.merge(i % 2, i, Integer::sum);
        }

        System.out.println(map);
    }
}
