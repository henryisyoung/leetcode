package facebook;

import java.util.*;

public class _CampusBikes {
    public static int[] assignBikes(int[][] workers, int[][] bikes) {
        final TreeMap<Integer, PriorityQueue<int[]>> map = new TreeMap<>();
        for (int i = 0; i < workers.length; i++) {
            for (int j = 0; j < bikes.length; j++) {
                map.computeIfAbsent(Math.abs(workers[i][0] - bikes[j][0]) + Math.abs(workers[i][1] - bikes[j][1]),
                        k -> new PriorityQueue<>((o1, o2) -> {
                            if (o1[0] == o2[0]) return o1[1] - o2[1];
                            return o1[0] - o2[0];
                        })).add(new int[]{i, j});
            }
        }

        int[] response = new int[workers.length];
        final Set<Integer> taken = new HashSet<>();
        Arrays.fill(response, -1);
        int count = 0;
        while (map.size() != 0 && count < response.length) {
            PriorityQueue<int[]> queue = map.pollFirstEntry().getValue();
            System.out.println(Arrays.deepToString(queue.toArray()));
            while (!queue.isEmpty()) {
                int[] d = queue.poll();
                if (response[d[0]] == -1 && !taken.contains(d[1])) {
                    response[d[0]] = d[1];
                    taken.add(d[1]);
                    count++;
                }
            }
        }
        System.out.println(Arrays.toString(response));
        return response;
    }

    public static void main(String[] args) {
        int[][] workers = {{0,0},{1,1}}, bikes = {{2,3},{2,0},{0,2}};
//        System.out.println(assignBikes(workers, bikes));


        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        TreeSet<Integer> set = new TreeSet<>();
        Random random = new Random();
        for (int i = 10; i > 0; i--) {
            treeMap.put(i, i);
            set.add(random.nextInt(i));
        }

        for (int i : set) System.out.println(i);

        Map<Integer, PriorityQueue<int[]>> map = new HashMap<>();
        map.putIfAbsent(1, new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                if(a[0] == b[0]) return a[1] - b[1];
                return a[0] - b[0];
            }
        }));
     }
}
