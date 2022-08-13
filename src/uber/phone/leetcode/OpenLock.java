package uber.phone.leetcode;

import java.util.*;

public class OpenLock {
    // https://leetcode.com/problems/open-the-lock/
    public int openLock(String[] deadends, String target) {
        Set<String> set = new HashSet<>(Arrays.asList(deadends));
        String start = "0000";
        if (set.contains(start)) {
            return -1;
        }
        if (start.equals(target)) {
            return 0;
        }
        set.add(start);
        int level = 0;
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int t = 0; t < size; t++) {
                String node = queue.poll();
                if (node.equals(target)) {
                    return level;
                }
                for (int i = 0; i < 4; ++i) {
                    for (int d = -1; d <= 1; d += 2) {
                        int y = ((node.charAt(i) - '0') + d + 10) % 10;
                        String nei = node.substring(0, i) + ("" + y) + node.substring(i+1);
                        if (!set.contains(nei)) {
                            set.add(nei);
                            queue.offer(nei);
                        }
                    }
                }
            }
            level++;
        }
        return -1;
    }
}
