package airbnb;

import java.util.*;

public class SkiLargestPath {
    private class Place {
        int num;
        String name;
        public Place (String name, int num) {
            this.num = num;
            this.name = name;
        }
    }

    public int findLargestScorePath(List<String> points, List<String> edges) {
        int maxPath  = Integer.MIN_VALUE;
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> paths = new HashMap<>();
        Map<String, Integer> pointMap = new HashMap<>();
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        
        constructGraph(graph, inDegree, edges);
        getPoints(points, pointMap);

//        for (String key : inDegree.keySet()) {
//            System.out.println("key " + key + " indegree " + inDegree.get(key));
//        }

//        for (String key : pointMap.keySet()) {
//            System.out.println("key " + key + " point " + pointMap.get(key));
//        }

//        for (String key : graph.keySet()) {
//            Map<String, Integer> neigb = graph.get(key);
//            for (String nei : neigb.keySet()) {
//                System.out.println("from " + key + " to " + nei + " dist " + neigb.get(nei));
//            }
//        }

        Queue<Place> queue = new LinkedList<>();
        for (String name : inDegree.keySet()) {
            if (inDegree.get(name) == 0) {
                queue.add(new Place(name, 0));
            }
        }
        String dest = "";
        while (!queue.isEmpty()) {
            Place cur = queue.poll();
            String curPoint = cur.name;
            int curDist = cur.num;

            if (maxPath < curDist) {
                maxPath = curDist;
                dest = curPoint;
            }
            Map<String, Integer> ngbrs = graph.get(curPoint);
            if (ngbrs == null) {
                continue;
            }
            for (String ngbrNode : ngbrs.keySet()) {
                int ngbrDist = ngbrs.get(ngbrNode);
                int dist = 2 * pointMap.get(ngbrNode) + ngbrDist;
                if (!distances.containsKey(ngbrNode) || curDist + dist > distances.get(ngbrNode)) {
                    distances.put(ngbrNode, curDist + dist);
                    paths.put(ngbrNode, curPoint);
                }

                if (inDegree.get(ngbrNode) > 0) {
                    inDegree.put(ngbrNode, inDegree.get(ngbrNode) - 1);
                    if (inDegree.get(ngbrNode) == 0) {
                        queue.add(new Place(ngbrNode, distances.get(ngbrNode)));
                    }
                }
            }
        }
        StringBuilder path = new StringBuilder();
        String itrPoint = dest;

        while (paths.containsKey(itrPoint)) {
            path.append(itrPoint + " ");
            itrPoint = paths.get(itrPoint);
        }
        path.append(itrPoint);

        System.out.println(path.reverse().toString());

        return maxPath;
    }

    private void getPoints(List<String> points, Map<String, Integer> pointMap) {
        for (String point : points) {
            String[] arr = point.split(",");
            pointMap.put(arr[0], Integer.parseInt(arr[1]));
        }
    }

    private void constructGraph(Map<String, Map<String, Integer>> graph, Map<String, Integer> inDegree, List<String> edges) {
        for (String edge : edges) {
            String[] arr = edge.split(",");

            int dist = Integer.parseInt(arr[2]);

            if (!graph.containsKey(arr[0])) {
                Map<String, Integer> next = new HashMap<>();
                next.put(arr[1], dist);
                graph.put(arr[0], next);
            } else {
                Map<String, Integer> next = graph.get(arr[0]);
                next.put(arr[1], dist);
                graph.put(arr[0], next);
            }
            if (!inDegree.containsKey(arr[0])) {
                inDegree.put(arr[0], 0);
            }
            if (!inDegree.containsKey(arr[1])) {
                inDegree.put(arr[1], 1);
            } else {
                inDegree.put(arr[1], inDegree.get(arr[1]) + 1);
            }
        }
    }

    public static void main(String[] args) {
        List<String> points = Arrays.asList("A,5", "B,7", "C,6", "D,2", "E,4");
        List<String> edges = Arrays.asList("A,B,2", "A,C,3", "B,D,5", "B,E,6", "C,E,4", "D,E,4");
        SkiLargestPath solver = new SkiLargestPath();
        int score = solver.findLargestScorePath(points, edges);
        System.out.println(score);
    }
}
