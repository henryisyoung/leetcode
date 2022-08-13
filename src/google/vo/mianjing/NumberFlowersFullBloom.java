package google.vo.mianjing;

import java.util.*;

public class NumberFlowersFullBloom {
    static class Node{
        int pos;
        int type;
        int index;
        public Node(int pos, int type, int index) {
            this.type = type;
            this.pos = pos;
            this.index = index;
        }

    }
    public static int[] fullBloomFlowers(int[][] flowers, int[] persons) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i <  persons.length; i++) {
            int p = persons[i];
            nodes.add(new Node(p, 1, i));
        }
        for (int[] f : flowers) {
            nodes.add(new Node(f[0], 0, -1));
            nodes.add(new Node(f[1], 2, -1));
        }

        Collections.sort(nodes, (a,b) -> {
            if (a.pos != b.pos) {
                return a.pos - b.pos;
            }

            return a.type - b.type;
        });
        int count = 0;

        int n = persons.length;
        int[] result = new int[n];
        for (Node node : nodes) {
            if (node.type == 0) {
                count++;
            } else if (node.type == 2) {
                count--;
            } else {
                int pIndex = node.index;
                result[pIndex] = count;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] flowers = {{0,7},{2,5}, {18,19}};
        int[] p = {1, 2, 4, 7,11,18,19,21 };
        System.out.println(Arrays.toString(fullBloomFlowers(flowers, p)));
    }
}
