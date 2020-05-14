package lyft;

import java.util.*;

public class MinSteps {
    public static int findMinSteps(int target) {
        if (target <= 1) return 0;
        Set<Integer> set = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);
        set.add(1);
        set.add(0);
        int level = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                if (cur == target) {
                    return level;
                }
                int left = cur * 2, right = cur / 3;
                if (!set.contains(left)) {
                    queue.add(left);
                    set.add(left);
                }
                if (!set.contains(right)) {
                    queue.add(right);
                    set.add(right);
                }
            }
            level++;
        }
        return level;
    }

    public static void main(String[] args) {
        System.out.println(findMinSteps(5));
    }
}
