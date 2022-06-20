package facebook;

import java.util.*;

public class UniqueMul {
    public static List<Integer> unique(int[] arr) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        Arrays.sort(arr);
        findAll(set, 1, 0, arr, 0);
        result.addAll(set);
        return result;
    }

    private static void findAll(Set<Integer> set, int cur, int pos, int[] arr, int count) {
        if (count != 0) {
            set.add(cur);
        }

        for (int i = pos; i < arr.length; i++) {
            if (i != pos && arr[i] == arr[i - 1]) continue;
            findAll(set, cur * arr[i], i + 1, arr, count + 1);
        }
    }


    public static void main(String[] args) {
        List<Integer> result = unique(new int[]{2,3,7});
        System.out.println(result.toString());
    }

}
