package uber.phone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ShuffleArray {
    public static int[] shuffle(int[] array) {
        int n = array.length;
        Random random = new Random();
        for (int i = n - 1; i > 0; i--) {
            int pos = random.nextInt(i + 1);
            int tmp = array[i];
            array[i] = array[pos];
            array[pos] = tmp;
        }
        return array;
    }

    public static int[] shuffleLarge(int m, int n) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = i;
        }
        Random random = new Random();
        for (int i = n - 1; i >= n - m; i--) {
            int pos = random.nextInt(i);
            int tmp = result[pos];
            result[pos] = result[i];
            result[i] = tmp;
        }
        int[] array = new int[m];
        for (int i = 0; i < m; i++) {
            array[i] = result[n - 1 - i];
        }
        return array;
    }

    public static int[] shuffleLargeOpt(int m, int n) {
        Map<Integer, Integer> map = new HashMap<>();
        Random random = new Random();
        for (int i = n - 1; i >= n - m; i--) {
            int pos = random.nextInt(i);
            int tmp = map.getOrDefault(pos, pos);
            map.put(pos, map.getOrDefault(i, i));
            map.put(i, tmp);
        }
        int[] result = new int[m];
        for (int i = 0; i < m; i++) {
            result[i] = map.get(n - 1 - i);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] array = {2,1,3,4,5};
        System.out.println(Arrays.toString(shuffle(array)));

        System.out.println(Arrays.toString(shuffleLarge(3,100)));
        System.out.println(Arrays.toString(shuffleLargeOpt(3,100)));
    }
}
