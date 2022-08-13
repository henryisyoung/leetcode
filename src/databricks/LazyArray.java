package databricks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LazyArray {
    List<Function<Integer, Integer>> list = new ArrayList<>();
    int[] nums;
    public LazyArray(int[] nums) {
        this.nums = nums;
    }

    public LazyArray map(Function<Integer, Integer> function) {
        list.add(function);
        return this;
    }

    public int indexOf(int num) {
        for (int i = 0; i < nums.length; i++) {
            int val = nums[i];
            for (Function<Integer, Integer> function : list) {
                val = function.apply(val);
            }
            if (val == num) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        LazyArray array = new LazyArray(new int[]{1,2,4,6});
        Function<Integer, Integer> f1 = x -> x * 3;
        Function<Integer, Integer> f2 = x -> x * 2;
        System.out.println(array.map(f1).map(f2).indexOf(36));
    }
}
