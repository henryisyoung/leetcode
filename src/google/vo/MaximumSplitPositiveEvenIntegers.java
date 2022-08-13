package google.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MaximumSplitPositiveEvenIntegers {
    public static List<Long> maximumEvenSplit(long finalSum) {
        List<Long> result = new ArrayList<>();
        if (finalSum % 2 != 0 || finalSum < 2) {
            return result;
        }
        dfsFindAll(finalSum, 2, new HashSet<>(), result);
        return result;
    }

    private static void dfsFindAll(long finalSum, long cur, HashSet<Long> set, List<Long> result) {
        if (finalSum == 0) {
            System.out.println(set);
            if (set.size() > result.size()) {
                result.clear();
                result.addAll(new HashSet<>(set));
            }
            return;
        }
        for (long val = cur; val <= finalSum; val += 2) {
            if (set.contains(val)) {
                continue;
            }
            set.add(val);
            dfsFindAll(finalSum - val, val, set, result);
            set.remove(val);
        }
    }

    public static void main(String[] args) {
        System.out.println(maximumEvenSplit(12));
    }
}
