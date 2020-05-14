package facebook;

import java.util.Arrays;

public class _MaximumSwap {
    public static int maximumSwap(int num) {
        char[] arr = Integer.toString(num).toCharArray();
        for (int i = 0; i < arr.length - 1; i++) {
            int val = arr[i] - '0';
            int max = 0, index = -1;
            for (int j = i + 1; j < arr.length; j++) {
                int val2 = arr[j] - '0';
                if (val2 > val && val2 >= max) {
                    max = val2;
                    index = j;
                }
            }
            if (index != -1) {
                char temp = arr[i];
                arr[i] = arr[index];
                arr[index] = temp;
                return Integer.parseInt(new String(arr));
            }
        }
        return Integer.parseInt(new String(arr));
    }

    public static int maximumSwap2(int num) {
        char[] arr = Integer.toString(num).toCharArray();
        int[] bucket = new int[10];
        Arrays.fill(bucket, -1);
        for (int i = 0; i < arr.length; i++) {
            int index = arr[i] - '0';
            bucket[index] = i;
        }
        int index = 0;
        for (int i = 9; i >= 0; i--) {
            if (bucket[i] == -1) {
                continue;
            }
            if (bucket[i] > index) {
                char temp = arr[index];
                arr[index] = arr[bucket[i]];
                arr[bucket[i]] = temp;
                return Integer.parseInt(new String(arr));
            } else {
                index++;
            }
        }
        return Integer.parseInt(new String(arr));
    }

    public static void main(String[] args) {
        int num = 2736;
        int num2 = 9973;
        System.out.println(maximumSwap(num));
        System.out.println(maximumSwap2(num));
        System.out.println(maximumSwap(num2));
        System.out.println(maximumSwap2(num2));
    }
}
