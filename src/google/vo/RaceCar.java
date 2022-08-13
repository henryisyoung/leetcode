package google.vo;

import java.util.*;

public class RaceCar {
    public static int racecar(int target) {
        Queue<int[]> queue = new LinkedList<>(); // arr = {1,2} arr[0] cur pos, arr[1] current speed
        queue.add(new int[]{0, 1});
        Set<String> visited = new HashSet<>();
        visited.add(0 + "_" + 1);
        visited.add(0 + "_" + -1);
        int step = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++){
                int[] cur = queue.poll();
                int pos = cur[0], speed = cur[1];
                {
                    // A
                    int pos1 = pos + speed;
                    int speed1 = speed * 2;

                    if (pos1 == target) {
                        return step + 1;
                    }
//                    queue.add(new int[]{pos1, speed1});
                    if (pos1 > 0 && pos1 < 2 * target) {
                        queue.add(new int[]{pos1, speed1});
                    }
                }
                {
                    // R
                    int speed2 = speed > 0 ? -1 : 1;
                    String key = pos + "_" + speed2;
                    if (!visited.contains(key)) {
                        queue.add(new int[]{pos, speed2});
                        visited.add(key);
                    }
                }
            }
            step++;
        }

        return -1;
    }

    public static void main(String[] args) {
        int target = 8;

        System.out.println(racecar(target));
    }
}
