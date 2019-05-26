package google;

import java.util.*;

public class AllSubArrays {
    public static class Node {
        String key;
        String[] vals;
        public Node(String key, String[] vals) {
            this.key = key;
            this.vals = vals;
        }
    }

    public List<String[]> findAllSubArrayPairs(List<Node> nodes) {
        List<String[]> result = new ArrayList<>();
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.vals.length - o1.vals.length;
            }
        });
        int n = nodes.size();
        for (int i = 0; i < n - 1; i++) {
            String[] a = nodes.get(i).vals;
            for (int j = i + 1; j < n; j++) {
                String[] b = nodes.get(j).vals;
                if (isSubArray(a, b)) {
                    String[] pair = new String[]{nodes.get(i).key, nodes.get(j).key};
                    result.add(pair);
                }
            }
        }
        return result;
    }

    private boolean isSubArray(String[] A, String[] B) {
        int n = A.length, m = B.length;
        int i = 0, j = 0;
        while (i < n && j < m) {
            if (A[i].equals(B[j])) {
                i++;
                j++;
                if (j == m) {
                    return true;
                }
            }
            else {
                i++;
                j = 0;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Node n1 = new Node("a", new String[]{"1", "2", "3"});
        Node n2 = new Node("b", new String[]{"2", "3"});
        Node n3 = new Node("c", new String[]{"2", "1"});
        Node n4 = new Node("d", new String[]{"2", "1", "4"});
        List<Node> nodes = Arrays.asList(n1, n2, n3, n4);
        AllSubArrays solver = new AllSubArrays();
        List<String[]> result = solver.findAllSubArrayPairs(nodes);
        for (String[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }
}
