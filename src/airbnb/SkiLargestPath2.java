package airbnb;

import org.omg.CORBA.INTERNAL;

import java.util.*;

public class SkiLargestPath2 {
    private class Place {
        int dist;
        String name;
        public Place (String name, int dist) {
            this.dist = dist;
            this.name = name;
        }
    }

    public int findLargestScorePath(List<String> points, List<String> edges) {
        int result = Integer.MIN_VALUE;
        Map<String, List<Place>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        buildGraph(edges, inDegree, graph);
        Map<String, Integer> pointsMap = new HashMap<>();
        buildPointsMap(points, pointsMap);
        Queue<Place> queue = new LinkedList<>();
        for (String name : inDegree.keySet()) {
            if (inDegree.get(name) == 0) {
                queue.add(new Place(name, 0));
            }
        }
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> path = new HashMap<>();
        String dest = "";
        while (!queue.isEmpty()) {
            Place cur = queue.poll();
            int curDist = cur.dist;
            String curName = cur.name;
            if (cur.dist > result) {
                result = curDist;
                dest = curName;
            }
            if (graph.containsKey(curName)) {
                for (Place next : graph.get(curName)) {
                    int nextDist = next.dist;
                    String nextName = next.name;
                    int newDist = 2 * pointsMap.get(nextName) + nextDist;
                    if (!distances.containsKey(nextName) || curDist + newDist > distances.get(nextName)) {
                        distances.put(nextName, curDist + newDist);
                        path.put(nextName, curName);
                    }
                    int val = inDegree.get(nextName) - 1;
                    inDegree.put(nextName, val);
                    if (val == 0) {
                        queue.add(new Place(nextName, distances.get(nextName)));
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        while (path.containsKey(dest)) {
            sb.append(dest + " ");
            dest = path.get(dest);
        }
        sb.append(dest);
        System.out.println(sb.reverse().toString());
        return result;
    }

    private void buildPointsMap(List<String> points, Map<String, Integer> pointsMap) {
        for (String point : points) {
            String[] arr = point.split(",");
            String name = arr[0];
            int val = Integer.parseInt(arr[1]);
            pointsMap.put(name, val);
        }
    }

    private void buildGraph(List<String> edges, Map<String, Integer> inDegree, Map<String, List<Place>> graph) {
        for (String edge : edges) {
            String[] arr = edge.split(",");
            String from = arr[0], to = arr[1];
            int dist = Integer.parseInt(arr[2]);
            if (!graph.containsKey(from)) {
                graph.put(from, new ArrayList<Place>());
            }
            graph.get(from).add(new Place(to, dist));
            if (!inDegree.containsKey(from)) {
                inDegree.put(from, 0);
            }
            if (!inDegree.containsKey(to)) {
                inDegree.put(to, 1);
            } else {
                inDegree.put(to, 1 + inDegree.get(to));
            }
        }
    }

    public static void main(String[] args) {
        List<String> points = Arrays.asList("A,5", "B,7", "C,6", "D,2", "E,4");
        List<String> edges = Arrays.asList("A,B,2", "A,C,3", "B,D,5", "B,E,6", "C,E,4", "D,E,4");
        SkiLargestPath2 solver = new SkiLargestPath2();
        int score = solver.findLargestScorePath(points, edges);
        System.out.println(score);
    }
}
