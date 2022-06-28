package roblox.karat;

import java.util.HashMap;
import java.util.Map;

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

    public static void main(String[] args) {
        String[] logs = {
                "1000000000,user1,1001,join", // user 1 joined game 1001
                "1000000005,user2,1002,join", // user 2 joined game 1002
                "1000000010,user1,1001,quit", // user 1 quit game 1001 after 10 seconds
                "1000000020,user2,1002,quit", // user 2 quit game 1002 after 15 seconds
        };
        System.out.println(findTopGame(logs));
    }
}
