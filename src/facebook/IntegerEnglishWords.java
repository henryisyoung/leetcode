package facebook;

import java.util.HashMap;
import java.util.Map;

public class IntegerEnglishWords {
    public static String numberToWords(int num) {
        Map<Integer, String> map = fillMap();
        StringBuilder sb = new StringBuilder();
        if (num == 0) return map.get(num);

        if (num >= 1000000000) {
            int extra = num/1000000000;
            sb.append(convert(extra, map) + " " + "Billion");
            num %= 1000000000;
        }

        if (num >= 1000000) {
            int extra = num/1000000;
            sb.append(convert(extra, map) + " " + "Million");
            num %= 1000000;
        }

        if (num >= 1000) {
            int extra = num/1000;
            sb.append(convert(extra, map) + " " + "Thousand");
            num %= 1000;
        }

        if (num > 0) {
            sb.append(convert(num, map));
        }

        return sb.toString().trim();
    }

    private static String convert(int val, Map<Integer, String> map) {
        StringBuilder sb = new StringBuilder();
        if (val >= 100) {
            int n = val / 100;
            sb.append(" " + map.get(n) + " " + "Hundred");
            val %= 100;
        }
        if (val > 10) {
            if (map.containsKey(val)) {
                sb.append(" " + map.get(val));
                return sb.toString();
            } else {
                int n = val / 10;
                sb.append(" " + map.get(n * 10) );
                val %= 10;
            }
        }

        if (val > 0) {
            sb.append(" " + map.get(val));
        }

        return sb.toString();
    }

    public static Map<Integer, String> fillMap(){
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Zero");
        map.put(1, "One");
        map.put(2, "Two");
        map.put(3, "Three");
        map.put(4, "Four");
        map.put(5, "Five");
        map.put(6, "Six");
        map.put(7, "Seven");
        map.put(8, "Eight");
        map.put(9, "Nine");
        map.put(10, "Ten");
        map.put(11, "Eleven");
        map.put(12, "Twelve");
        map.put(13, "Thirteen");
        map.put(14, "Fourteen");
        map.put(15, "Fifteen");
        map.put(16, "Sixteen");
        map.put(17, "Seventeen");
        map.put(18, "Eighteen");
        map.put(19, "Nineteen");
        map.put(20, "Twenty");
        map.put(30, "Thirty");
        map.put(40, "Forty");
        map.put(50, "Fifty");
        map.put(60, "Sixty");
        map.put(70, "Seventy");
        map.put(80, "Eighty");
        map.put(90, "Ninety");
        return map;
    }

    public static void main(String[] args) {
        int num = 123;
        int num2 = 1234567891;
        System.out.println(numberToWords(num));
        System.out.println(numberToWords(num2));
    }
}
