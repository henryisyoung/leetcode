package facebook;

import java.util.HashMap;
import java.util.Map;

public class EnglishWordsInteger {
    public static int numberToWords(String s) {
        int val = 0;
        if (s.indexOf("Billion") != -1) {
            int pos = s.indexOf("Billion");
            val += convert(s.substring(0, pos).trim()) * 1000000000;
            s = s.substring(pos + 7).trim();
        }

        if (s.indexOf("Million") != -1) {
            int pos = s.indexOf("Million");
            val += convert(s.substring(0, pos).trim()) * 1000000;
            s = s.substring(pos + 7).trim();
        }

        if (s.indexOf("Thousand") != -1) {
            int pos = s.indexOf("Thousand");
            val += convert(s.substring(0, pos).trim()) * 1000000;
            s = s.substring(pos + 8).trim();
        }
        if (s.length() > 0) {
            val += convert(s);
        }
        return val;
    }

    private static int convert(String s) {
        Map<String, Integer> map = new HashMap<>();
        map.put("Zero", 0);
        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3);
        map.put("Four", 4);
        map.put("Five", 5);
        map.put("Six", 6);
        map.put("Seven", 7);
        map.put("Eight", 8);
        map.put("Nine", 9);
        map.put("Ten", 10);
        map.put("Eleven", 11);
        map.put("Twelve", 12);
        map.put("Thirteen", 13);
        map.put("Fourteen", 14);
        map.put("Fifteen", 15);
        map.put("Sixteen", 16);
        map.put("Seventeen", 17);
        map.put("Eighteen", 18);
        map.put("Nineteen", 19);
        map.put("Twenty", 20);
        map.put("Thirty", 30);
        map.put("Forty", 40);
        map.put("Fifty", 50);
        map.put("Sixty", 60);
        map.put("Seventy", 70);
        map.put("Eighty", 80);
        map.put("Ninety", 90);
        int val = 0;
        if(s.indexOf("Hundred") != -1) {
            int pos = s.indexOf("Hundred");
            val += map.get(s.substring(0, pos).trim()) * 100;
            s = s.substring(pos + 7).trim();
        }
        if (s.length() > 0) {
            if (map.containsKey(s)) {
                val += map.get(s);
                return val;
            }
            int pos = s.indexOf(" ");
            val += map.get(s.substring(0, pos).trim());
            s = s.substring(pos).trim();
            if (map.containsKey(s)) {
                val += map.get(s);
                return val;
            }
        }
        return val;
    }

    public static void main(String[] args) {
        int num = 123;
        int num2 = 1234567891;
        String s = "One Billion Five Million Sixty Seven";
        String s2 = "Seven";
        System.out.println(numberToWords(s));
        System.out.println(numberToWords(s2));
    }
}
