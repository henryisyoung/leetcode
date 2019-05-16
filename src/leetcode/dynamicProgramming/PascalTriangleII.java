package leetcode.dynamicProgramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PascalTriangleII {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> result = new ArrayList<>();
        if (rowIndex == 0) {
            return result;
        }
        if (rowIndex == 1) {
            result.add(1);
            return result;
        }
        if (rowIndex == 2) {
            result.addAll(Arrays.asList(1, 1));
            return result;
        }
        List<Integer> last = getRow(rowIndex - 1);
        for (int i = 0; i < rowIndex; i++) {
            if (i == 0 || i == rowIndex - 1) {
                result.add(1);
            } else {
                result.add(last.get(i - 1) + last.get(i));
            }
        }
        return result;
    }

    public static void main(String[] args) {
        PascalTriangleII solver = new PascalTriangleII();
        System.out.println(solver.getRow(5));
    }
}
