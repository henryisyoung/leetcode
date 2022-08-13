package databricks;

import java.util.*;

public class ConsumerRevenueLevel {
// 有一个系统，里面记录着每个customer产生的revenue，要你实现3个API：
//1. insert(revenue): 一个新customer，产生了revenue，返回新customer的ID。customerID是自增ID，第一次insert是0，第二次是1，以此类推
//2. insert(revenue, referrerID): 现有ID为referrerID的customer refer了一个新customer，产生了revenue，返回新customer的ID。
//   这种情况下认为referrer也产生了revenue。比如说customer 0之前产生的revenue为20，他refer了新人，产生了40revenue，customer 0产生的revenue就变为60
//3. getKLowestRevenue(k, targetRevenue): 给定k和revenue，要求返回>给定revenue的k个最小revenue所对应的customer ID
//    followup 问 给你多一个参数表示 include 多少层的referred customer 的revenue， 让你返回。
//    我说用hashmap 存refer的关系 和原始 consumer revenue数据，然后recursive的相加。

    Map<Integer, Integer> map;
    int id;
    Map<Integer, Integer> graph;
    public ConsumerRevenueLevel() {
        this.map = new HashMap<>();
        this.id = 0;
        this.graph = new HashMap<>();
    }

    public int insert(int revenue) {
        map.put(id, revenue);
        return id++;
    }

    public int insert(int revenue, int referrerID, int lvl) {
        int newId = insert(revenue);
        graph.put(newId, referrerID);
        dfsUpdateRev(graph, referrerID, lvl, revenue);
        return newId;
    }

    private void dfsUpdateRev(Map<Integer, Integer> graph, int curId, int lvl, int revenue) {
        if (lvl == 0) {
            return;
        }
        if (map.containsKey(curId)) {
            map.put(curId, map.get(curId) + revenue);
        }
        if (graph.containsKey(curId)) {
            dfsUpdateRev(graph, graph.get(curId), lvl - 1, revenue);
        }
    }


    public List<Integer> getKLowestRevenue(int k ,int targetRevenue) {
       PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (b.rev - a.rev));
       for (int id : map.keySet()) {
           if (map.get(id) > targetRevenue) {
               pq.add(new Node(id, map.get(id)));
           }
           if (pq.size() > k) {
               pq.poll();
           }
       }
        List<Integer> result = new ArrayList<>();
       while (!pq.isEmpty()) {
           result.add(pq.poll().id);
       }
       return result;
    }

    class Node{
        int id, rev;
        public Node(int id, int rev) {
            this.rev = rev;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return id == node.id && rev == node.rev;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, rev);
        }
    }

    public static void main(String[] args) {
        ConsumerRevenueLevel revenue = new ConsumerRevenueLevel();
        revenue.insert(10); // 0 -> 10
        revenue.insert(20); // 1 -> 20
        revenue.insert(30, 1, 2); // 2 -> 30 1 -> 50
        revenue.insert(10, 1, 3); // 3 -> 10 1 -> 60
        revenue.insert(10, 2, 2); // 4 -> 10 2 -> 40

        /*
        0               10
        1               20 + 30 + 10 + 10 = 70
        2 -> 1          30 + 10 = 40
        3 -> 1          10
        4 -> 2 -> 1     10

         */
        System.out.println(revenue.map);
        System.out.println(revenue.getKLowestRevenue(1, 10));
    }
}
