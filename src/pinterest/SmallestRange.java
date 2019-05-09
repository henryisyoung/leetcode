package pinterest;

import java.util.*;

public class SmallestRange {
    class Node {
        int val, pos;

        public Node(int val, int pos) {
            this.pos = pos;
            this.val = val;
        }

        @Override
        public String toString() {
            return val + "";
        }
    }

    public int[] smallestRange(List<List<Integer>> nums) {
        if (nums == null || nums.size() == 0) {
            return new int[0];
        }
        int[] result = new int[2];
        int n = nums.size();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> list = nums.get(i);
            if (list == null || list.size() == 0) {
                return new int[0];
            }
            for (int val : list) {
                nodes.add(new Node(val, i));
            }
        }
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.val - o2.val;
            }
        });
//        System.out.println(nodes.toString());
        int[] table = new int[n];
        int j = 0, min = Integer.MAX_VALUE;
        for (int i = 0; i < nodes.size(); i++) {
            while (j < nodes.size() && !isValid(table)) {
                Node cur = nodes.get(j++);
                table[cur.pos]++;
            }
            if (isValid(table)) {
                int curA = nodes.get(i).val, curB = nodes.get(j - 1).val;
                if (curB - curA < min) {
                    min = curB - curA;
                    result[0] = curA;
                    result[1] = curB;
                }
            }
            Node remove = nodes.get(i);
            table[remove.pos]--;
        }
        return result;
    }

    private boolean isValid(int[] table) {
        for (int i : table) {
            if (i == 0) {
                return false;
            }
        }
        return true;
    }
    public int[] smallestRangePQ(List<List<Integer>> nums) {
        if (nums == null || nums.size() == 0) {
            return new int[0];
        }
        int[] result = new int[2];
        int n = nums.size();
        int[] pos = new int[n];
        PriorityQueue<Node> pq = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.val - o2.val;
            }
        });
        int right = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            pos[i]++;
            Node node = new Node(nums.get(i).get(0), i);
            pq.add(node);
            right = Math.max(right, nums.get(i).get(0));
        }
        result[0] = pq.peek().val;
        result[1] = right;

        while (pos[pq.peek().pos] < nums.get(pq.peek().pos).size()) {
            Node cur = pq.poll();
            int t = cur.pos;
            int index = pos[t];
            Node next = new Node(nums.get(t).get(index), t);
            right = Math.max(right, nums.get(t).get(index));
            pos[t]++;
            pq.add(next);
            if (result[1] - result[0] > right - pq.peek().val) {
                result[1] = right;
                result[0] = pq.peek().val;
            }
        }
        return result;
    }
    public static void main(String[] args) {
        SmallestRange sovler = new SmallestRange();
        List<Integer> l1 = Arrays.asList(4,10,15,24,26), l2 = Arrays.asList(0,9,12,20), l3 = Arrays.asList(5,18,22,30);
        List<List<Integer>> nums = new ArrayList<>();
        nums.add(l1);
        nums.add(l2);
        nums.add(l3);
        int[] result  = sovler.smallestRange(nums);
        int[] result2  = sovler.smallestRangePQ(nums);
        System.out.println(Arrays.toString(result2));
    }
}
