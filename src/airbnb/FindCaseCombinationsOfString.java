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
        List<String> result = new ArrayList<>();
        if (input == null || input.length() == 0) {
            return result;
        }
        input = input.toLowerCase();
        int num = 1 << input.length();
        for (int i = 0; i < num; i++) {
            char[] chars = input.toCharArray();
            for (int j = 0; j < input.length(); j++) {
                if (((i >> j) & 1) == 1) {
                    chars[j] = Character.toUpperCase(input.charAt(j));
                }
            }
            result.add(new String(chars));
        }
        return result;
    }

    public static void main(String[] args) {
        String input = "aklkjfg";
        List<String> list = permute(input);
        List<String> list2 = ermutateString(input);
        System.out.println(list2.toString());
    }
}
