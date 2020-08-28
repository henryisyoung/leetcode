package apple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SmallestRangeCoveringElementsfromKLists {
    public int[] smallestRange(List<List<Integer>> nums) {
        int[] result = new int[2];
        if (nums == null || nums.size() == 0) return result;
        int k = nums.size();
        int[] table = new int[k];
        int left = 0, right = 0;
        int min = Integer.MAX_VALUE;
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nums.size(); i++) {
            List<Integer> num = nums.get(i);
            for (int n : num) {
                list.add(new Node(i, n));
            }
        }
        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.val - o2.val;
            }
        });
        while (right < list.size()) {
            while (right < list.size() && !validTable(table)) {
                table[list.get(right++).pos]++;
            }
            if (validTable(table)) {
                if (min > list.get(right - 1).val - list.get(left).val) {
                    min = list.get(right - 1).val - list.get(left).val;
                    result[0] = list.get(left).val;
                    result[1] = list.get(right - 1).val;
                }
            }
            table[list.get(right++).pos]--;
        }
        return result;
    }

    private boolean validTable(int[] table) {
        for (int i = 0; i < table.length; i++) {
            if (table[i] == 0) return false;
        }
        return true;
    }

    class Node{
        int pos, val;
        public Node(int pos, int val) {
            this.pos = pos;
            this.val = val;
        }
    }

    public static void main(String[] args) {

    }
}
