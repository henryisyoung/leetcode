package google.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

class StockPrice {
    class Node{
        int timestamp, price;
        public Node(int timestamp, int price) {
            this.price = price;
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (! (o instanceof Node)) {
                return false;
            }
            return ((Node) o).price == this.price && ((Node) o).timestamp == this.timestamp;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timestamp, price);
        }
    }

    Map<Integer, Integer> map;
    int lastT, lastP;
    PriorityQueue<Node> max, min;
    public StockPrice() {
        this.map = new HashMap<>();
        this.lastP = 0;
        this.lastT = 0;
        this.max = new PriorityQueue<>((a, b) -> (b.price - a.price));
        this.min = new PriorityQueue<>((a, b) -> (a.price - b.price));

    }

    public void update(int timestamp, int price) {
        if (map.containsKey(timestamp)) {
            int prevP = map.get(timestamp);
            max.remove(new Node(timestamp, prevP));
            min.remove(new Node(timestamp, prevP));
        }
        map.put(timestamp, price);
        max.add(new Node(timestamp, price));
        min.add(new Node(timestamp, price));
        if (lastT <= timestamp) {
            lastP = price;
            lastT = timestamp;
        }
    }

    public int current() {
        return lastP;
    }

    public int maximum() {
        return max.peek().price;
    }

    public int minimum() {
        return min.peek().price;
    }
}