package airbnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindCaseCombinationsOfString {
    public static List<String> ermutateString(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.length() == 0) {
            return result;
        }
        text = text.toLowerCase();
        int n = text.length();
        int count = 1 << n;
        for (int i = 0; i < count; i++) {
            char[] arr = text.toCharArray();
            for (int j = 0; j < n; j++) {
                if (((i >> j) & 1) == 1) {
                    arr[j] = Character.toUpperCase(arr[j]);
                }
            }
            result.add(new String(arr));
        }
        return result;
    }

    public static List<String> permute(String input) {
        int n = input.length();
        List<String> result = new ArrayList<>();

        // Number of permutations is 2^n
        int max = 1 << n;

        // Converting string to lower case
        input = input.toLowerCase();

        // Using all subsequences and permuting them
        for(int i = 0;i < max; i++) {
            char combination[] = input.toCharArray();

            // If j-th bit is set, we convert it to upper case
            for(int j = 0; j < n; j++) {
                if(((i >> j) &  1) == 1) {
                    combination[j] = Character.toUpperCase(combination[j]);
                }
            }

            result.add(new String(combination));
        }
        return result;
    }

    public static void main(String[] args) {
        String input = "abc";
        List<String> list = permute(input);
        List<String> list2 = ermutateString(input);
        System.out.println(list2.toString());
    }
}
