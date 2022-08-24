package rippling.VO;

import java.util.*;

public class UniqueNumberOccurrences {
    // 1. regular 2. no additional space 3. data stream
    public boolean uniqueOccurrences(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : arr) {
            map.put(i, map.getOrDefault(i, 0) + 1);
        }

        Set<Integer> set = new HashSet<>();
        set.addAll(map.values());
        return map.size() == set.size();
    }

    // no additional space
    public static boolean uniqueOccurrences2(int[] arr) {
        Arrays.sort(arr);
        int i = 0, j = 0, n = arr.length;
        int index = 0;
        while (j < n) {
            while (j < n && arr[j] == arr[i]) {
                j++;
            }
            int count = j - i;
            arr[index++] = count;
            i = j;
        }
        while (index < n) {
            arr[index++] = 0;
        }
        Arrays.sort(arr);
        i = 0;
        while (i < n && arr[i] == 0) {
            i++;
        }
        for (; i < n - 1; i++) {
            if (arr[i] == arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int[] arr = {1,2,2,1,1,3};
        System.out.println(uniqueOccurrences2(arr));
    }
}
