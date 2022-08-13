package databricks;

import java.util.*;

public class ConsumerRevenue {
// 有一个系统，里面记录着每个customer产生的revenue，要你实现3个API：
//1. insert(revenue): 一个新customer，产生了revenue，返回新customer的ID。customerID是自增ID，第一次insert是0，第二次是1，以此类推
//2. insert(revenue, referrerID): 现有ID为referrerID的customer refer了一个新customer，产生了revenue，返回新customer的ID。
//   这种情况下认为referrer也产生了revenue。比如说customer 0之前产生的revenue为20，他refer了新人，产生了40revenue，customer 0产生的revenue就变为60
//3. getKLowestRevenue(k, targetRevenue): 给定k和revenue，要求返回>给定revenue的k个最小revenue所对应的customer ID
//    followup 问 给你多一个参数表示 include 多少层的referred customer 的revenue， 让你返回。
//    我说用hashmap 存refer的关系 和原始 consumer revenue数据，然后recursive的相加。

    Map<Integer, Integer> customerMap;
    int id;
    public ConsumerRevenue() {
        this.customerMap = new HashMap<>();
        this.id = 0;
    }

    public int insert(int revenue) {
        customerMap.put(id, revenue);
        return id++;
    }

    public int insert(int revenue, int referrerID) {
        int newCustomer = insert(revenue);
        if (customerMap.containsKey(referrerID)) {
            customerMap.put(referrerID, customerMap.get(referrerID) + revenue);
        }
        return newCustomer;
    }

    public List<Integer> getKLowestRevenue(int k ,int targetRevenue) {
        List<Integer> result = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (int id : customerMap.keySet()) {
            if (customerMap.get(id) > targetRevenue) {
                ids.add(id);
            }
        }
        PriorityQueue<Node> pq = new PriorityQueue<>(k + 1, (a, b) -> (b.rev - a.rev));

        for (int id : ids) {
            pq.add(new Node(id, customerMap.get(id)));
            if (pq.size() > k) {
                pq.poll();
            }
        }
        while (pq.size() > 0) {
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
    }

    public static void main(String[] args) {
        ConsumerRevenue revenue = new ConsumerRevenue();
        revenue.insert(10); // 0 -> 10
        revenue.insert(20); // 1 -> 20
        revenue.insert(30, 1); // 2 -> 30 1 -> 50
        revenue.insert(10, 1); // 3 -> 10 1 -> 60
        revenue.insert(10, 2); // 4 -> 10 2 -> 40

        System.out.println(revenue.customerMap);
        System.out.println(revenue.getKLowestRevenue(3, 10));
    }
}
