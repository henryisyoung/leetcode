package airbnb;

import java.util.*;

public class RoundPrices {
    public int[] roundUp(double[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        int n = arr.length;
        int[] result = new int[n];
        List<Node> list = new ArrayList<>();
        int tSum = 0;
        double oSum = 0.0;
        for (int i = 0; i < n; i++) {
            oSum += arr[i];
            int cur = (int) Math.floor(arr[i]);
            result[i] = cur;
            double rest = arr[i] - cur;
            tSum += cur;
            Node node = new Node(i, rest);
            list.add(node);
        }
        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o2.val > o1.val) {
                    return 1;
                } else if (o2.val < o1.val) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        int diff = (int) Math.round(oSum) - tSum;
        int index = 0;
        while (diff > 0) {
            Node node = list.get(index++);
            diff--;
            int pos = node.pos;
            result[pos]++;
        }
        return result;
    }

    private class Node {
        int pos;
        double val;
        public Node (int pos, double val) {
            this.pos = pos;
            this.val = val;
        }
    }

    public static void main(String[] args) {
        RoundPrices solver = new RoundPrices();
        double[] a = {1.2, 2.3, 3.4};
        double[] b = {1.2, 2.5, 3.6, 4.0};
        int[] result = solver.roundUp(a);
        int[] result2 = solver.roundUp(b);
        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(result2));
    }
}
