package facebook;

import java.util.*;

public class MinimumKnightMoves {
    public int minKnightMoves(int x, int y) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(0, 0 ,0));
        int[][] dirs = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        Set<Integer> visited = new HashSet<>();
        visited.add(0);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            if (cur.x == x && cur.y == y) return cur.dist;
            for (int[] dir : dirs) {
                int nx = cur.x + dir[0], ny = cur.y + dir[1];
                if (visited.contains(1000 * nx + ny)) continue;
                visited.add(1000 * nx + ny);
                queue.add(new Node(nx, ny, cur.dist + 1));
            }
        }
        return 0;
    }
    Map<String, Integer> cache = new HashMap<>();

    public int minKnightMovesMemo(int x, int y) {
        x = Math.abs(x);
        y = Math.abs(y);

        if(0 == x + y) {
            // Destination is at origin
            return 0;
        }

        if(2 == x + y) {
            // Origin and destination are on same horizontal or vertical line
            return 2;
        }

        String key1 = Math.abs(x-1) + "," + Math.abs(y-2);
        String key2 = Math.abs(x-2) + "," + Math.abs(y-1);

        int path1 = 0;
        int path2 = 0;

        if(cache.containsKey(key1)) {
            path1 = cache.get(key1);
        } else {
            path1 = minKnightMoves(Math.abs(x-1), Math.abs(y-2));
            cache.put(key1, path1);
        }

        if(cache.containsKey(key2)) {
            path2 = cache.get(key2);
        } else {
            path2 = minKnightMoves(Math.abs(x-2), Math.abs(y-1));
            cache.put(key2, path2);
        }

        return Math.min(path1, path2) + 1;
    }
    public static int minKnightMovesMemo2(int x, int y) {
        Map<String, Integer> map = new HashMap<>();
        return helper(map, Math.abs(x), Math.abs(y));
    }

    private static int helper(Map<String, Integer> map, int x, int y) {
        String key = x + "," + y;
        if (map.containsKey(key)) return map.get(key);
        if (x + y == 0 || x + y == 2) {
            map.put(key, x + y);
            return x + y;
        }
        int pathA = helper(map, Math.abs(x - 2), Math.abs(y - 1));
        int pathB = helper(map, Math.abs(x - 1), Math.abs(y - 2));
        int path = Math.min(pathA, pathB) + 1;
        map.put(key, path);
        return path;
    }

    public static void main(String[] args) {
        int x = 2, y = 1;
        System.out.println(minKnightMovesMemo2(x, y));
    }

    class Node{
        int x, y, dist;
        public Node(int x, int y, int dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }
    }
}
