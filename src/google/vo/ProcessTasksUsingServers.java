package google.vo;

import java.util.Arrays;
import java.util.PriorityQueue;

public class ProcessTasksUsingServers {
    public static int[] assignTasks(int[] servers, int[] tasks) {
        PriorityQueue<Server> pq = new PriorityQueue<>((a, b) -> {
            if (a.weight != b.weight) {
                return a.weight - b.weight;
            }
            return a.id - b.id;
        });

        PriorityQueue<Server> usedServer = new PriorityQueue<>((a, b) -> {
            if (a.timeStamp != b.timeStamp) {
                return a.timeStamp - b.timeStamp;
            }
            if (a.weight != b.weight) {
                return a.weight - b.weight;
            }
            return a.id - b.id;
        });

        for (int i = 0; i < servers.length; i++) {
            pq.add(new Server(i, servers[i], 0));
        }
        int[] result = new int[tasks.length];

        for (int cur = 0; cur < tasks.length; cur++) {
            while (!usedServer.isEmpty() && usedServer.peek().timeStamp <= cur) {
                pq.add(usedServer.poll());
            }
            if (!pq.isEmpty()) {
                Server server = pq.poll();
                result[cur] = server.id;
                server.timeStamp = cur + tasks[cur];
                usedServer.add(server);
            } else {
                Server server = usedServer.poll();
                result[cur] = server.id;
                server.timeStamp += tasks[cur];
                usedServer.add(server);
            }

        }
        return result;
    }

    static class Server{
        int id, weight, timeStamp;
        public Server(int id, int weight, int timeStamp) {
            this.id = id;
            this.weight = weight;
            this.timeStamp = timeStamp;
        }
    }

    public static void main(String[] args) {
        int[] servers = {3,3,2}, tasks = {1,2,3,2,1,2};
        System.out.println(Arrays.toString(assignTasks(servers, tasks)));
    }
}
