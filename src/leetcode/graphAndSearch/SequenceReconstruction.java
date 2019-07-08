package leetcode.graphAndSearch;

import java.util.*;

public class SequenceReconstruction {
    public static boolean sequenceReconstruction(int[] org, int[][] seqs) {
        // write your code here
        if (seqs == null || seqs.length == 0 || seqs[0] == null || seqs[0].length == 0) return false;
        int n = org.length;
        int[] inDegree = new int[n + 1];
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int [] seq : seqs) {
            for (int i = 0; i < seq.length; i++) {
                if (i != 0) {
                    int from = seq[i - 1], to = seq[i];
                    inDegree[to]++;
                    if (!map.containsKey(from)) map.put(from, new ArrayList<>());
                    map.get(from).add(to);
                }
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == 0) queue.add(i);
        }
        while (!queue.isEmpty()) {
            if (queue.size() > 1) return false;
            int cur = queue.poll();
            list.add(cur);
            if (map.containsKey(cur)) {
                for (int next : map.get(cur)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) queue.add(next);
                }
            }
        }
        if (list.size() != org.length) return false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != org[i]) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int[] org = {4,1,5,2,6,3};
        int[][] seqs = {{5,2,6,3},{4,1,5,2}};
        System.out.println(sequenceReconstruction(org, seqs));
    }
}
