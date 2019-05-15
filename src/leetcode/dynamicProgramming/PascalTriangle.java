package leetcode.dynamicProgramming;

import java.util.*;

public class PascalTriangle {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> result = new ArrayList<>();
        if (numRows == 0) {
            return result;
        }
        if (numRows == 1) {
            result.add(Arrays.asList(1));
            return result;
        }
        if (numRows == 2) {
            result.add(Arrays.asList(1));
            result.add(Arrays.asList(1, 1));
            return result;
        }
        result.add(Arrays.asList(1));
        result.add(Arrays.asList(1, 1));
        for (int i = 2; i < numRows; i++) {
            List<Integer> list = new ArrayList<>();
            List<Integer> last = result.get(i - 1);
            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i ) {
                    list.add(1);
                } else {
                    list.add(last.get(j - 1) + last.get(j));
                }
            }
            result.add(list);
        }

        return result;
    }

    public static void main(String[] args) {
        PascalTriangle solver = new PascalTriangle();
        System.out.println(solver.generate(5));
    }
}
