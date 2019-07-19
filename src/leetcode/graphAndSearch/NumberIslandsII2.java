package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class NumberIslandsII2 {

    static class Point {
      int x;
      int y;
      Point() { x = 0; y = 0; }
      Point(int a, int b) { x = a; y = b; }
    }
    class Union{
        int[] father, size;
        int capacity;
        public Union(int n) {
            this.capacity = n;
            this.size = new int[n];
            this.father = new int[n];
            for (int i = 0; i < n; i++) {
                father[i] = i;
                size[i] = 1;
            }
        }
        public int find(int a) {
            while (a != father[a]) {
                a = father[a];
            }
            return a;
        }

        public void union(int a, int b) {
            int fa = find(a), fb = find(b);
            if (fa == fb) return;
            int faSize = size[fa], fbSize = size[fb];
            if (faSize > fbSize) {
                father[fb] = fa;
                size[fa] += fbSize;
            } else {
                father[fa] = fb;
                size[fb] += faSize;
            }
        }
    }
    public List<Integer> numIslands2(int n, int m, Point[] operators) {
        List<Integer> result = new ArrayList<>();
        if (m * n == 0 || operators == null || operators.length == 0) return result;
        Union union = new Union(m * n);
        int count = 0;
        int[][] table = new int[n][m];
        int[][] dirs = {{1,0},{-1,0},{0,-1},{0,1}};
        for (Point p : operators) {
            int r = p.x, c = p.y;
            if (table[r][c] == 1) {
                result.add(count);
                continue;
            }
            table[r][c] = 1;
            int a = r * m + c;
            count++;
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < m && table[nr][nc] == 1) {
                    int b = nr * m + nc;
                    int fa = union.find(a);
                    int fb = union.find(b);
                    if (fa != fb) {
                        count--;
                        union.union(a, b);
                    }
                }
            }
            result.add(count);
        }
        return result;
    }

    public static void main(String[] args) {
        Point[] operators = new Point[4];
        operators[0] = new Point(1,1);
        operators[1] = new Point(0,1);
        operators[2] = new Point(3,3);
        operators[3] = new Point(3,4);
        int n = 4, m = 5;
        NumberIslandsII2 solver = new NumberIslandsII2();
        List<Integer> result = solver.numIslands2(n,m,operators);
        System.out.println(result.toString());
    }
}
