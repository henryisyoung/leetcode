package apple;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class KMostRecentId {
    static class Node {
        int index, id;
        long timestamp;
        public Node(int index, int id, long timestamp) {
            this.id = id;
            this.index = index;
            this.timestamp = timestamp;
        }
    }
    public static List<Integer> findKIds(int[][] events, int k) {
        List<Integer> result = new ArrayList<>();
        if (events == null || events.length == 0) return result;
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) ->{
           if (a.timestamp != b.timestamp) {
               return Long.compare(a.timestamp, b.timestamp);
           }
           return a.index - b.index;
        });

        for (int i = 0; i < events.length; i++) {
            pq.add(new Node(i, events[i][0], events[i][1]));
            if (pq.size() > k) pq.poll();
        }
        while (!pq.isEmpty()) {
            result.add(0, pq.poll().id);
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] events = {
                {1,400},
                {3,200},
                {1,300},
                {2,100},
                {3,100},
                {5,800},
                {3,500},
        };
        System.out.println(findKIds(events, 2));
    }
}
