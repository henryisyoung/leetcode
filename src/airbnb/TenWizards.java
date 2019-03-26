package airbnb;

import java.util.*;

public class TenWizards {
    private static class Route {
        int fromWizard, wizard;
        int dist;
        public Route (int fromWizard, int wizard, int dist) {
            this.fromWizard = fromWizard;
            this.wizard = wizard;
            this.dist = dist;
        }
    }

    public static List<Integer> getShortestPath(List<List<Integer>> wizards, int source, int target) {
        List<Integer> path = new ArrayList<>();
        if (wizards == null || wizards.size() == 0) {
            return path;
        }
        int n = wizards.size();
        PriorityQueue<Route> pq = new PriorityQueue<>(n, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.dist - o2.dist;
            }
        });
        Route[] table = new Route[n];

        for (Integer wizard : wizards.get(source)) {
            pq.add(new Route(source, wizard, (int) Math.pow(wizard - source, 2)));
        }

        while (!pq.isEmpty()) {
            Route cur = pq.poll();
            if (table[cur.wizard] == null) {
                table[cur.wizard] = cur;
                if (cur.wizard == target) {
                    System.out.println("cur dist " + cur.dist);
                    buildPath(table, target, source, path);
                    return path;
                }
                for (int next : wizards.get(cur.wizard)) {
                    pq.add(new Route(cur.wizard, next,  cur.dist + (int) Math.pow(next - cur.wizard, 2)));
                }
            }
        }
        return path;
    }

    private static void buildPath(Route[] from, int target, int source, List<Integer> path) {
        while (source != target) {
            path.add(target);
            target = from[target].fromWizard;
        }
        path.add(source);
        Collections.reverse(path);
    }

    public static void main(String[] args) {
        List<List<Integer>> wizards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Integer> list = new ArrayList<>();
            if (i == 0) {
                list.add(1);
                list.add(2);
            } else if (i == 1) {
                list.add(3);
            } else if (i == 2) {
                list.add(3);
                list.add(4);
            } else if (i == 3) {
                list.add(4);
            }
            wizards.add(list);
        }
        System.out.println("path " + getShortestPath(wizards, 0, 4));
    }
}
