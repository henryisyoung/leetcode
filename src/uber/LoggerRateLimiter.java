package uber;

import java.util.*;

public class LoggerRateLimiter {
    Map<String, Integer> map;
    public LoggerRateLimiter(){
        this.map = new HashMap<>();
    }
    public boolean shouldPrintMessage(int timestamp, String message) {
        if (!map.containsKey(message)) {
            map.put(message, timestamp);
            return true;
        }
        int prevTime = map.get(message);
        if (prevTime + 10 < timestamp) {
            map.put(message, timestamp);
            return true;
        }
        return false;
    }
}
