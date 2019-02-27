package airbnb;

import java.util.*;

public class RoundPricesII {
    static class Node {
        double num;
        double diffToFloor;
        int index;
        public Node(double num, double diffToFloor, int index) {
            this.num = num;
            this.diffToFloor = diffToFloor;
            this.index = index;
        }
    }

    public static int[] roundUp(double[] input) {
        int n = input.length;
        double sum = 0;
        int sumFloor = 0;
        Node[] arr = new Node[n];
        for (int i = 0; i < n; i++) {
            sum += input[i];
            int floor = (int) input[i];
            sumFloor += floor;
            arr[i] = new Node(input[i], input[i] - floor, i);
        }
        Arrays.sort(arr, new Comparator<Node>(){
            public int compare(Node n1, Node n2) {
                return Double.compare(n2.diffToFloor, n1.diffToFloor);
            }
        });
        int sumRound = (int) Math.round(sum);
        int diff = sumRound - sumFloor;
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            int index = arr[i].index;
            if (i < diff) {
                result[index] = (int) arr[i].num + 1;
            } else {
                result[index] = (int) arr[i].num;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        RoundPricesII solver = new RoundPricesII();
        double[] a = {1.2, 2.3, 3.4};
        double[] b = {1.2, 2.5, 3.6, 4.0};
        int[] result = solver.roundUp(a);
        int[] result2 = solver.roundUp(b);
        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(result2));
    }
}
