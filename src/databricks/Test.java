package databricks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Test {

    public static void main(String[] args) {
        Function<Integer, Integer> function = x -> x * 3;
        Function<Integer, Integer> function2 = x -> x * 2;
        List<Function<Integer, Integer>> list = new ArrayList<>();
        list.add(function);
        list.add(function2);
        int a = 5;
        for (Function<Integer, Integer> fuc : list) {
            a = fuc.apply(a);
        }
        System.out.println(a);
    }
}
