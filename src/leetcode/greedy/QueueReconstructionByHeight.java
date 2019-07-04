package leetcode.greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class QueueReconstructionByHeight {
    public static int[][] reconstructQueue(int[][] people) {
        if (people == null || people.length == 0 || people[0] == null || people[0].length == 0) {
            return people;
        }
        int n = people.length;
        List<int[]> result = new ArrayList<>();
        Arrays.sort(people, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        if (o1[0] != o2[0]) {
                            return o2[0] - o1[0];
                        } else {
                            return o1[1] - o2[1];
                        }
                    }
                }
        );
        for (int[] p : people) {
            result.add(p[1], p);
        }
        int[][] array = new int[n][2];
        for (int i = 0; i < n; i++) {
            array[i] = result.get(i);
        }
        return array;
    }

    public static void main(String[] args) {
        int[][] people = {{7,0}, {4,4}, {7,1}, {5,0}, {6,1}, {5,2}};
        int[][] result  = reconstructQueue(people);
        System.out.println(Arrays.deepToString(result));
    }
}
